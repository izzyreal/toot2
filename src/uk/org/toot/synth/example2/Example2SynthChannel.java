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
	private EnvelopeVariables envelopeLPFVars;
	private EnvelopeVariables envelopeSVFVars;
	private EnvelopeVariables envelopeO1Vars;
	private EnvelopeVariables envelopeO2Vars;
	private FilterVariables lpFilterVars;
	private FilterVariables svFilterVars;
	private AmplifierVariables amplifierVars;
	private DelayedLFOVariables vibratoVars;
	private DelayedLFOVariables lfoO1Vars;
	private DelayedLFOVariables lfoO2Vars;
	private DelayedLFOVariables lfoO3Vars;
	private MixerVariables lpFilterMixerVars;
	private MixerVariables svFilterMixerVars;

	public Example2SynthChannel(Example2SynthControls controls) {
		super(controls.getName());
		oscillator1Vars = controls.getOscillatorVariables(1-1);
		oscillator2Vars = controls.getOscillatorVariables(2-1);
		oscillator3Vars = controls.getOscillatorVariables(3-1);
		envelopeAVars = controls.getEnvelopeVariables(1-1);
		envelopeLPFVars = controls.getEnvelopeVariables(2-1);
		envelopeO1Vars = controls.getEnvelopeVariables(3-1);
		envelopeO2Vars = controls.getEnvelopeVariables(4-1);
		envelopeSVFVars = controls.getEnvelopeVariables(5-1);
		lpFilterVars = controls.getFilterVariables(1-1);
		svFilterVars = controls.getFilterVariables(2-1);
		amplifierVars = controls.getAmplifierVariables();
		vibratoVars = controls.getLFOVariables(0);
		lfoO1Vars = controls.getLFOVariables(1);
		lfoO2Vars = controls.getLFOVariables(2);
		lfoO3Vars = controls.getLFOVariables(3);
		lpFilterMixerVars = controls.getMixerVariables(1-1);
		svFilterMixerVars = controls.getMixerVariables(2-1);
	}

	protected void setSampleRate(int rate) {
		super.setSampleRate(rate);
		envelopeAVars.setSampleRate(rate);
		envelopeLPFVars.setSampleRate(rate);
		envelopeO1Vars.setSampleRate(rate);
		envelopeO2Vars.setSampleRate(rate);
		lpFilterVars.setSampleRate(rate);
		svFilterVars.setSampleRate(rate);
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
		private Filter lpFilter;
		private Filter svFilter;
		private EnvelopeGenerator envelopeA;
		private EnvelopeGenerator envelopeLPF;
		private EnvelopeGenerator envelopeSVF;
		private EnvelopeGenerator envelopeO1;
		private EnvelopeGenerator envelopeO2;
		private DelayedLFO vibratoLFO;
		private DelayedLFO widthO1LFO;
		private DelayedLFO widthO2LFO;
		private DelayedLFO widthO3LFO;
		private OscillatorControl oscControl;
//		private SingleTapDelay delay;
//		private float fb = 0;
		private float ampTracking;
		private float lpfOsc1Level;
		private float lpfOsc2Level;
		private float lpfOsc3Level;
		private float svfOsc1Level;
		private float svfOsc2Level;
		private float svfOsc3Level;
		
		public Example2Voice(int pitch, int velocity, int sampleRate) {
			super(pitch, velocity);
			oscillator1 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator1Vars, frequency);
			oscillator2 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator2Vars, frequency);
			oscillator3 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator3Vars, frequency);
			envelopeA = new EnvelopeGenerator(envelopeAVars);
			envelopeLPF = new EnvelopeGenerator(envelopeLPFVars);
			envelopeSVF = new EnvelopeGenerator(envelopeSVFVars);
			envelopeO1 = new EnvelopeGenerator(envelopeO1Vars);
			envelopeO2 = new EnvelopeGenerator(envelopeO2Vars);
			vibratoLFO = new DelayedLFO(vibratoVars);
			widthO1LFO = new DelayedLFO(lfoO1Vars);
			widthO2LFO = new DelayedLFO(lfoO2Vars);
			widthO3LFO = new DelayedLFO(lfoO3Vars);
			lpFilter = new MoogFilter2(lpFilterVars, frequency, amplitude);
			svFilter = new StateVariableFilter(svFilterVars, frequency, amplitude);
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
			envelopeLPFVars.setSampleRate(rate);
			envelopeO1Vars.setSampleRate(rate);
			envelopeO2Vars.setSampleRate(rate);
			vibratoLFO.setSampleRate(rate);
			widthO1LFO.setSampleRate(rate);
			widthO2LFO.setSampleRate(rate);
			widthO3LFO.setSampleRate(rate);
			lpFilter.setSampleRate(rate);
			svFilter.setSampleRate(rate);
		}
		
		public boolean mix(AudioBuffer buffer) {
			oscillator1.update();
			oscillator2.update();
			oscillator3.update();
			vibratoLFO.update();
			widthO1LFO.update();
			widthO2LFO.update();
			widthO3LFO.update();
			lpFilter.update();
			svFilter.update();
			lpfOsc1Level = lpFilterMixerVars.getLevel(0);
			lpfOsc2Level = lpFilterMixerVars.getLevel(1);
			lpfOsc3Level = lpFilterMixerVars.getLevel(2);
			svfOsc1Level = svFilterMixerVars.getLevel(0);
			svfOsc2Level = svFilterMixerVars.getLevel(1);
			svfOsc3Level = svFilterMixerVars.getLevel(2);
			return super.mix(buffer);
		}
		
		protected float getSample() {
			// modulation
			float mod = vibratoLFO.getSample();						// -1..1
//			float modWheel = (float)getController(1) / 128;			// 0..1
			float vibrato = (mod/50);  								// 2% freq change max
			// oscillators
			float lfo = widthO1LFO.getSample();						// -1..1
			float osc1sample = oscillator1.getSample(vibrato, 0, lfo, oscControl);
			float env = envelopeO1.getEnvelope(release); 			// 0..1
			lfo = widthO2LFO.getSample();							// -1..1
			float osc2sample = oscillator2.getSample(vibrato, env*env, lfo, oscControl);
			env = envelopeO2.getEnvelope(release); 					// 0..1
			lfo = widthO3LFO.getSample();							// -1..1
			float osc3sample = oscillator3.getSample(vibrato, env*env, lfo, oscControl);
			oscControl.sync = false; 								// clear sync
			// mix oscillators for low pass filter
			float sample = osc1sample * lpfOsc1Level
						 + osc2sample * lpfOsc2Level
						 + osc3sample * lpfOsc3Level;
			// low pass filter it with envelope modulation
			env = envelopeLPF.getEnvelope(release); 				// 0..1
			sample = lpFilter.filter(sample, env);
			// mix oscillators for state variable filter
			float sample2 = osc1sample * svfOsc1Level
						  + osc2sample * svfOsc2Level
						  + osc3sample * svfOsc3Level;
			// State Variable Filter it wih envelope modulation
			env = envelopeSVF.getEnvelope(release);					// 0..1
			sample += svFilter.filter(sample2, env);
			// scale for velocity with envelope1 modulation
			env = envelopeA.getEnvelope(release); 					// 0..1
			sample *= env * ( 1 - ampTracking * (1 - amplitude));	// -1..1 
			return sample / 4; // TODO level control
		}

		protected boolean isComplete() {
			return envelopeA.isComplete();
		}
	}
}