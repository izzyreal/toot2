package uk.org.toot.synth.oscillator;

public class ClassicWaveforms 
{
	public static float[] createSquareWave(int nsamples) {
		float[] wave = new float[nsamples];
		int waveHalf = nsamples / 2;
		for ( int i = 0; i < waveHalf; i++) {
			wave[i] = 1f;
		}
		for ( int i = waveHalf; i < nsamples; i++) {
			wave[i] = -1f;
		}
		return wave;
	}

	public static float[] createSawtoothWave(int nsamples) {
		float[] wave = new float[nsamples];
		int waveHalf = nsamples / 2;
		for ( int i = 0; i < waveHalf; i++) {
			wave[i] = i / waveHalf;
			wave[i+waveHalf] = wave[i] - 1f;
		}
		return wave;
	}

	public static float[] createTriangleWave(int nsamples) {
		float[] wave = new float[nsamples];
		int waveHalf = nsamples / 2;
		int waveQtr = waveHalf / 2;
		for ( int i = 0; i < waveQtr; i++) {
			float v = i / waveQtr;
			wave[i] = v;
			wave[i+waveQtr] = 1f - v;
			wave[i+waveHalf] = -v;
			wave[i+waveHalf+waveQtr] = v - 1f;
		}
		return wave;
	}
	
	public static float[] createSineWave(int nsamples) {
		float[] wave = new float[nsamples];
		for ( int i = 0; i < nsamples; i++) {
			wave[i] = (float)Math.sin(Math.PI * 2 * i / nsamples);
		}
		return wave;
	}
	
}
