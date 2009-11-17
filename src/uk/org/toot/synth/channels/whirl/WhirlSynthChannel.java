package uk.org.toot.synth.channels.whirl;

import uk.org.toot.midi.misc.Controller;
import uk.org.toot.synth.MonophonicSynthChannel;
import uk.org.toot.synth.modules.GlideVariables;
import uk.org.toot.synth.modules.amplifier.AmplifierVariables;
import uk.org.toot.synth.modules.envelope.EnvelopeGenerator;
import uk.org.toot.synth.modules.envelope.EnvelopeVariables;
import uk.org.toot.synth.modules.filter.StateVariableFilter;
import uk.org.toot.synth.modules.mixer.ModulationMixerVariables;
import uk.org.toot.synth.modules.oscillator.LFO;
import uk.org.toot.synth.modules.oscillator.DualMultiWaveOscillator;
import uk.org.toot.synth.modules.oscillator.OscillatorControl;

public class WhirlSynthChannel extends MonophonicSynthChannel
{
	private EnvelopeVariables modEnvVars, ampEnvVars;
	private AmplifierVariables ampVars;

	private OscillatorControl oscControl;
	private DualMultiWaveOscillator mainOsc, subOsc;
	private StateVariableFilter filter;
	private EnvelopeGenerator modEnv, ampEnv;
	private LFO lfo;
	
	private ModulationMixerVariables mainSyncMod, mainPWMMod, subPWMMod, cutoffMod;
	private GlideVariables glideVars;
	
	private float mainSyncLFODepth, mainSyncEnvDepth;
	private float mainWidthLFODepth, mainWidthEnvDepth;
	private float subWidthLFODepth, subWidthEnvDepth;
	private float[] cutoffDepths;
	
	private float ampT; // amp tracking factor
	private float ampLevel;
	private boolean release = false;
	
	public WhirlSynthChannel(WhirlSynthControls controls) {
		super("Whirl");
		oscControl = new OscillatorControl();
		mainOsc = new DualMultiWaveOscillator(this, controls.getOscillatorVariables(0));
		subOsc = new DualMultiWaveOscillator(this, controls.getOscillatorVariables(1));
		filter = new StateVariableFilter(controls.getFilterVariables(0));
		ampVars = controls.getAmplifierVariables();
		modEnvVars = controls.getEnvelopeVariables(1);
		modEnv = new EnvelopeGenerator(modEnvVars);
		ampEnvVars = controls.getEnvelopeVariables(0);
		ampEnv = new EnvelopeGenerator(ampEnvVars);
		lfo = new LFO(controls.getLFOVariables(0), (float)(-Math.PI /2));
		mainSyncMod = controls.getModulationMixerVariables(0);
		mainPWMMod = controls.getModulationMixerVariables(1);
		subPWMMod = controls.getModulationMixerVariables(2);
		cutoffMod = controls.getModulationMixerVariables(3);
		glideVars = controls.getGlideVariables();
	}

	@Override
	protected void trigger(float amp) {
		release = false;
		float ampTracking = ampVars.getVelocityTrack();
		ampT = amp == 0f ? 0f : (1 - ampTracking * (1 - amp));
		modEnv.trigger();
		ampEnv.trigger();
	}

	@Override
	protected void release() {
		release = true;
	}

	@Override
	protected boolean isComplete() {
		return ampEnv.isComplete();
	}

	@Override
	protected void setSampleRate(int rate) {
		super.setSampleRate(rate);
		mainOsc.setSampleRate(rate);
		subOsc.setSampleRate(rate);
		filter.setSampleRate(rate);
		modEnvVars.setSampleRate(rate);
		ampEnvVars.setSampleRate(rate);
	}
	
	@Override
	protected void update(float frequency) {
		mainSyncLFODepth = mainSyncMod.getDepth(0);
		mainSyncEnvDepth = mainSyncMod.getDepth(1);
		mainWidthLFODepth = mainPWMMod.getDepth(0);
		mainWidthEnvDepth = mainPWMMod.getDepth(1);
		subWidthLFODepth = subPWMMod.getDepth(0);
		subWidthEnvDepth = subPWMMod.getDepth(1);
		cutoffDepths = cutoffMod.getDepths(); // LFO, Env, Vel, Key, AT, Wheel
		cutoffDepths[0] /= 4; // LFO reduced sensitivity for lf control
		cutoffDepths[3] += 1; // Key -1..1 -> 0..2, default 0 -> 1
		float cutoffMod = amplitude * cutoffDepths[2] +
							frequency * 2 / sampleRate * cutoffDepths[3] +
							getChannelPressure() / 128 * cutoffDepths[4] +
							getController(Controller.MODULATION) / 128 * cutoffDepths[5];
		if ( cutoffMod > 1f ) cutoffMod = 1f;
		else if ( cutoffMod < -1f ) cutoffMod = -1f;
		lfo.update();
		mainOsc.update(frequency);
		subOsc.update(frequency * 0.5f);
		filter.update(frequency * (1f + 0.99f * cutoffMod)); // down to zero, up one octave!
		ampLevel = ampVars.getLevel() * ampT;
	}

	@Override
	protected float getSample() {
		// modulation sources
		float lfoSample = (1f + lfo.getSample()) / 2f; 	// 0..1
		float envSample = modEnv.getEnvelope(release);	// 0..1
		float envSampleSquared = envSample * envSample; // 0..1
		// modulation destinations
		float vibMod = 1f; // + vibDepth * vibSample / 50; // TODO AT? Wheel?
		float syncMod = mainSyncLFODepth * lfoSample + mainSyncEnvDepth * envSampleSquared;
		float subWidthMod = subWidthLFODepth * lfoSample + subWidthEnvDepth * envSample;
		float mainWidthMod = mainWidthLFODepth * lfoSample + mainWidthEnvDepth * envSample;
		float cutoffMod = cutoffDepths[0] * lfoSample + cutoffDepths[1] * envSampleSquared;
		// oscillators
		float sample = subOsc.getSample(vibMod, subWidthMod, oscControl);
		sample += mainOsc.getSample(vibMod + syncMod, mainWidthMod, oscControl); // syncs to sub
		oscControl.sync = false;
		// filter
		sample = filter.filter(sample, cutoffMod);
		// amplifier
		return sample * ampEnv.getEnvelope(release) * ampLevel;
	}

	@Override
	protected int getGlideMilliseconds() {
		return glideVars.getGlideMilliseconds();
	}

	@Override
	protected boolean isGlideEnabled() {
		return glideVars.isGlideEnabled();
	}
}
