// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.modules.oscillator;

import uk.org.toot.audio.core.FloatDenormals;
import uk.org.toot.dsp.Cosine;
import uk.org.toot.dsp.Sine;

/**
 * This class implements an Oscillator using the Discrete Summation Formula 
 * by Moorer, 1975, as specified by Stilson & Smith.
 * @author st
 */
public class DSFOscillatorSS
{
	private float a;
	private int np;
	private Sine s1, s2, s3, s4;
	private Cosine cosine;
	private float aNm1;
	
	/**
	 * Create a Discrete Summation Formula Oscillator with a spectrum of
	 * a fundamental frequency, wn, and np-1 partial frequencies separated by wp, 
	 * with amplitude rolling off exponentially with increasing frequency.
	 * 
	 * @param wn - fundamental normalised frequency, > 0, < PI 
	 * @param wp - partial separation normalised frequency > 0
	 * @param a - partial rolloff weight 0..1
	 * @param np - number of partials, 1..
	 */
	public DSFOscillatorSS(float wn, float wp, int np, float a) {
		assert ( wn > 0f && wn < Math.PI );
		assert wp > 0f;
		assert np > 0;
		assert ( a >= 0 && a < 1f );
		this.a = a;
		// ensure the highest partial is below nyquist
		if ( wn + wp * np >= Math.PI ) np = (int)((Math.PI - wn) / wp);
		this.np = np;
		aNm1 = (float)Math.pow(a, np-1);
		s1 = new Sine(wn + wp * (np-1));
		s2 = new Sine(wn + wp * np); 	
		s3 = new Sine(wn - wp); 
		s4 = new Sine(wn);
		cosine = new Cosine(wp);
	}
	
	public void update(float a) {
		this.a = a;
		aNm1 = (float)Math.pow(a, np-1); 	// !!! EXPENSIVE		
	}
	
	public float getSample() {
		float denom = (1 - 2 * a * cosine.out() + a * a);
		if ( FloatDenormals.isDenormalOrZero(denom) ) return 0f;
		return (s4.out() - a * (s3.out() + aNm1 * (s2.out() - a * s1.out()))) /	denom;
	}
}
