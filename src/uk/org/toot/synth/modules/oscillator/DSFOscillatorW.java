// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.modules.oscillator;

import uk.org.toot.dsp.Cosine;
import uk.org.toot.dsp.Sine;

/**
 * This class implements an Oscillator using the Discrete Summation Formula 
 * as detailed by Wolfram.
 * @author st
 */
public class DSFOscillatorW
{
	private float a;
	private Sine sine1, sine2, sine3;
	private Cosine cosine;
	private double aN;
	
	/**
	 * Create a Discrete Summation Formula Oscillator with a spectrum of
	 * a fundamental frequency and N partial frequencies with amplitude rolling 
	 * off with respect to the fundamental.
	 * 
	 * @param wn - fundamental normalised frequency
	 * @param wp - unused
	 * @param a - partial rolloff weight 0..1
	 * @param np - number of partials, 1..
	 */
	public DSFOscillatorW(float wn, float wp, int np, float a) {
		assert ( wn > 0f && wn < 0.5f );
		assert np > 0;
		assert ( a >= 0 && a < 1f );
		this.a = a; 						// !!! SHOULDN'T BE CONSTANT
		// ensure the highest partial is below nyquist
		if ( wn * np >= Math.PI ) np = (int)(Math.PI / wn);
		aN = Math.pow(a, np); 	// !!! EXPENSIVE
		System.out.println("wn="+wn+", np="+np+", a="+a+", aN="+aN);
		sine1 = new Sine(wn * (np+1));
		sine2 = new Sine(wn * np); 	
		sine3 = new Sine(wn); 			
		cosine = new Cosine(wn); 			
	}
	
	public float getSample() {
		return (float)((((a * sine2.out() - sine1.out()) * aN + sine3.out()) * a) /
								(1 - 2 * a * cosine.out() + a * a));
	}
}
