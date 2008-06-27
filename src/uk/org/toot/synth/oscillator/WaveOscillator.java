package uk.org.toot.synth.oscillator;

public class WaveOscillator implements Oscillator
{
	private float index = 0f;
	private float increment = 1f;
	private float[] wave;
	private int waveSize;
	private float wavePeriod;
	private float frequency;
	
	public WaveOscillator(WaveOscillatorVariables oscillatorVariables, int pitch) {
		Wave wave = oscillatorVariables.getWave();
		this.wave = wave.getData();
		waveSize = this.wave.length - 1;
		this.wavePeriod = wave.getPeriod();
		frequency = midiFreq(pitch);
	}
	
	// TODO move elsewhere
	protected float midiFreq(int pitch) { 
		return (float)(440.0 * Math.pow( 2.0, ((double)pitch - 69.0) / 12.0 )); 
	}
	 
	public void setSampleRate(int rate) {
		float period = rate / frequency; // period in samples
		increment = wavePeriod / period;
//		System.out.print("["+(int)frequency+"/"+increment+"]");
	}
	
	public float getSample(float fm, float pm) { // TODO fm and pm ?
		int ix = (int)index;
		float frac = index - ix;
		float sample = (1f - frac) * wave[ix] + frac * wave[ix+1];
		//float sample = wave[(int)(index + 0.5f)]; // TODO interpolate?
		index += increment;
		if ( index >= waveSize ) index -= waveSize;
		return sample ;// / 10f; // -20dBFS (K-20)
	}
}
