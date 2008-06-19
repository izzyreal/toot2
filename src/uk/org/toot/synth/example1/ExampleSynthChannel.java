package uk.org.toot.synth.example1;

import uk.org.toot.synth.SynthChannel;
//import uk.org.toot.midi.synth.delay.SingleTapDelay;
import uk.org.toot.synth.envelope.*;
import uk.org.toot.synth.oscillator.*;
import uk.org.toot.synth.filter.*;

/**
 * This class provides an example SynthChannel producing Voices which use a 
 * wave lookup table oscillator, a Moogish LPF and separate DAHDSR envelopes for amplitude 
 * and optional filter modulation.
 * 
 * We don't band-limit, we hope the LPF will remove aliases :(
 *
 * @author st
 *
 */
public class ExampleSynthChannel extends SynthChannel
{
	private WaveOscillatorVariables oscillatorVariables;
	private EnvelopeVariables envelope1Vars; // TODO supply
	private EnvelopeVariables envelope2Vars; // TODO supply
	private FilterVariables filterVars; // TODO supply
	
	public ExampleSynthChannel(ExampleSynthControls controls) {
		super(controls.getName());
		oscillatorVariables = controls.getOscillatorVariables();
		envelope1Vars = controls.getEnvelopeVariables(0);
		envelope2Vars = controls.getEnvelopeVariables(1);
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
		private Oscillator oscillator;
		private Filter filter;
		private EnvelopeGenerator envelope1;
		private EnvelopeGenerator envelope2;
//		private SingleTapDelay delay;
		private float amplitude = 1;
		private float filterFreq = 0.001f;
		private float filterRes = 2f;
		private float filterEnvDepth = 1f;
//		private float fb = 0;
		
		public ExampleVoice(int pitch, int velocity, int sampleRate) {
			super(pitch, velocity);
			amplitude = (float)velocity / 128;
			oscillator = new WaveOscillator(oscillatorVariables, pitch);
			envelope1 = new EnvelopeGenerator(envelope1Vars);
			envelope2 = new EnvelopeGenerator(envelope2Vars);
			filter = new MoogFilter2();
			if ( filterVars != null ) {
				float fEnvDepth = filterVars.getEvelopeDepth();
				// normalise the filter env depth to ensure 0 < fc < 1
				filterEnvDepth = fEnvDepth * ((fEnvDepth < 0) ?  filterFreq : (1 - filterFreq));
//				System.out.println("fED="+fEnvDepth+" => "+filterEnvDepth);
			}
//			delay = new SingleTapDelay(4410);
			setSampleRate(sampleRate);
		}

		public void setSampleRate(int rate) {
			oscillator.setSampleRate(rate);
			// update sample rate dependent filter vars
			if ( filterVars != null ) {
				filterFreq = filterVars.getFrequency();
				filterRes  = filterVars.getResonance();
			}
		}
		
		protected float getSample() {
			// the envelopes
			float env1 = envelope1.getEnvelope(release); 		// 0..1 - amplitude
			float env2 = envelope2.getEnvelope(release); 		// 0..1 - filter fc
			// an oscillator sample
			float sample = oscillator.getSample(0f, 0f); 		// no fm, pm or env
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