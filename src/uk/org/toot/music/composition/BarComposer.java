// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

import javax.sound.midi.Track;

import uk.org.toot.tonality.Key;

public abstract class BarComposer 
{
	private String name;
	private int program;
	private int channel;
	private Context context;

	public BarComposer(String name, int program, int channel) {
		this.name = name;
		this.program = program;
		this.channel = channel;
	}
	/**
	 * Create a bar of notes in the specified Key.
	 * @param key the Key to compose in
	 * @return an int array representing packed notes
	 */
	public abstract int[] composeBar(Key key);

	/**
	 * Render a bar of notes to a MIDI Track starting with
	 * startTick with ticksPerBar.
	 * The inverse will be very complicated because it will
	 * have to cope with swing and any time offset!!!
	 * @param notes the notes to render as MIDI messages
	 * @param track the MIDI Track to render notes to
	 * @param startTick the tick at the start of the bar
	 * @param ticksPerBar the number of ticks per bar
	 */
	public abstract void renderBar(int[] notes, Track track, 
			long startTick, int ticksPerBar);

	/**
	 * @return the name of this composer
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the channel
	 */
	public int getChannel() {
		return channel;
	}
	/**
	 * @return the program
	 */
	public int getProgram() {
		return program;
	}

	public static class Context
	{
	
	}

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
}
