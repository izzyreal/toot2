package uk.org.toot.synth.channels.example2;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.synth.SynthChannel;
//import uk.org.toot.midi.synth.delay.SingleTapDelay;
import uk.org.toot.synth.modules.amplifier.AmplifierVariables;
import uk.org.toot.synth.modules.envelope.*;
import uk.org.toot.synth.modules.filter.*;
import uk.org.toot.synth.modules.mixer.MixerVariables;
import uk.org.toot.synth.modules.oscillator.*;

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
		private float ampT; // amp tracking factor
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
			lpFilter = new MoogFilter2(lpFilterVars, envelopeLPF, frequency, amplitude);
			svFilter = new StateVariableFilter(svFilterVars, envelopeSVF, frequency, amplitude);
			oscControl = new OscillatorControl();
//			delay = new SingleTapDelay(4410);
			float ampTracking = amplifierVars.getVelocityTrack();
			ampT = velocity == 0 ? 0f : (1 - ampTracking * (1 - amplitude));
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
			vibratoLFO.update(); // other LFO's are updated by their oscillator
			oscillator1.update();
			lpfOsc1Level = lpFilterMixerVars.getLevel(0);
			lpfOsc2Level = lpFilterMixerVars.getLevel(1);
			lpfOsc3Level = lpFilterMixerVars.getLevel(2);
			svfOsc1Level = svFilterMixerVars.getLevel(0);
			svfOsc2Level = svFilterMixerVars.getLevel(1);
			svfOsc3Level = svFilterMixerVars.getLevel(2);
			osc2Enabled = lpfOsc2Level + svfOsc2Level > 0.01f;
			if ( osc2Enabled ) oscillator2.update();
			osc3Enabled = lpfOsc3Level + svfOsc3Level > 0.01f;
			if ( osc3Enabled ) oscillator3.update();
			lpfEnabled = lpfOsc1Level + lpfOsc2Level + lpfOsc3Level > 0.01f;   
			if ( lpfEnabled) lpFilter.update();
			svfEnabled = svfOsc1Level + svfOsc2Level + svfOsc3Level > 0.01f;
			if ( svfEnabled) svFilter.update();
			ampLevel = amplifierVars.getLevel() * ampT;
			return super.mix(buffer);
		}
		
		protected float getSample() {
			float sample = 0f;
			float s2 = 0f;
			float s3 = 0f;
			float vibrato = 1f + vibratoLFO.getSample() / 50; // 2% freq change max
			// oscillators
			float s1 = oscillator1.getSample(vibrato, oscControl, release);
			if ( osc2Enabled ) {
				s2 = oscillator2.getSample(vibrato, oscControl, release);
			}
			if ( osc3Enabled ) {
				s3 = oscillator3.getSample(vibrato, oscControl, release);
			}
			oscControl.sync = false; // clear sync for next iteration
			// filters
			if ( lpfEnabled ) {
				sample = s1 * lpfOsc1Level + s2 * lpfOsc2Level + s3 * lpfOsc3Level;
				sample = lpFilter.filter(sample, release);
			}
			if ( svfEnabled ) {
				float 
				sample2 = s1 * svfOsc1Level + s2 * svfOsc2Level + s3 * svfOsc3Level;
				sample += svFilter.filter(sample2, release);
			}
			// amplifier
			return sample * ampLevel * envelopeA.getEnvelope(release);				   
		}

		protected boolean isComplete() {
			return envelopeA.isComplete();
		}
	}
}