//Copyright (C) 2007 Steve Taylor.
//Distributed under the Toot Software License, Version 1.0. (See
//accompanying file LICENSE_1_0.txt or copy at
//http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

/**
 * This class is the abstract base class for automated composers.
 * @author st
 *
 */
public abstract class AbstractComposer implements BarComposer
{
	private Context context;

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	public static class Context
	{
		private int level = 64; // default medium level
		private float density = 0.90f;
		private int minNoteLen = 16;
		private long jamTiming = 0;
		private long clearTiming = 0;
		private long accentTiming = 0;
		private int accent = 0;
		private int[] probabilities;

		public long createTiming() {
			long timing = 0;
			int[] probs = getTimingProbabilities();
			if ( probs == null ) {
				timing = getJamTiming();
				timing |= Timing.subdivide(getDensity(), getMinNoteLen());
				timing &= ~getClearTiming();
			} else {
				timing = Timing.byProbabilities(probs);
			}
			return timing;
		}
		
		/**
		 * @return the level
		 */
		public int getLevel(int time) {
			int lvl = level;
			if ( accent != 0 && (accentTiming & (1 << time)) != 0 ) {
				lvl += accent;
			}
			if ( lvl < 0 ) lvl = 0;
			else if ( lvl > 127 ) lvl = 127;
			return lvl;
		}

		/**
		 * @param level the level to set
		 */
		public void setLevel(int level) {
			if ( level < 0 || level > 127 ) {
				throw new IllegalArgumentException("require 0 <= level <= 127");
			}
			this.level = level;
		}

		/**
		 * @return the density
		 */
		public float getDensity() {
			return density;
		}

		/**
		 * @param density the density to set
		 */
		public void setDensity(float density) {
			this.density = density;
		}

		/**
		 * @return the minnotelen
		 */
		public int getMinNoteLen() {
			return minNoteLen;
		}

		/**
		 * @param minnotelen the minnotelen to set
		 */
		public void setMinNoteLen(int minnotelen) {
			if ( Integer.bitCount(minnotelen) != 1 ) {
				throw new IllegalArgumentException("minnotelen must be a power of 2, from 1 to 64");
			}
			this.minNoteLen = minnotelen;
		}

		/**
		 * @return the clearTiming
		 */
		public long getClearTiming() {
			return clearTiming;
		}

		/**
		 * @param clearTiming the clearTiming to set
		 */
		public void setClearTiming(long clearTiming) {
			this.clearTiming = clearTiming;
		}

		/**
		 * @return the jamTiming
		 */
		public long getJamTiming() {
			return jamTiming;
		}

		/**
		 * @param jamTiming the jamTiming to set
		 */
		public void setJamTiming(long jamTiming) {
			this.jamTiming = jamTiming;
		}

		/**
		 * @return the accentTiming
		 */
		public long getAccentTiming() {
			return accentTiming;
		}

		/**
		 * @param accentTiming the accentTiming to set
		 */
		public void setAccentTiming(long accentTiming) {
			this.accentTiming = accentTiming;
		}

		/**
		 * @return the accent
		 */
		public int getAccent() {
			return accent;
		}

		/**
		 * @param accent the accent to set
		 */
		public void setAccent(int accent) {
			if ( accent < -127 || accent > 127 ) {
				throw new IllegalArgumentException("require -127 <= accent <= 127");
			}
			this.accent = accent;
		}

		/**
		 * Set the timing probabilities.
		 * These replace the use of of 
		 * setJamTiming(), equivalent to probability 1, 
		 * setClearTiming(), equivalent to probability 0,
		 * setDensity(), related to probabilities
		 * setMinNoteLen(), equivalent to the probability array length
		 * @param probs
		 */
		public void setTimingProbabilities(int[] probs) {
			if ( probs != null && Integer.bitCount(probs.length) != 1 ) {
				throw new IllegalArgumentException("probability array should be null or have a power of 2 length");
			}
			probabilities = probs;
		}

		public int[] getTimingProbabilities() {
			return probabilities;
		}
	}
}
