package uk.org.toot.synth.oscillator;

public interface Wave 
{
	/**
	 * @return the data
	 */
	float[] getData();

	/**
	 * @return the period of the wave signal in samples, which may be less
	 * than the wave length for some waves.
	 */
	float getPeriod();

	/**
	 * @param index the floating point index
	 * @return a linearly interpolated sample of the wave
	 */
	float get(float index);
}
