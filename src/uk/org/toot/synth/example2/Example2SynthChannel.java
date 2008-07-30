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
 * 3 Band Limited Oscillators
 * 		continously variable width between Pulse/Square or Saw/Triangle
 * 		Hard Sync of oscillators 2 and 3
 * 4 LFOs, Sine/Triangle, one for Vibrato, one per Oscillator modulating width
 * 5 AHDSR Envelopes, one amplifier, one per filter, one for oscillators 2 and 3
 * Moog 24dB/octave Low Pass Filter
 * Oberheim SEM 12dB/octave Multimode Filter
 * 
 * @author st
 */
public class Example2SynthChannel extends SynthChannel
{
	private MultiWaveOscillatorVariables oscillator1Vars;
	private MultiWaveOscillatorVariables oscillator2Vars;
	private MultiWaveOscillatorVariables oscillator3Vars;
	private EnvelopeVariables envelopeAVars;
	private EnvelopeVariables envelopeLPFVars;
	private EnvelopeVariables envelopeSVFVars;
	private EnvelopeVariables envelopeO2Vars;
	private EnvelopeVariables envelopeO3Vars;
	private FilterVariables lpFilterVars;
	private StateVariableFilterVariables svFilterVars;
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
		envelopeAVars = controls.getEnvelopeVariables(0);
		envelopeLPFVars = controls.getEnvelopeVariables(1);
		envelopeO2Vars = controls.getEnvelopeVariables(2);
		envelopeO3Vars = controls.getEnvelopeVariables(3);
		envelopeSVFVars = controls.getEnvelopeVariables(4);
		lpFilterVars = controls.getFilterVariables(0);
		svFilterVars = (StateVariableFilterVariables)controls.getFilterVariables(1);
		amplifierVars = controls.getAmplifierVariables();
		vibratoVars = controls.getLFOVariables(0);
		lfoO1Vars = controls.getLFOVariables(1);
		lfoO2Vars = controls.getLFOVariables(2);
		lfoO3Vars = controls.getLFOVariables(3);
		lpFilterMixerVars = controls.getMixerVariables(0);
		svFilterMixerVars = controls.getMixerVariables(1);
	}

	protected void setSampleRate(int rate) {
		super.setSampleRate(rate);
		envelopeAVars.setSampleRate(rate);
		envelopeLPFVars.setSampleRate(rate);
		envelopeSVFVars.setSampleRate(rate);
		envelopeO2Vars.setSampleRate(rate);
		envelopeO3Vars.setSampleRate(rate);
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
		private float ampLevel;
		private float lpfOsc1Level;
		private float lpfOsc2Level;
		private float lpfOsc3Level;
		private float svfOsc1Level;
		private float svfOsc2Level;
		private float svfOsc3Level;
		
		private boolean lpfEnabled;
		private boolean svfEnabled;
		private boolean osc2Enabled;
		private boolean osc3Enabled;
		
		public Example2Voice(int pitch, int velocity, int sampleRate) {
			super(pitch, velocity);
			envelopeO1 = new EnvelopeGenerator(envelopeO2Vars);
			envelopeO2 = new EnvelopeGenerator(envelopeO3Vars);
			widthO1LFO = new DelayedLFO(lfoO1Vars);
			widthO2LFO = new DelayedLFO(lfoO2Vars);
			widthO3LFO = new DelayedLFO(lfoO3Vars);
			oscillator1 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator1Vars, null, widthO1LFO, frequency);
			oscillator2 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator2Vars, envelopeO1, widthO2LFO, frequency);
			oscillator3 = new MultiWaveOscillator(Example2SynthChannel.this, oscillator3Vars, envelopeO2, widthO3LFO, frequency);
			envelopeA = new EnvelopeGenerator(envelopeAVars);
			envelopeLPF = new EnvelopeGenerator(envelopeLPFVars);
			envelopeSVF = new EnvelopeGenerator(envelopeSVFVars);
			vibratoLFO = new DelayedLFO(vibratoVars);
			lpFilter = new MoogFilter2(lpFilterVars, frequency, amplitude);
			svFilter = new StateVariableFilter(svFilterVars, frequency, amplitude);
			oscControl = new OscillatorControl();
//			delay = new SingleTapDelay(4410);
			ampTracking = amplifierVars.getVelocityTrack();
			setSampleRate(sampleRate);
		}

		public void setSampleRate(int rate) {
			oscillator1.setSampleRate(rate);
			oscillator2.setSampleRate(rate);
			oscillator3.setSampleRate(rate);
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
			vibratoLFO.update(); // other LFO's are updated by their oscillator
			lpFilter.update();
			svFilter.update();
			lpfOsc1Level = lpFilterMixerVars.getLevel(0);
			lpfOsc2Level = lpFilterMixerVars.getLevel(1);
			lpfOsc3Level = lpFilterMixerVars.getLevel(2);
			svfOsc1Level = svFilterMixerVars.getLevel(0);
			svfOsc2Level = svFilterMixerVars.getLevel(1);
			svfOsc3Level = svFilterMixerVars.getLevel(2);
			osc2Enabled = lpfOsc2Level + svfOsc2Level > 0.01f;
			osc3Enabled = lpfOsc3Level + svfOsc3Level > 0.01f;
			lpfEnabled = lpfOsc1Level + lpfOsc2Level + lpfOsc3Level > 0.01f;   
			svfEnabled = svfOsc1Level + svfOsc2Level + svfOsc3Level > 0.01f;
			ampLevel = amplifierVars.getLevel();
			return super.mix(buffer);
		}
		
		protected float getSample() {
			float sample = 0f;
			float env;
			float osc2sample = 0f;
			float osc3sample = 0f;
			// modulation
			float mod = vibratoLFO.getSample();						// -1..1
//			float modWheel = (float)getController(1) / 128;			// 0..1
			float vibrato = (mod/50);  								// 2% freq change max
			// oscillators
			float osc1sample = oscillator1.getSample(vibrato, oscControl, release);
			if ( osc2Enabled ) {
				osc2sample = oscillator2.getSample(vibrato, oscControl, release);
			}
			if ( osc3Enabled ) {
				osc3sample = oscillator3.getSample(vibrato, oscControl, release);
			}
			oscControl.sync = false; 								// clear sync
			if ( lpfEnabled ) {
				// mix oscillators for low pass filter
				sample = osc1sample * lpfOsc1Level
					   + osc2sample * lpfOsc2Level
					   + osc3sample * lpfOsc3Level;
				// low pass filter with envelope modulation
				sample = lpFilter.filter(sample, envelopeLPF.getEnvelope(release));
			}
			if ( svfEnabled ) {
				// mix oscillators for state variable filter
				float 
				sample2 = osc1sample * svfOsc1Level
					    + osc2sample * svfOsc2Level
					    + osc3sample * svfOsc3Level;
				// State Variable Filter wih envelope modulation
				sample += svFilter.filter(sample2, envelopeSVF.getEnvelope(release));
			}
			// scale for velocity with envelope modulation
			env = envelopeA.getEnvelope(release); 					// 0..1
			sample *= env * ( 1 - ampTracking * (1 - amplitude));	// -1..1 
			return sample * ampLevel;
		}

		protected boolean isComplete() {
			return envelopeA.isComplete();
		}
	}
}