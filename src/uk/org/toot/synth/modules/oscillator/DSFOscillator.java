// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.modules.oscillator;

/**
 * This class implements an Oscillator using the Discrete Summation Formula 
 * by Moorer, 1975, as modified by Stilson.
 * Efficient recursive different equation sine oscillators are used rather than
 * costly math functions.
 * @author st
 */
public class DSFOscillator
{
	private float a;
	private Sine sine1, sine2, sine3, sine4, cosine;
	private float aNm1;
	
	/**
	 * Create a Discrete Summation Formula Oscillator with a spectrum of
	 * a fundamental frequency and N partial frequencies with amplitude rolling 
	 * off with respect to the fundamental.
	 * 
	 * @param wn - fundamental normalised frequency
	 * @param wp - partial separation normalised frequency, not zero!
	 * @param a - partial rolloff weight 0..1
	 * @param np - number of partials, 1..
	 */
	public DSFOscillator(float wn, float wp, float np, float a) {
		assert ( wn > 0f && wn < 0.5f );
		assert wp != 0f;
		assert np > 0;
		assert ( a >= 0 && a <= 1f );
		this.a = a; 						// !!! SHOULDN'T BE CONSTANT
		// ensure the highest partial is below nyquist
		if ( wn + wp * np > 0.5f ) np = (int)((0.5f - wn) / wp);
		aNm1 = (float)Math.pow(a, np-1); 	// !!! EXPENSIVE
		// x represents the fundamental frequency, wn
		// fi represents the partial separation frequency, wp
		sine1 = new Sine(wp + (np-1) * wn, 0); 			// sin((N-1)*x+fi)
		sine2 = new Sine(wp + np * wn, 0); 				// sin(N*x+fi)
		sine3 = new Sine(wp + wn, 0); 					// sin(x+fi)
		sine4 = new Sine(wp, 0); 						// sin(fi)
		cosine = new Sine(wn, (float)(Math.PI / 2)); 	// cos(x)
	}
	
	// http://musicdsp.org/showArchiveComment.php?ArchiveID=68
	public float getSample() {
		float s1 = aNm1 * sine1.out();
		float s2 = aNm1 * a * sine2.out();
		float s3 = a * sine3.out();
		float s4 = 1f - (2 * a * cosine.out()) + (a * a);
		if ( s4 == 0 )
			return 0;
		else
			return (sine4.out() - s3 - s2 + s1) / s4; 
	}
	
	// http://musicdsp.org/showArchiveComment.php?ArchiveID=9
	// A 2nd order harmonic oscillator, as described by RBJ
	private class Sine
	{
		private float y0, y1, y2;
		private float b1;
		
		/**
		 * @param w - the normalised angular frequency, 2*PI*f/sampleRate
		 * @param theta - the initial phase in radians
		 */
		public Sine(float w, float theta) {
			b1 = 2f * (float)Math.cos(w);
			y1 = (float)Math.sin(theta - w);
			y2 = (float)Math.sin(theta - 2 * w);
		}
		
		public float out() {
			y0 = b1 * y1 - y2;
			y2 = y1;
			y1 = y0;
			return y0;
		}
	}
}
