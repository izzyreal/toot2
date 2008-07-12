package uk.org.toot.synth.oscillator;

import uk.org.toot.synth.SynthChannel;

public class WaveOscillator implements Oscillator
{
	private float index = 0f;
	private float increment = 1f;
	private float[] wave;
	private int waveSize;
	private SynthChannel channel;
	private float bendFactor = 1f;
	private float k;
	
	public WaveOscillator(SynthChannel channel, WaveOscillatorVariables oscillatorVariables, int pitch) {
		this.channel = channel;
		Wave wave = oscillatorVariables.getWave();
		this.wave = wave.getData();
		waveSize = this.wave.length - 1;
		k = wave.getPeriod() * midiFreq(pitch);
	}
	
	// TODO move elsewhere
	protected float midiFreq(int pitch) { 
		return (float)(440.0 * Math.pow( 2.0, ((double)pitch - 69.0) / 12.0 )); 
	}
	 
	public void setSampleRate(int sampleRate) {
		increment = k / sampleRate;
	}
	
	public void update() {
		bendFactor = channel.getBendFactor();
	}
	
	public float getSample(float vibratoFactor, float pm) { // TODO fm and pm ?
		// lookup with linear interpolation
		int ix = (int)index;
		float frac = index - ix;
		float sample = (1f - frac) * wave[ix] + frac * wave[ix+1];
		// prepare next lookup index
		index += increment * bendFactor * vibratoFactor;
		if ( index >= waveSize ) index -= waveSize;
		return sample ;
	}
}
