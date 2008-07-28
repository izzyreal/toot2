package uk.org.toot.synth.oscillator;

import uk.org.toot.synth.SynthChannel;

public class SingleWaveOscillator implements Oscillator
{
	private SynthChannel channel;
	private SingleWaveOscillatorVariables vars;
	private boolean master;
	private Wave wave;
	private int waveSize;
	private float k;				// product of period in samples * frequency in Hz
	private float increment = 1f;	// wave increment for the nominal pitch
	private float bentIncrement;
	private float syncEnvDepth;
	private boolean sync;
	private float detuneFactor;
	private float index = 0f;
	
	public SingleWaveOscillator(SynthChannel channel, SingleWaveOscillatorVariables oscillatorVariables, float frequency) {
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
		syncEnvDepth = vars.getEnvelopeDepth();
		sync = syncEnvDepth > 0.01f;
		detuneFactor = vars.getDetuneFactor();
	}
	
	public float getSample(float mod, float env, float lfo, OscillatorControl control) {
		float inc = bentIncrement * (mod + 1); // !!! 0 .. 2 instead of 0.5 .. 2 !!!
		if ( !master ) {
			if ( sync ) {
				if ( control.sync ) index = 0; // hard sync
				inc *= (2 + (syncEnvDepth * env));
			}
			inc *= detuneFactor;
		}
		float sample = wave.get(index);
		index += inc;
		if ( index >= waveSize ) {
			index -= waveSize;
			if ( master ) control.sync = true;
		} 
		return sample;
	}
}
