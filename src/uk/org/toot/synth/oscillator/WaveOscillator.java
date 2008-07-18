package uk.org.toot.synth.oscillator;

import uk.org.toot.synth.SynthChannel;

public class WaveOscillator implements Oscillator
{
	private SynthChannel channel;
	private WaveOscillatorVariables vars;
	private boolean master;
	private float level;
	private Wave wave;
	private int waveSize;
	private float k;				// product of period in samples * frequency in Hz
	private float increment = 1f;	// wave increment for the nominal pitch
	private float bentIncrement;
	private float syncEnvDepth;
	private boolean sync;
	private float detuneFactor;
	private float index = 0f;
	
	public WaveOscillator(SynthChannel channel, WaveOscillatorVariables oscillatorVariables, float frequency) {
		this.channel = channel;
		vars = oscillatorVariables;
		master = vars.isMaster();
		wave = vars.getWave();
		waveSize = wave.getData().length - 1;
		k = wave.getPeriod() * frequency;
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
	}
	
	public float getSample(float mod, float env, OscillatorControl control) {
		float inc = bentIncrement * (mod + 1);
		if ( !master ) {
			if ( sync ) {
				if ( control.sync ) index = 0; // hard sync
				inc *= (2 + (syncEnvDepth * env));
			}
			inc *= detuneFactor;
		}
		float sample = level * wave.get(index);
		index += inc;
		if ( index >= waveSize ) {
			index -= waveSize;
			if ( master ) control.sync = true;
		} 
		return sample;
	}
}
