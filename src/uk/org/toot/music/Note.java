// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music;

/**
 * This class provides static methods to assist in the representation of a note
 * as an int. Such a note encodes time, pitch, level and duration.
 * The time is specified as sixty-fourth notes relative to the start of a bar, 0..127.
 * The duration is specified as sixty-fourth notes relative to the time, 
 * 0..255 such that the off time can be up to 4 bars later than the on time.
 * The pitch (or drum) is specified as a MIDI compatible pitch, 0..127
 * The level is specified as a MIDI-compatible velocity, 0..127.
 * The most significant bits represent time so that an array of ints
 * representing notes can be time-ordered by simple sorting.
 * Such sorting orders notes by time, then duration, then pitch, then level.
 * @author st
 *
 * Format 0ttttttt dddddddd 0ppppppp 0vvvvvvv
 * always positive
 * can be sorted by time (t)
 * uses 1/4 of positive int values, unused bits 8 and 16 are both zero
 * 
 */
public class Note extends TimedCoding
{
	private final static int DURATION_SHIFT = 16;
	private final static int DURATION_MASK = 0xff;	// 8 bits, 0..255
	private final static int PITCH_SHIFT = 8;
	private final static int PITCH_MASK = 0x7f;		// 7 bits, 0..127
	private final static int LEVEL_MASK = 0x7f;		// 7 bits, 0..127
	
	public static int create(int timeOn, int pitch, int level) {
		int note = create(timeOn);
		note = setDuration(note, 1); // shortest note duration, 1/64th
		note = setPitch(note, pitch);
		note = setLevel(note, level);
		return note;
	}
	
	/**
	 * Return the duration, in sixty-forths, of the specified note.
	 * @param note the int which contains the duration of the note
	 * @return the time, in sixty-fourths, of the duration.
	 */
	public static int getDuration(int note) {
		return (note >> DURATION_SHIFT) & DURATION_MASK;
	}
	
	public static int setDuration(int note, int time) {
		note &= ~(DURATION_MASK << DURATION_SHIFT);
		note |= (time & DURATION_MASK) << DURATION_SHIFT;
		return note;
	}
	
	public static int getPitch(int note) {
		return (note >> PITCH_SHIFT) & PITCH_MASK;
	}
	
	public static int setPitch(int note, int pitch) {
		note &= ~(PITCH_MASK << PITCH_SHIFT);
		note |= (pitch & PITCH_MASK) << PITCH_SHIFT;
		return note;
	}

	public static int getLevel(int note) {
		return note & LEVEL_MASK;
	}
	
	public static int setLevel(int note, int level) {
		note &= ~LEVEL_MASK;
		note |= level & LEVEL_MASK;
		return note;
	}
}
