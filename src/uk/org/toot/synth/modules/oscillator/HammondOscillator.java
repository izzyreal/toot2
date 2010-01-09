// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.modules.oscillator;

import uk.org.toot.dsp.Sine;

/**
 * A model of a Hammond oscillator.
 * The drawbars are named according to organ stops
 * 16'		  	0.5		1 octave below
 *  5 1/3'	    1.5		a fifth above (out of logical sequence)
 *  8'			1		fundamental
 *  4'			2		1 octave above
 *  2 2/3'		3		1 octaves and a fifth above
 *  2'			4		2 octaves above
 *  1 3/5'		5		2 octaves and a major third above
 *  1 1/3'		6		2 octaves and a fifth above
 *  1'			8		3 octaves above
 * @author st
 */
public class HammondOscillator
{
	private int nsines = 0;
	
	private Sine[] sines;
	private float[] levels;
	
	public HammondOscillator(float wn, float[] levels) {
		this.levels = levels;
		nsines = levels.length;
		sines = new Sine[nsines];
		sines[0] = new Sine(wn * 0.5);  // sub octave
		sines[1] = new Sine(wn * 1.5); 	// 2nd harmonic of sub octave
		sines[2] = new Sine(wn);
		sines[3] = new Sine(wn * 2);
		sines[4] = new Sine(wn * 3);
		sines[5] = new Sine(wn * 4);
		sines[6] = new Sine(wn * 5);
		sines[7] = new Sine(wn * 6);
		sines[8] = new Sine(wn * 8);
	}
	
	public float getSample() {
		float sample = 0f;
		for ( int i = 0; i < nsines; i++ ) {
			sample += sines[i].out() * levels[i];
		}
		return sample;
	}
}
