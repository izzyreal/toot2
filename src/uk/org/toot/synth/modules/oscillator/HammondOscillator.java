// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.modules.oscillator;

import uk.org.toot.dsp.Phasor;

/**
 * A model of an oscillator for a Hammond drawbar organ, even though a
 * real Hammond drawbar organ has 91 continuously running tonewheels.
 * @author st
 */
public class HammondOscillator
{
	private int nsines = 0;
	
	private Phasor[] sines;
	private float[] levels;
	
	/*
	 * The drawbars are named according to organ stops
	 * 16'		  	0.5		1 octave below
	 *  5 1/3'	    1.5		a fifth above (3rd harmonic of octave below)
	 *  8'			1		fundamental
	 *  4'			2		1 octave above
	 *  2 2/3'		3		1 octaves and a fifth above
	 *  2'			4		2 octaves above
	 *  1 3/5'		5		2 octaves and a major third above
	 *  1 1/3'		6		2 octaves and a fifth above
	 *  1'			8		3 octaves above
	 * but see http://www.electricdruid.net/index.php?page=info.hammond
	 * for exact ratios which are sort of equal temperament.
	 * Ideally each Phasor would start at different times, high frequencies first,
	 * over a 1 to 40ms period depending on key velocity. 
	 */
	public HammondOscillator(float wn, float[] levels) {
		this.levels = levels;
		nsines = levels.length;
		sines = new Phasor[nsines];
		sines[0] = new Phasor(wn * 0.5, 		0);
		sines[1] = new Phasor(wn * 1.498823530, 0); 	
		sines[2] = new Phasor(wn, 				0);
		sines[3] = new Phasor(wn * 2, 			0);
		sines[4] = new Phasor(wn * 2.997647060, 0);
		sines[5] = new Phasor(wn * 4, 			0);
		sines[6] = new Phasor(wn * 5.040941178, 0);
		sines[7] = new Phasor(wn * 5.995294120, 0);
		sines[8] = new Phasor(wn * 8, 			0);
	}
	
	public float getSample() {
		float sample = 0f;
		for ( int i = 0; i < nsines; i++ ) {
			sample += sines[i].out() * levels[i];
		}
		return sample;
	}
}
