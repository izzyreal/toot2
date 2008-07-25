package uk.org.toot.synth.example2;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.synth.SynthChannel;
//import uk.org.toot.midi.synth.delay.SingleTapDelay;
import uk.org.toot.synth.amplifier.AmplifierVariables;
import uk.org.toot.synth.envelope.*;
import uk.org.toot.synth.oscillator.*;
import uk.org.toot.synth.filter.*;

/**
 * This class provides an example SynthChannel producing Voices which use a 
 * wave lookup table oscillator, a Moogish LPF and separate AHDSR envelopes for amplitude 
 * and optional filter modulation. A AHDSR envelope is also provided for modulating the 
 * frequency of the oscillator's sub lookup for hard sync. The oscillator also provides
 * a fixed detune if the sync envelope modulation is zero.
 * 
 * We don't band-limit.
 *
 * @author st
 *
 */
public class Example2SynthChannel extends SynthChannel
{
	private MultiWaveOscillatorVariables oscillator1Variables;
	private MultiWaveOscillatorVariables oscillator2Variables;
	private EnvelopeVariables envelopeAVars;
	private EnvelopeVariables envelopeFVars;
	private EnvelopeVariables envelopeOVars;
	private FilterVariables filterVars;
	private AmplifierVariables amplifierVariables;
	
	public Example2SynthChannel(Example2SynthControls controls) {
		super(controls.getName());
		oscillator1Variables = controls.getOscillatorVariables(1-1);
		oscillator2Variables = controls.getOscillatorVariables(2-1);
		envelopeAVars = controls.getEnvelopeVariables(1-1);
		envelopeFVars = controls.getEnvelopeVariables(2-1);
		envelopeOVars = controls.getEnvelopeVariables(3-1);
		filterVars = controls.getFilterVariables(0);
		amplifierVariables = controls.getAmplifierVariables();
	}

	protected void setSampleRate(int rate) {
		super.setSampleRate(rate);
		envelopeAVars.setSampleRate(rate);
		envelopeFVars.setSampleRate(rate);
		envelopeOVars.setSampleRate(rate);
		filterVars.setSampleRate(rate);
	}
	
	@Override
	protected Voice createVoice(int pitch, int velocity, int sampleRate) {
		return new ExampleVoice(pitch, velocity, sampleRate);
	}

	public class ExampleVoice extends AbstractVoice
	{
		private Oscillator oscillator1;
		private Oscillator oscillator2;
		private Filter filter;
		private EnvelopeGenerator envelopeA;
		private EnvelopeGenerator envelopeF;
		private EnvelopeGenerator envelopeO;
		private Oscillator lfo;
		private OscillatorControl oscillatorControl;
//		private SingleTapDelay delay;
		private float filterFreq = 0.001f;
		private float filterRes = 2f;
		private float filterEnvDepth = 1f;
//		private float fb = 0;
		private float ampTracking;
		
		public ExampleVoice(int pitch, int velocity, int sampleRate) {
			super(pitch, velocity);
			oscillator1 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator1Variables, frequency);
			oscillator2 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator2Variables, frequency);
			envelopeA = new EnvelopeGenerator(envelopeAVars);
			envelopeF = new EnvelopeGenerator(envelopeFVars);
			envelopeO = new EnvelopeGenerator(envelopeOVars);
			lfo = new LFOscillator();
			filter = new MoogFilter2();
			oscillatorControl = new OscillatorControl();
//			delay = new SingleTapDelay(4410);
			setSampleRate(sampleRate);
			ampTracking = amplifierVariables.getVelocityTrack();
		}

		public void setSampleRate(int rate) {
			oscillator1.setSampleRate(rate);
			oscillator2.setSampleRate(rate);
			envelopeAVars.setSampleRate(rate);
			envelopeFVars.setSampleRate(rate);
			envelopeOVars.setSampleRate(rate);
			lfo.setSampleRate(rate);
			// update sample rate dependent filter vars
			if ( filterVars != null ) {
				filterRes  = filterVars.getResonance();
				float f = frequency * 2 / rate;
				filterFreq = f + filterVars.getFrequency();
				filterFreq *= 1 - filterVars.getVelocityTrack() * (1f - amplitude); // !!! TODO
				if ( filterFreq >= 1 ) filterFreq = 1f;
				// normalise the filter env depth to ensure 0 < fc < 1
				filterEnvDepth = filterVars.getEvelopeDepth() * (1 - filterFreq) * amplitude;
			}
		}
		
		public boolean mix(AudioBuffer buffer) {
			oscillator1.update();
			oscillator2.update();
			return super.mix(buffer);
		}
		
		protected float getSample() {
			// the envelopes
			float envA = envelopeA.getEnvelope(release); 		// 0..1 - amplitude
			float envF = envelopeF.getEnvelope(release); 		// 0..1 - filter fc
			float envO = envelopeO.getEnvelope(release); 		// 0..1 - sync osc
			// modulation
			float mod = lfo.getSample(0f, 0f, null);			// -1..1
			float modWheel = (float)getController(1) / 128;		// 0..1
			float vibrato = modWheel * (mod/50);  				// 2% freq change max
			// an oscillator sample
			float sample = oscillator1.getSample(vibrato, envO, oscillatorControl);
			sample += oscillator2.getSample(vibrato, envO, oscillatorControl);
			oscillatorControl.sync = false; // clear sync
//			sample += fb;										// delay feedback
			// filter it, optionally with envelope2 modulation
			float fc = filterFreq + filterEnvDepth * envF;		// 0..1
			sample = filter.filter(sample, fc, filterRes);
			// scale for velocity with envelope1 modulation
			sample *= envA * ( 1 - ampTracking * (1 - amplitude));						// -1..1
//			fb = 0.9f * delay.getSample(sample);
			return sample;
		}

		protected boolean isComplete() {
			return envelopeA.isComplete();
		}
	}
}