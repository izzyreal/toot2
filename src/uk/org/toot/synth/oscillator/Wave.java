package uk.org.toot.synth.oscillator;

/**
 * This class represents the wave for WaveOScillator.
 * @author st
 *
 */
public class Wave 
{
	private float[] data;
	private float period;

	public Wave(float[] data, float period) {
		this.data = data;
		this.period = period;
	}
	
	/**
	 * @return the data
	 */
	public float[] getData() {
		return data;
	}

	/**
	 * @return the period of the wave signal in samples, which may be less
	 * than the wave length for some waves.
	 */
	public float getPeriod() {
		return period;
	}
}
