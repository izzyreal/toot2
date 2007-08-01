// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

import uk.org.toot.music.Note;
import uk.org.toot.tonality.Key;

/**
 * @author st
 *
 */
public class RhythmicComposer extends AbstractComposer 
{
	private int drum;
	
	public RhythmicComposer(String name, int program, int channel, int drum) {
		super(name, program, channel);
		this.drum = drum;
	}
	
	public int[] composeBar(Key key) {
		long timing = getContext().getJamTiming();
		timing = Timing.subdivide(timing, getContext().getDensity(), getContext().getMinNoteLen());
		timing &= ~getContext().getClearTiming();
		int[] notes = new int[Long.bitCount(timing)];
		int n = 0;
		for ( int i = 0; i < Timing.COUNT; i++) {
			if ( (timing & (1l << i)) == 0 ) continue;
			notes[n++] = Note.create(i, drum, getContext().getLevel());
		}
		return notes;
	}
}
