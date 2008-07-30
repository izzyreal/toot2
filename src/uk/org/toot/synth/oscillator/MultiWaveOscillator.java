package uk.org.toot.synth.oscillator;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.envelope.EnvelopeGenerator;

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
	private float k;				// product of period in samples * frequency in Hz
	private float increment = 1f;	// wave increment for the nominal pitch
	private float bentIncrement;
	private float syncEnvDepth;
	private boolean sync;
	private float detuneFactor;
	private float index = 0f;
	private float scalar = 1f;
	private float offset = 0f;
	private float frequency;
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
		this.frequency = frequency;
		wave = getWave(frequency);
		waveSize = wave.getData().length - 1;
		k = wave.getPeriod() * frequency;
		index = waveSize * multiWave.getWidthStartFactor(vars.getWidth());
	}
	
	public void setSampleRate(int sampleRate) {
		increment = k / sampleRate;
	}
	
	public void update() {
		bentIncrement = increment * channel.getBendFactor();
		syncEnvDepth = vars.getEnvelopeDepth();
		sync = syncEnv != null && syncEnvDepth > 0.01f;
		detuneFactor = vars.getDetuneFactor();
		width = vars.getWidth();
		lfoDepth = vars.getWidthLFODepth() / 2.002f;
		widthMod = lfoDepth > 0.01f;
		if ( widthMod ) lfo.update();
		scalar = multiWave.getWidthScalar(width);
		offset = multiWave.getWidthOffset(width);
	}
	
	public float getSample(float mod, OscillatorControl control, boolean release) {
		float inc = bentIncrement * (mod + 1); 	// !!! 0 .. 2 instead of 0.5 .. 2 !!!
		if ( !master ) {
			if ( sync ) {
				if ( control.sync ) index = 0; 	// hard sync - aliases
				float env = syncEnv.getEnvelope(release);
				inc *= (2 + (syncEnvDepth * env * env));
			}
			inc *= detuneFactor;
		}
		float sample = wave.get(index);
		float w = width;
		if ( widthMod ) {
			w += lfoDepth * lfo.getSample();
			w = Math.abs(w);
			if ( w > 0.99f ) w = 0.99f;
			else if ( w < 0.01f ) w = 0.01f;
		}
		float ixShift = index + waveSize*w;
		if ( ixShift >= waveSize ) ixShift -= waveSize;
		sample -= wave.get(ixShift);  			// inverted phase shifted for PWM etc.
		index += inc;
		if ( index >= waveSize ) {
			index -= waveSize;
			wave = getWave(frequency * inc / increment);
			if ( master ) control.sync = true;
		} 
		return sample * scalar + offset;
	}

	protected Wave getWave(float freq) {
		return multiWave.getWave(multiWave.getIndex(freq));
	}

}
