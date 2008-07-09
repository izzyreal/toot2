package uk.org.toot.synth.oscillator;

import uk.org.toot.synth.SynthChannel;

public class WaveOscillator implements Oscillator
{
	private int sampleRate;
	private float index = 0f;
	private float increment = 1f;
	private float[] wave;
	private int waveSize;
	private float wavePeriod;
	private float frequency;
	private float startFrequency;
	private SynthChannel channel;
	private float bendFactor = 1f;
	
	public WaveOscillator(SynthChannel channel, WaveOscillatorVariables oscillatorVariables, int pitch) {
		this.channel = channel;
		Wave wave = oscillatorVariables.getWave();
		this.wave = wave.getData();
		waveSize = this.wave.length - 1;
		this.wavePeriod = wave.getPeriod();
		frequency = startFrequency = midiFreq(pitch);
	}
	
	// TODO move elsewhere
	protected float midiFreq(int pitch) { 
		return (float)(440.0 * Math.pow( 2.0, ((double)pitch - 69.0) / 12.0 )); 
	}
	 
	protected void updateIncrement() {
		float period = sampleRate / frequency; // period in samples
		increment = wavePeriod / period;
	}
	
	public void setSampleRate(int rate) {
		sampleRate = rate;
		updateIncrement();
	}
	
	public void update() {
		float bf = channel.getBendFactor();
		if ( bf != bendFactor ) {
			bendFactor = bf;
			frequency = startFrequency * bendFactor;
			updateIncrement();
		}
	}
	
	public float getSample(float fm, float pm) { // TODO fm and pm ?
		int ix = (int)index;
		float frac = index - ix;
		float sample = (1f - frac) * wave[ix] + frac * wave[ix+1];
		index += increment;
		if ( index >= waveSize ) index -= waveSize;
		return sample ;// / 10f; // -20dBFS (K-20)
	}
}
