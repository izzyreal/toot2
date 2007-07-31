// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

import javax.sound.midi.Track;

import uk.org.toot.tonality.Key;
import java.util.List;
import java.util.Arrays;

public class CompoundComposer extends BarComposer 
{
	private List<AbstractComposer> composers;

	public CompoundComposer(String name, int program, int channel) {
		super(name, program, channel);
		composers = new java.util.ArrayList<AbstractComposer>();
	}
	
	@Override
	public int[] composeBar(Key key) {
		// TODO Auto-generated method stub
		int[][] allnotes = new int[composers.size()][];
		int comp = 0;
		int noteCount = 0;
		for ( AbstractComposer composer : composers ) {
			allnotes[comp] = composer.composeBar(key);
			noteCount += allnotes[comp].length;
			comp += 1;
		}
		int[] notes = new int[noteCount];
		int n = 0;
		for ( int i = 0; i < allnotes.length; i++ ) {
			for ( int j = 0; j < allnotes[i].length; j++ ) {
				notes[n++] = allnotes[i][j];
			}
		}
		Arrays.sort(notes);
		return notes;
	}

	@Override
	public void renderBar(int[] notes, Track track, long startTick,
			int ticksPerBar) {
		// Track will sort out time ordering
		for ( AbstractComposer composer : composers ) {
			composer.renderBar(notes, track, startTick, ticksPerBar);
		}
	}

	protected void addComposer(AbstractComposer composer) {
		if ( composer.getChannel() != getChannel() ||
		     composer.getProgram() != getProgram() ) {
			throw new IllegalArgumentException(composer.getName()+
					" should be MIDI Channel "+getChannel());
		}
		composers.add(composer);
	}
	
	protected void removeComposer(AbstractComposer composer) {
		composers.remove(composer);
	}
}
