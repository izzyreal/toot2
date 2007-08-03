// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

/**
 * Assume sixteenths are spoken as "1 uh and uh 2 uh and uh 3 uh and uh 4 uh and uh".
 * 
 * @author st
 *
 */
public class Timing 
{
	public final static int COUNT = 64;
	
	public final static long ONE			= 0x0000000000000001L;
	public final static long ONE_UH			= 0x0000000000000010L;
	public final static long ONE_AND		= 0x0000000000000100L;
	public final static long ONE_AND_UH		= 0x0000000000001000L;
	
	public final static long TWO			= 0x0000000000010000L;
	public final static long TWO_UH			= 0x0000000000100000L;
	public final static long TWO_AND		= 0x0000000001000000L;
	public final static long TWO_AND_UH		= 0x0000000010000000L;
	
	public final static long THREE			= 0x0000000100000000L;
	public final static long THREE_UH		= 0x0000001000000000L;
	public final static long THREE_AND		= 0x0000010000000000L;
	public final static long THREE_AND_UH	= 0x0000100000000000L;
	
	public final static long FOUR			= 0x0001000000000000L;
	public final static long FOUR_UH		= 0x0010000000000000L;
	public final static long FOUR_AND		= 0x0100000000000000L;
	public final static long FOUR_AND_UH	= 0x1000000000000000L;
	
	public final static long ALL_DOWNBEATS  = ONE | TWO | THREE | FOUR;
	public final static long EVEN_DOWNBEATS = TWO | FOUR;
	public final static long ODD_DOWNBEATS  = ONE | THREE; 
	
	public final static long ALL_UPBEATS    = ONE_AND | TWO_AND | THREE_AND | FOUR_AND;
	public final static long EVEN_UPBEATS   = TWO_AND | FOUR_AND;
	public final static long ODD_UPBEATS    = ONE_AND | THREE_AND;
	
	/**
	 * Stochastic Binary Subdivision
	 * from Six Techniques for Algorithmic Music Composition, 1985
	 * by Peter S. Langston, Bellcore, Morristown, New Jersey
	 *
	 * This implementation sets bits in a long so is limited to 64th note resolution.
	 * The caller should probably, but not necessarily, zero the mask prior to calling.
	 * The binary aspect means it only relates sanely to 4/4.
	 */
	public static long subdivide(long mask, float density, int minres, int lo, int hi) {
		mask |= 1l << lo; // mark this division start
		int mid = (lo + hi) >> 1; // the point of next subdivision
		if ( Math.random() < density && hi - lo > minres) {
			mask = subdivide(mask, density, minres, lo, mid); // lower subdivision
			mask = subdivide(mask, density, minres, mid, hi); // higher subdivision
		}		
		return mask;
	}
	
	/**
	 * 
	 * @param density the probability of binary subdivision
	 * @param minnotelen e.g. 4 for quarter notes, 16 for sixteenth notes
	 * @return the long mask of timing subdivisions
	 */
	public static long subdivide(float density, int minnotelen) {
		if ( minnotelen > COUNT ) minnotelen = COUNT; // prevent zero resolution
		else if ( minnotelen < 1 ) minnotelen = 32; // !!!
		return subdivide(0, density, COUNT / minnotelen, 0, COUNT);		
	}
	
	public static long byProbabilities(int[] probabilities) {
		long timing = 0;
		int step = Timing.COUNT / probabilities.length;
		for ( int i = 0, p = 0; i < Timing.COUNT; i += step, p++) {
			if ( Math.random() > probabilities[p] ) continue;
			timing |= 1L << i;
		}
		return timing;
	}
}
