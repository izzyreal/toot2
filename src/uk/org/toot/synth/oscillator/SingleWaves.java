package uk.org.toot.synth.oscillator;

import java.util.List;
import java.util.Collections;

/**
 * Naive implementations of classic waves.
 * These waves are not band-limited so except for sine they alias.
 * @author st
 *
 */
public class SingleWaves 
{
	private static String SINE = "Sine";
	private static String SQUARE = "Square";
	private static String SAW = "Saw";
	private static String TRIANGLE = "Triangle";
	
	private static int size = 1024;
	
	private static List<String> names = new java.util.ArrayList<String>();
	
	static {
		names.add(SINE);		// no harmonics, so no aliasing
		names.add(TRIANGLE);	// no even harmonics, -12dB/Octave odd harmonic rolloff
		names.add(SQUARE);		// no even harmonics, -6dB/Octave odd harmonic rolloff
		names.add(SAW);			// even and odd harmonics, -6dB/Octave rolloff
	}
	
	public static List<String> getNames() {
		return Collections.unmodifiableList(names);
	}

	public static Wave create(String name) {
		if ( name.equals(SINE) ) {
			return createSineWave(size);
		} else if ( name.equals(SQUARE) ) {
			return createSquareWave(size);
		} else if ( name.equals(SAW) ) {
			return createSawtoothWave(size);
		} else if ( name.equals(TRIANGLE) ) {
			return createTriangleWave(size);
		}
		return null;
	}
	
	/**
	 * The actual size is likely to be slightly larger than the requested size
	 * to allow for efficient linear interpolation. The requested size is actually
	 * the size of the loopable data.
	 * @param aSize
	 */
	public static void setSize(int aSize) {
		size = aSize;
	}
	
	public static Wave createSquareWave(int nsamples) {
		float[] wave = new float[nsamples+1];
		int waveHalf = nsamples / 2;
		for ( int i = 0; i < waveHalf; i++) {
			wave[i] = 1f;
		}
		for ( int i = waveHalf; i < nsamples; i++) {
			wave[i] = -1f;
		}
		wave[nsamples] = wave[0];
		return new SingleWave(wave, wave.length-1);
	}

	public static Wave createSawtoothWave(int nsamples) {
		float[] wave = new float[nsamples+1];
		int waveHalf = nsamples / 2;
		for ( int i = 0; i < waveHalf; i++) {
			wave[i] = i / waveHalf;
			wave[i+waveHalf] = wave[i] - 1f;
		}
		wave[nsamples] = wave[0];
		return new SingleWave(wave, wave.length-1);
	}

	public static Wave createTriangleWave(int nsamples) {
		float[] wave = new float[nsamples+1];
		int waveHalf = nsamples / 2;
		int waveQtr = waveHalf / 2;
		for ( int i = 0; i < waveQtr; i++) {
			float v = i / waveQtr;
			wave[i] = v;
			wave[i+waveQtr] = 1f - v;
			wave[i+waveHalf] = -v;
			wave[i+waveHalf+waveQtr] = v - 1f;
		}
		wave[nsamples] = wave[0];
		return new SingleWave(wave, wave.length-1);
	}
	
	public static Wave createSineWave(int nsamples) {
		float[] wave = new float[nsamples+1];
		for ( int i = 0; i < nsamples; i++) {
			wave[i] = (float)Math.sin(Math.PI * 2 * i / nsamples);
		}
		wave[nsamples] = wave[0];
		return new SingleWave(wave, wave.length-1);
	}
	
}
