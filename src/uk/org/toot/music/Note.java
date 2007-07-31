// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music;

/**
 * This class provides static methods to assist in the representation of a note
 * as an int. Such a note encodes on time, pitch, level and off time.
 * The on time is specified as sixty-fourth notes relative to the start of a bar, 0..63.
 * The off time is specified as sixty-fourth notes relative to the start of a bar, 
 * 0..255 such that the off time can be up to 3 bars later than the on time.
 * The pitch (or drum) is specified as a MIDI compatible pitch, 0..127
 * The level is specified as a MIDI-compatible velocity, 0..127.
 * The most significant bits represent time so that an array of ints
 * representing notes can be time-ordered by simple sorting.
 * @author st
 *
 */
public class Note 
{
	private final static int TIME_ON_SHIFT = 24;
	private final static int TIME_ON_MASK = 0x3f;
	private final static int TIME_OFF_SHIFT = 16;
	private final static int TIME_OFF_MASK = 0xff;
	private final static int PITCH_SHIFT = 8;
	private final static int PITCH_MASK = 0x7f;
	private final static int LEVEL_MASK = 0x7f;
	
	public static int create(int timeOn, int pitch, int level) {
		int note = 0;
		note = setTimeOn(note, timeOn);
		note = setTimeOff(note, timeOn+1); // shortest note duration, 1/64th
		note = setPitch(note, pitch);
		note = setLevel(note, level);
		return note;
	}
	
	public static int create(int timeOn, int pitch) {
		return create(timeOn, pitch, 100);
	}
	
	public static int create(int timeOn) {
		return create(timeOn, 64, 100);
	}
	
	/**
	 * Return the time on, in sixty-forths, of the specified note.
	 * @param note the int which contains the time of the note on
	 * @return the time, in sixty-fourths, of the note on.
	 */
	public static int getTimeOn(int note) {
		return (note >> TIME_ON_SHIFT) & TIME_ON_MASK;
	}
	
	public static int setTimeOn(int note, int time) {
		note &= ~(TIME_ON_MASK << TIME_ON_SHIFT);
		note |= (time & TIME_ON_MASK) << TIME_ON_SHIFT;
		return note;
	}
	
	/**
	 * Return the time off, in sixty-forths, of the specified note.
	 * @param note the int which contains the time off of the note
	 * @return the time, in sixty-fourths, of the note off.
	 */
	public static int getTimeOff(int note) {
		return (note >> TIME_OFF_SHIFT) & TIME_OFF_MASK;
	}
	
	public static int setTimeOff(int note, int time) {
		note &= ~(TIME_OFF_MASK << TIME_OFF_SHIFT);
		note |= (time & TIME_OFF_MASK) << TIME_OFF_SHIFT;
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
