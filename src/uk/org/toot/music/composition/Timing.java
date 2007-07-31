// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

public class Timing 
{
	public final static int COUNT = 64;
	
	// 1, 2, 3, 4, i.e house kick
	public final static long ALL_DOWNBEATS = 0x001000100010001L;
	
	// 2, 4, i.e. house snare
	public final static long EVEN_DOWNBEATS = 0x001000000010000L;
	
	// 1, 3
	public final static long ODD_DOWNBEATS  = 0x000000100000001L; 
	
	// 1and, 2and, 3and, 4and, i.e. house open hat
	public final static long ALL_UPBEATS   = 0x100010001000100L;
	
	// 2and, 4and
	public final static long EVEN_UPBEATS   = 0x100000001000000L;
	
	// 1and, 3and
	public final static long ODD_UPBEATS    = 0x000010000000100L;
	
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
	public static long subdivide(long mask, float density, int minnotelen) {
		if ( minnotelen > COUNT ) minnotelen = COUNT; // prevent zero resolution
		return subdivide(mask, density, COUNT / minnotelen, 0, COUNT);		
	}
}
