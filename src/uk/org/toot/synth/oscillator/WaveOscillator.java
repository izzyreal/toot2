package uk.org.toot.synth.oscillator;

import uk.org.toot.synth.SynthChannel;

public class WaveOscillator implements Oscillator
{
	private SynthChannel channel;
	private WaveOscillatorVariables vars;
	private Wave wave;
	private int waveSize;
	private float k;				// product of period in samples * frequency in Hz
	private float increment = 1f;	// wave increment for the nominal pitch
	private float bentIncrement;
	private float subLevel;
	private float syncEnvDepth;
	private boolean sub; 			// sub-oscillator using same wave
	private boolean sync;			// hard sync of sub-oscillator
	private float index = 0f;
	private float subIndex = 0f;
	
	public WaveOscillator(SynthChannel channel, WaveOscillatorVariables oscillatorVariables, int pitch) {
		this.channel = channel;
		vars = oscillatorVariables;
		wave = vars.getWave();
		waveSize = wave.getData().length - 1;
		k = wave.getPeriod() * SynthChannel.midiFreq(pitch);
	}
	
	public void setSampleRate(int sampleRate) {
		increment = k / sampleRate;
	}
	
	public void update() {
		bentIncrement = increment * channel.getBendFactor();
		subLevel = vars.getSubLevel();
		syncEnvDepth = vars.getEnvelopeDepth();
		sub = subLevel > 0.01f;
		sync = syncEnvDepth > 0.01f;
	}
	
	public float getSample(float mod, float env) {
		float inc = bentIncrement * (mod + 1);
		float sample = wave.get(index);
		index += inc;
		if ( index >= waveSize ) {
			index -= waveSize;
			if ( sync ) subIndex = 0f; // hard sync
		} 
		if ( sub ) {
			sample += subLevel * wave.get(subIndex);
			subIndex += inc * ( sync ? (2 + (syncEnvDepth * env)) : 1.003f );
			if ( subIndex >= waveSize ) {
				subIndex -= waveSize;
			}
		}

		return sample;
	}
}
