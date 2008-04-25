package uk.org.toot.synth.oscillator;

public class WaveOscillator implements Oscillator
{
	private float index = 0f;
	private float increment = 1f;
	private float[] wave;
	private float frequency;
	
	public WaveOscillator(float[] wave, int pitch) {
		this.wave = wave;
		frequency = midiFreq(pitch);
	}
	
	// TODO move elsewhere
	protected float midiFreq(int pitch) { 
		return (float)(440.0 * Math.pow( 2.0, ((double)pitch - 69.0) / 12.0 )); 
	}
	 
	public void setSampleRate(int rate) {
		float period = rate / frequency; // period in samples
		increment = wave.length / period;
//		System.out.print("["+(int)frequency+"/"+increment+"]");
	}
	
	public float getSample(float fm, float pm) { // TODO fm and pm ?
		float sample = wave[(int)(index + 0.5f) % wave.length]; // TODO interpolate?
		index += increment;
		if ( index >= wave.length ) index -= wave.length;
		return sample / 10f; // -20dBFS (K-20)
	}
}
