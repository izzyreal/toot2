package uk.org.toot.synth.example2;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.synth.SynthChannel;
//import uk.org.toot.midi.synth.delay.SingleTapDelay;
import uk.org.toot.synth.amplifier.AmplifierVariables;
import uk.org.toot.synth.envelope.*;
import uk.org.toot.synth.mixer.MixerVariables;
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
	private MultiWaveOscillatorVariables oscillator1Vars;
	private MultiWaveOscillatorVariables oscillator2Vars;
	private MultiWaveOscillatorVariables oscillator3Vars;
	private EnvelopeVariables envelopeAVars;
	private EnvelopeVariables envelopeFVars;
	private EnvelopeVariables envelopeO1Vars;
	private EnvelopeVariables envelopeO2Vars;
	private FilterVariables filterVars;
	private AmplifierVariables amplifierVars;
	private DelayedLFOVariables vibratoVars;
	private DelayedLFOVariables lfoO1Vars;
	private DelayedLFOVariables lfoO2Vars;
	private DelayedLFOVariables lfoO3Vars;
	private MixerVariables filterMixerVars;

	public Example2SynthChannel(Example2SynthControls controls) {
		super(controls.getName());
		oscillator1Vars = controls.getOscillatorVariables(1-1);
		oscillator2Vars = controls.getOscillatorVariables(2-1);
		oscillator3Vars = controls.getOscillatorVariables(3-1);
		envelopeAVars = controls.getEnvelopeVariables(1-1);
		envelopeFVars = controls.getEnvelopeVariables(2-1);
		envelopeO1Vars = controls.getEnvelopeVariables(3-1);
		envelopeO2Vars = controls.getEnvelopeVariables(4-1);
		filterVars = controls.getFilterVariables(0);
		amplifierVars = controls.getAmplifierVariables();
		vibratoVars = controls.getLFOVariables(0);
		lfoO1Vars = controls.getLFOVariables(1);
		lfoO2Vars = controls.getLFOVariables(2);
		lfoO3Vars = controls.getLFOVariables(3);
		filterMixerVars = controls.getMixervariables();
	}

	protected void setSampleRate(int rate) {
		super.setSampleRate(rate);
		envelopeAVars.setSampleRate(rate);
		envelopeFVars.setSampleRate(rate);
		envelopeO1Vars.setSampleRate(rate);
		envelopeO2Vars.setSampleRate(rate);
		filterVars.setSampleRate(rate);
	}
	
	@Override
	protected Voice createVoice(int pitch, int velocity, int sampleRate) {
		return new Example2Voice(pitch, velocity, sampleRate);
	}

	public class Example2Voice extends AbstractVoice
	{
		private Oscillator oscillator1;
		private Oscillator oscillator2;
		private Oscillator oscillator3;
		private Filter filter;
		private EnvelopeGenerator envelopeA;
		private EnvelopeGenerator envelopeF;
		private EnvelopeGenerator envelopeO1;
		private EnvelopeGenerator envelopeO2;
		private DelayedLFO vibratoLFO;
		private DelayedLFO widthO1LFO;
		private DelayedLFO widthO2LFO;
		private DelayedLFO widthO3LFO;
		private OscillatorControl oscControl;
//		private SingleTapDelay delay;
		private float filterFreq = 0.001f;
		private float filterRes = 2f;
		private float filterEnvDepth = 1f;
//		private float fb = 0;
		private float ampTracking;
		private float lpfOsc1Level;
		private float lpfOsc2Level;
		private float lpfOsc3Level;
		
		public Example2Voice(int pitch, int velocity, int sampleRate) {
			super(pitch, velocity);
			oscillator1 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator1Vars, frequency);
			oscillator2 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator2Vars, frequency);
			oscillator3 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator3Vars, frequency);
			envelopeA = new EnvelopeGenerator(envelopeAVars);
			envelopeF = new EnvelopeGenerator(envelopeFVars);
			envelopeO1 = new EnvelopeGenerator(envelopeO1Vars);
			envelopeO2 = new EnvelopeGenerator(envelopeO2Vars);
			vibratoLFO = new DelayedLFO(vibratoVars);
			widthO1LFO = new DelayedLFO(lfoO1Vars);
			widthO2LFO = new DelayedLFO(lfoO2Vars);
			widthO3LFO = new DelayedLFO(lfoO3Vars);
			filter = new MoogFilter2();
			oscControl = new OscillatorControl();
//			delay = new SingleTapDelay(4410);
			setSampleRate(sampleRate);
			ampTracking = amplifierVars.getVelocityTrack();
		}

		public void setSampleRate(int rate) {
			oscillator1.setSampleRate(rate);
			oscillator2.setSampleRate(rate);
			oscillator3.setSampleRate(rate);
			envelopeAVars.setSampleRate(rate);
			envelopeFVars.setSampleRate(rate);
			envelopeO1Vars.setSampleRate(rate);
			envelopeO2Vars.setSampleRate(rate);
			vibratoLFO.setSampleRate(rate);
			widthO1LFO.setSampleRate(rate);
			widthO2LFO.setSampleRate(rate);
			widthO3LFO.setSampleRate(rate);
			// update sample rate dependent filter vars
			if ( filterVars != null ) {
				filterRes  = filterVars.getResonance();
				float f = frequency * 2 / rate;
				filterFreq = f + filterVars.getFrequency();
				filterFreq *= 1 - filterVars.getVelocityTrack() * (1f - amplitude); // !!! TODO
				if ( filterFreq >= 1 ) filterFreq = 1f;
				float fED = filterVars.getEvelopeDepth();
				float fERange = fED < 0 ? filterFreq - 0.0025f : 1 - filterFreq;
				// normalise the filter env depth to ensure 0 < fc < 1
				filterEnvDepth = fED * fERange * amplitude;
			}
		}
		
		public boolean mix(AudioBuffer buffer) {
			oscillator1.update();
			oscillator2.update();
			oscillator3.update();
			vibratoLFO.update();
			widthO1LFO.update();
			widthO2LFO.update();
			widthO3LFO.update();
			lpfOsc1Level = filterMixerVars.getLevel(0);
			lpfOsc2Level = filterMixerVars.getLevel(1);
			lpfOsc3Level = filterMixerVars.getLevel(2);
			return super.mix(buffer);
		}
		
		protected float getSample() {
			float env;
			float lfo;
			// modulation
			float mod = vibratoLFO.getSample();						// -1..1
//			float modWheel = (float)getController(1) / 128;			// 0..1
			float vibrato = (mod/50);  								// 2% freq change max
			// oscillators
			lfo = widthO1LFO.getSample();
			float osc1sample = oscillator1.getSample(vibrato, 0, lfo, oscControl);
			env = envelopeO1.getEnvelope(release); 					// 0..1 - sync osc
			lfo = widthO2LFO.getSample();
			float osc2sample = oscillator2.getSample(vibrato, env*env, lfo, oscControl);
			env = envelopeO2.getEnvelope(release); 					// 0..1 - sync osc
			lfo = widthO3LFO.getSample();
			float osc3sample = oscillator3.getSample(vibrato, env*env, lfo, oscControl);
			oscControl.sync = false; 						// clear sync
			// mix oscillators for filter
			float sample = osc1sample * lpfOsc1Level
						 + osc2sample * lpfOsc2Level
						 + osc3sample * lpfOsc3Level;
			// filter it, optionally with envelope2 modulation
			env = envelopeF.getEnvelope(release); 					// 0..1 - filter fc
			float fc = filterFreq + filterEnvDepth * env;			// 0..1
			sample = filter.filter(sample, fc, filterRes);
			// scale for velocity with envelope1 modulation
			env = envelopeA.getEnvelope(release); 					// 0..1 - amplitude
			sample *= env * ( 1 - ampTracking * (1 - amplitude));	// -1..1 
			return sample / 4; // TODO level control
		}

		protected boolean isComplete() {
			return envelopeA.isComplete();
		}
	}
}