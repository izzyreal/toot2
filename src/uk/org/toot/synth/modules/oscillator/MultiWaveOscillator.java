package uk.org.toot.synth.modules.oscillator;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.modules.envelope.EnvelopeGenerator;

public class MultiWaveOscillator implements Oscillator
{
	private SynthChannel channel;
	private MultiWaveOscillatorVariables vars;
	private EnvelopeGenerator syncEnv;
	private LFO lfo;
	private boolean master;
	private MultiWave multiWave;
	private Wave wave;
	private int waveSize;
	private int waveIndex;			// index of the Wave in the MultiWave
	private float k;				// product of period in samples * frequency in Hz
	private float k2;				// frequency / increment
	private float increment = 1f;	// wave increment for the nominal pitch
	private float currentIncrement;
	private float syncEnvDepth;
	private boolean sync;
	private float index = 0f;		// index of the sample within the Wave
	private float scalar = 1f;
	private float offset = 0f;
	private float frequency;		// nominal (start) frequency
	private float width;
	private float lfoDepth;
	private boolean widthMod;
	
	public MultiWaveOscillator(
			SynthChannel channel, 
			MultiWaveOscillatorVariables oscillatorVariables, 
			EnvelopeGenerator env,
			LFO lfo,
			float frequency) {
		this.channel = channel;
		vars = oscillatorVariables;
		syncEnv = env;
		this.lfo = lfo;
		master = vars.isMaster();
		multiWave = vars.getMultiWave();
		int octave = vars.getOctave();
		switch ( octave ) {
		case -2: frequency /= 4; break;
		case -1: frequency /= 2; break;
		case +1: frequency *= 2; break;
		case +2: frequency *= 4; break;
		}
		this.frequency = frequency;
		waveIndex = multiWave.getIndex(frequency);
		wave = multiWave.getWave(waveIndex);
		waveSize = wave.getData().length - 1;
		k = wave.getPeriod() * frequency;
		index = waveSize * multiWave.getWidthStartFactor(vars.getWidth());
	}
	
	public void setSampleRate(int sampleRate) {
		increment = k / sampleRate;
		k2 = frequency / increment;
	}
	
	public void update() {
		currentIncrement = increment * channel.getBendFactor() * vars.getDetuneFactor();
		syncEnvDepth = vars.getEnvelopeDepth();
		sync = syncEnv != null && syncEnvDepth > 0.01f && !master;
		width = vars.getWidth();
		lfoDepth = Math.min(width, 1f-width) * vars.getWidthLFODepth();
		widthMod = lfoDepth > 0.01f;
		if ( widthMod ) lfo.update();
		scalar = multiWave.getWidthScalar(width);
		offset = multiWave.getWidthOffset(width);
	}
	
	public float getSample(float mod, OscillatorControl control, boolean release) {
		float inc = currentIncrement * mod; 	// !!! 0 .. 2 instead of 0.5 .. 2 !!!
		if ( sync ) {
			if ( control.sync ) index = 0; 		// hard sync - aliases
			float env = syncEnv.getEnvelope(release);
			inc *= (1 + syncEnvDepth * env * env);
		}
		float sample = wave.get(index);
		float w = width;
		if ( widthMod ) w += lfoDepth * lfo.getSample();
		float ixShift = index + waveSize * w;
		if ( ixShift >= waveSize ) ixShift -= waveSize;
		sample -= wave.get(ixShift);  			// inverted phase shifted for PWM etc.
		index += inc;
		if ( index >= waveSize ) {				// once per wave cycle
			index -= waveSize;					// glitches shifted sample!
			int wi = multiWave.getIndex(k2 * inc);
			if ( wi != waveIndex ) {
				wave = multiWave.getWave(wi);
				waveIndex = wi;
			}
			if ( master ) control.sync = true;
		} 
		return sample * scalar + offset;
	}
}
