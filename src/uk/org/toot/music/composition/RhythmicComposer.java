//Copyright (C) 2007 Steve Taylor.
//Distributed under the Toot Software License, Version 1.0. (See
//accompanying file LICENSE_1_0.txt or copy at
//http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

import uk.org.toot.music.Note;
import uk.org.toot.tonality.Key;

/**
 * This class represents a composer that only composes one drum/cymbal hit at the 
 * same time. Drums/cymbals are prioritised by the order they are added.
 * This class effectively represents a drummer's limb.
 * @author st
 *
 */
public class RhythmicComposer extends AbstractComposer 
{
	public int[] composeBar(Key key) {
		long timing = getContext().createTiming();
		int[] notes = new int[Long.bitCount(timing)];
		int n = 0;
		for ( int i = 0; i < Timing.COUNT; i++) {
			if ( (timing & (1L << i)) == 0 ) continue;
			notes[n++] = Note.create(i, getContext().nextDrum(), getContext().getLevel(i));
		}
		return notes;
	}

	public Context getContext() {
		return (Context)super.getContext();
	}
	
	public abstract static class Context extends AbstractComposer.Context
	{
		public abstract int nextDrum();
	}
	
	public static class SingleDrumContext extends Context
	{
		private int drum;
		
		public SingleDrumContext(int drum) {
			this.drum = drum;
		}
		
		public int nextDrum() {
			return drum;
		}
	}

	public static class DualDrumContext extends Context
	{
		private int drum1;
		private float probability1;
		private int drum2;
		
		public DualDrumContext(int drum1, float probability1, int drum2) {
			this.drum1 = drum1;
			this.probability1 = probability1;
			this.drum2 = drum2;
		}
		
		public int nextDrum() {
			return Math.random() < probability1 ? drum1 : drum2;
		}
	}

}
