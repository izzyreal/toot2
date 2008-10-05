package uk.org.toot.synth.channels.pluckedString2;

import uk.org.toot.audio.core.AudioBuffer;
//import uk.org.toot.audio.core.FloatDenormals;
import uk.org.toot.dsp.jSTK.Delay;
import uk.org.toot.dsp.jSTK.DelayA;
import uk.org.toot.dsp.jSTK.Noise;
import uk.org.toot.dsp.jSTK.OnePole;
import uk.org.toot.dsp.jSTK.OneZero;
import uk.org.toot.synth.PolyphonicSynthChannel;

/**
 * from pluck.c - elementary waveguide simulation of plucked strings - JOS 6/6/92
 * @author st
 */
public class PluckedString2SynthChannel extends PolyphonicSynthChannel
{
	private PluckedString2SynthControls controls;

	private DelayA   delayLine_ = new DelayA();
	private OneZero  loopFilter_ = new OneZero();
	private OnePole  pickFilter_ = new OnePole();
	private Noise    noise_ = new Noise();
	private Delay    combDelay = new Delay();
	
	private float loopGain_;
	private int length_;

	private int lowestFrequency = 25;
	private float lastOutput_;

	public PluckedString2SynthChannel(PluckedString2SynthControls controls) {
		super(controls.getName());
		this.controls = controls;
		setPolyphony(1); // mono
		setSampleRate(sampleRate); // provisional
	}

	protected void setSampleRate(int rate) {
		super.setSampleRate(rate);
		length_ = (int) (rate / lowestFrequency + 1);
		loopGain_ = 0.999f;
		delayLine_.setMaximumDelay( length_ );
		delayLine_.setDelay( 0.5f * length_ );
		clear();
	}
	
	public void clear()	{
		delayLine_.clear();
		loopFilter_.clear();
		pickFilter_.clear();
	}

	@Override
	protected Voice createVoice(int pitch, int velocity, int sampleRate) {
		return new PluckedString2Voice(pitch, velocity, sampleRate);
	}

	public class PluckedString2Voice extends AbstractVoice
	{
//		private float pickup;
		private float pick;
		
		private float ampT;
		private float level;
		
		private float bendFactor = 1f;
		
		public PluckedString2Voice(int pitch, int velocity, int sampleRate) {
			super(pitch, velocity);
			
//			pickup = controls.getPickup();
			pick = controls.getPick();
			combDelay.setDelay(pick * length_);

			float ampTracking = controls.getVelocityTrack();
			ampT = velocity == 0 ? 0f : (1 - ampTracking * (1 - amplitude));
			
			setFrequency( frequency );
			pluck( ampT );
		}

		protected void setFrequency( float frequency )	{
			// Delay = length - approximate filter delay.
			float delay = (sampleRate / frequency) - 0.5f;
			delayLine_.setDelay( delay );

			loopGain_ = 0.995f + (frequency * 0.000005f);
			if ( loopGain_ >= 1.0 ) loopGain_ = 0.99999f;
		}

		protected void pluck( float amp ) {
			float gain = amp;
			pickFilter_.setPole( 0.999f - (gain * 0.15f) );
			pickFilter_.setGain( gain * 0.5f );
			for (int i=0; i<length_; i++)
				// Fill delay with noise additively with current contents.
				delayLine_.tick( 0.6f * delayLine_.lastOut() + pickFilter_.tick( noise_.tick() ) );
		}

		public void setSampleRate(int rate) {
			// we can't change sample rate of a playing voice
			// because the digital waveguide model is set up
			// for the sample rate
		}
		
		public boolean mix(AudioBuffer buffer) {
			if ( release ) loopGain_ *= 0.995f;
			level = controls.getLevel() * 12f; // * ampT
			float bf = getBendFactor();
			if ( bendFactor != bf ) {
				setFrequency(frequency * bf);
				bendFactor = bf;
			}
			return super.mix(buffer);
		}
		
		protected float getSample() {
			// Here's the whole inner loop of the instrument!!
			lastOutput_ = delayLine_.tick( loopFilter_.tick( delayLine_.lastOut() * loopGain_ ) ); 
		    return level * lastOutput_;
		}

		protected boolean isComplete() {
			return loopGain_ < 0.1f; // ??? might be well too late TODO
		}
	}
}