package uk.org.toot.synth.oscillator;

import uk.org.toot.synth.SynthChannel;

public class MultiWaveOscillator implements Oscillator
{
	private SynthChannel channel;
	private MultiWaveOscillatorVariables vars;
	private boolean master;
	private float level;
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
	private float shift = 0;
	
	public MultiWaveOscillator(SynthChannel channel, MultiWaveOscillatorVariables oscillatorVariables, float frequency) {
		this.channel = channel;
		vars = oscillatorVariables;
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
		level = vars.getLevel();
		syncEnvDepth = vars.getEnvelopeDepth();
		sync = syncEnvDepth > 0.01f;
		detuneFactor = vars.getDetuneFactor();
		float width = vars.getWidth();
		scalar = multiWave.getWidthScalar(width);
		offset = multiWave.getWidthOffset(width);
		shift = width * waveSize;
	}
	
	public float getSample(float mod, float env, OscillatorControl control) {
		if ( level < 0.02 ) return 0; // TESTING ONLY
		float inc = bentIncrement * (mod + 1); // !!! 0 .. 2 instead of 0.5 .. 2 !!!
		if ( !master ) {
			if ( sync ) {
				if ( control.sync ) index = 0; // hard sync - aliases
				inc *= (2 + (syncEnvDepth * env));
			}
			inc *= detuneFactor;
		}
		float sample = wave.get(index);
		float ixShift = index + shift;
		if ( ixShift >= waveSize ) ixShift -= waveSize;
		sample -= wave.get(ixShift);  // inverted phase shifted for PWM etc.
		index += inc;
		if ( index >= waveSize ) {
			index -= waveSize;
			wave = getWave(frequency * inc / increment);
			if ( master ) control.sync = true;
		} 
		return level * (sample * scalar + offset);
	}

	protected Wave getWave(float freq) {
		return multiWave.getWave(multiWave.getIndex(freq));
	}

}
