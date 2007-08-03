//Copyright (C) 2007 Steve Taylor.
//Distributed under the Toot Software License, Version 1.0. (See
//accompanying file LICENSE_1_0.txt or copy at
//http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Track;

import uk.org.toot.midi.message.ChannelMsg;
import uk.org.toot.music.Note;

/**
 * This class is the abstract base class for automated composers.
 * @author st
 *
 */
public abstract class AbstractComposer extends BarComposer
{
	private float swingRatio = 1f; // default no swing

	/**
	 * Create a new AbstractComposer with the specified name, MIDI Program and
	 * MIDI Channel.
	 * @param name the name of this composer
	 * @param program the MIDI Program for the instrument we compose for
	 * @param channel the MIDI Channel for the instrument we compose for
	 */
	public AbstractComposer(String name, int program, int channel) {
		super(name, program, channel);
	}

	/**
	 * Render a bar of notes as MIDI to the specified Track from the
	 * specified start tick with the specified ticks per bar.
	 * @param notes the notes to render
	 * @param track the MIDI Track to render to
	 * @param startTick the tick at the start of the bar
	 * @param ticksPerBar the number of ticks per bar
	 */
	public void renderBar(int[] notes, Track track, 
			long startTick, int ticksPerBar) {
		final int channel = getChannel();
		MidiMessage msg;
		try {
			for ( int i = 0; i < notes.length; i++) {
				int note = notes[i];
				float timeOn = swing(Note.getTimeOn(note));
				int pitch = Note.getPitch(note);
				int level = Note.getLevel(note);
				long onTick = (int)(ticksPerBar * timeOn / Timing.COUNT);
				msg = ChannelMsg.createChannel(
						ChannelMsg.NOTE_ON, channel, pitch, level);
				track.add(new MidiEvent(msg, startTick + onTick));
				// note off
				msg = ChannelMsg.createChannel(
						ChannelMsg.NOTE_OFF, channel, pitch, 0);
				float timeOff = swing(Note.getTimeOff(note));
				long offTick = (int)(ticksPerBar * timeOff / Timing.COUNT);
				track.add(new MidiEvent(msg, startTick + offTick));
			}			
		} catch ( InvalidMidiDataException imde ) {
			System.err.println("Failed to render bar of "+getName());
		}
	}

	/**
	 * @return the ratio of the first eigth note to the second eigth note
	 * when a quarter note is divided into two with a swing or shuffle rhythm
	 */
	public float getSwingRatio() {
		return swingRatio;
	}

	/**
	 * Set the ratio of the first eigth note to the second eigth note
	 * when a quarter note is divied into two with a swing or shuffle rhythm
	 * @param ratio the ratio of the first eigth note to the second eigth note
	 */
	public void setSwingRatio(float ratio) {
		swingRatio = ratio;
	}

	/**
	 * Swing the timing of a sixty-fourth note timing such that when a
	 * quarter note is divided into two the second eigth note is delayed
	 * relative to its nominal position and all other sixty-fourth note
	 * timings are smoothly varied accordingly.
	 * @param time the timing index of a sixty-fourth note in a bar
	 * @return the swung timing
	 */
	public float swing(int time) {
		float swingFactor = swingRatio - 1;
		double angle = -Math.PI/2 + 2*Math.PI*(time%16)/16;
		return (float)(time + 4 * swingFactor * (Math.sin(angle)+1)/2);
	}

	/**
	 * Print a list of swing conversions for all sixty-fourth note
	 * timing indices.
	 */
	public void checkSwing() {
		for ( int i = 0; i < Timing.COUNT; i++) {
			System.out.println(i+" => "+swing(i));
		}
	}

	public Context getContext() {
		return (Context)super.getContext();
	}
	
	public static class Context extends BarComposer.Context
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
