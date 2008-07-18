package uk.org.toot.synth.example1;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.synth.SynthChannel;
//import uk.org.toot.midi.synth.delay.SingleTapDelay;
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
public class ExampleSynthChannel extends SynthChannel
{
	private WaveOscillatorVariables oscillator1Variables;
	private WaveOscillatorVariables oscillator2Variables;
	private EnvelopeVariables envelope1Vars;
	private EnvelopeVariables envelope2Vars;
	private EnvelopeVariables envelope3Vars;
	private FilterVariables filterVars;
	
	public ExampleSynthChannel(ExampleSynthControls controls) {
		super(controls.getName());
		oscillator1Variables = controls.getOscillatorVariables(1-1);
		oscillator2Variables = controls.getOscillatorVariables(2-1);
		envelope1Vars = controls.getEnvelopeVariables(1-1);
		envelope2Vars = controls.getEnvelopeVariables(2-1);
		envelope3Vars = controls.getEnvelopeVariables(3-1);
		filterVars = controls.getFilterVariables(0);
	}

	protected void setSampleRate(int rate) {
		super.setSampleRate(rate);
		if ( envelope1Vars != null ) {
			envelope1Vars.setSampleRate(rate);
		}
		if ( envelope2Vars != null ) {
			envelope2Vars.setSampleRate(rate);
		}
		if ( envelope3Vars != null ) {
			envelope2Vars.setSampleRate(rate);
		}
		if ( filterVars != null ) {
			filterVars.setSampleRate(rate);
		}
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
		private EnvelopeGenerator envelope1;
		private EnvelopeGenerator envelope2;
		private EnvelopeGenerator envelope3;
		private Oscillator lfo;
		private OscillatorControl oscillatorControl;
//		private SingleTapDelay delay;
		private float amplitude;
		private float frequency;
		private float filterFreq = 0.001f;
		private float filterRes = 2f;
		private float filterEnvDepth = 1f;
//		private float fb = 0;
		
		public ExampleVoice(int pitch, int velocity, int sampleRate) {
			super(pitch, velocity);
			amplitude = (float)velocity / 128;
			frequency = midiFreq(pitch);
			oscillator1 = new WaveOscillator(ExampleSynthChannel.this, oscillator1Variables, frequency);
			oscillator2 = new WaveOscillator(ExampleSynthChannel.this, oscillator2Variables, frequency);
			envelope1 = new EnvelopeGenerator(envelope1Vars);
			envelope2 = new EnvelopeGenerator(envelope2Vars);
			envelope3 = new EnvelopeGenerator(envelope3Vars);
			lfo = new LFOscillator();
			filter = new MoogFilter2();
			oscillatorControl = new OscillatorControl();
//			delay = new SingleTapDelay(4410);
			setSampleRate(sampleRate);
		}

		public void setSampleRate(int rate) {
			oscillator1.setSampleRate(rate);
			oscillator2.setSampleRate(rate);
			lfo.setSampleRate(rate);
			// update sample rate dependent filter vars
			if ( filterVars != null ) {
				filterRes  = filterVars.getResonance();
				float f = frequency * 2 / rate;
				filterFreq = f + filterVars.getFrequency();
				filterFreq *= 1 + filterVars.getVelocityTrack() * amplitude;
				if ( filterFreq >= 1 ) filterFreq = 0.99f;
				// normalise the filter env depth to ensure 0 < fc < 1
				filterEnvDepth = filterVars.getEvelopeDepth() * (1 - filterFreq);
			}
		}
		
		public boolean mix(AudioBuffer buffer) {
			oscillator1.update();
			oscillator2.update();
			return super.mix(buffer);
		}
		
		protected float getSample() {
			// the envelopes
			float env1 = envelope1.getEnvelope(release); 		// 0..1 - amplitude
			float env2 = envelope2.getEnvelope(release); 		// 0..1 - filter fc
			float env3 = envelope3.getEnvelope(release); 		// 0..1 - sync osc
			// modulation
			float mod = lfo.getSample(0f, 0f, null);					// -1..1
			float modWheel = (float)getController(1) / 128;		// 0..1
			float vibrato = modWheel * (mod/50);  				// 2% freq change max
			// an oscillator sample
			float sample = oscillator1.getSample(vibrato, env3, oscillatorControl);
			sample += oscillator2.getSample(vibrato, env3, oscillatorControl);
			oscillatorControl.sync = false; // clear sync
//			sample += fb;										// delay feedback
			// filter it, optionally with envelope2 modulation
			float fc = filterFreq + filterEnvDepth * env2;		// 0..1
			sample = filter.filter(sample, fc, filterRes);
			// scale for velocity with envelope1 modulation
			sample *= amplitude * env1 ;						// -1..1
//			fb = 0.9f * delay.getSample(sample);
			return sample;
		}

		protected boolean isComplete() {
			return envelope1.isComplete();
		}
	}
}