// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

import uk.org.toot.music.Note;
import uk.org.toot.tonality.*;

/**
 * This class composes melodies, one bar at a time.
 * @author st
 *
 */
public class TonalComposer extends AbstractComposer
{
	private int currentPitch;
	private int minPitch;
	private int maxPitch;
	private int maxPitchChange = 3;
	private int minPoly = 3;
	private int maxPoly = 5;
	private float legato = 1.0f;
	private float melodyProbability = 0f; // probability of melody (single notes)
	private float repeatPitchProbability = 0.25f;
	
	public TonalComposer(String name, int program, int channel, int minPitch, int maxPitch) {
		super(name, program, channel);
		this.minPitch = minPitch;
		this.maxPitch = maxPitch;
		currentPitch = minPitch + (int)(Math.random() * (1 + maxPitch - minPitch));
	}

	public int[] composeBar(Key key) {
		long timing = getJamTiming();
		timing = Timing.subdivide(timing, getDensity(), getMinNoteLen());
		timing &= ~getClearTiming();
		int n = Long.bitCount(timing);
		int[] polys = new int[n];
		int m = 0;
		for ( int i = 0; i < n; i++) {
			if ( Math.random() >= getMelodyProbability() ) {
				polys[i] = getMinPoly() + (int)(Math.random() * (1 + getMaxPoly() - getMinPoly()));
			} else {
				polys[i] = 1; // a melody
			}
			m += polys[i];
		}
		int[] notes = new int[m];
		n = 0;
		m = 0;
		int[] chordNotes;
		int offset;
		for ( int i = 0; i < Timing.COUNT; i++) {
			if ( (timing & (1l << i)) == 0 ) continue;
			currentPitch = nextPitch(currentPitch, key);
			if ( polys[n] > 1 ) {
				chordNotes = key.getChordNotes(key.index(currentPitch), polys[n], ChordMode.TERTIAN);
				offset = currentPitch - chordNotes[0];
				for ( int p = 0; p < polys[n]; p++ ) {
					notes[m++] = Note.create(i, chordNotes[p] + offset, getLevel());
				}
			} else {
				notes[m++] = Note.create(i, currentPitch, getLevel());
			}
			n += 1;
		}
		fixupOffTimes(notes);
		return notes;
	}

	protected void fixupOffTimes(int[] notes) {
		for ( int i = 0; i < notes.length; i++ ) {
			// off up until next note at later time on or end of bar
			int note = notes[i];
			int onTime = Note.getTimeOn(note);
			int offTime = Timing.COUNT; // default end of bar
			if ( i < notes.length - 1 ) { // not final note in bar
				for ( int j = i + 1; j < notes.length; j++) {
					if ( Note.getTimeOn(notes[j]) > onTime ) {
						offTime = Note.getTimeOn(notes[j]);
						break; // found a later note
					}
				}
			}
			int diff = Math.max(1, (int)(getLegato() * (offTime - onTime)));
			offTime = onTime + diff;
			notes[i] = Note.setTimeOff(note, offTime);
		}
	}

	protected int nextPitch(int pitch, Key key) {
		if ( Math.random() > repeatPitchProbability ) {
			int offset = (int)((2 * maxPitchChange + 1) * Math.random() - maxPitchChange);
			// don't get stuck at min or max pitches
			if ( pitch == minPitch && offset < 0 || 
					pitch == maxPitch && offset > 0 ) {
				offset = -offset;
			}
			pitch = key.getRelativePitch(pitch, offset);
			if ( pitch < minPitch ) pitch = minPitch;
			else if ( pitch > maxPitch ) pitch = maxPitch;
		}
		return key.diatonicPitch(pitch);
	}

	/**
	 * @return the minPitch
	 */
	public int getMinPitch() {
		return minPitch;
	}

	/**
	 * @param minPitch the minPitch to set
	 */
	public void setMinPitch(int minPitch) {
		this.minPitch = minPitch;
	}

	/**
	 * @return the maxPitch
	 */
	public int getMaxPitch() {
		return maxPitch;
	}

	/**
	 * @param maxPitch the maxPitch to set
	 */
	public void setMaxPitch(int maxPitch) {
		this.maxPitch = maxPitch;
	}

	/**
	 * @return the maxPitchChange
	 */
	public int getMaxPitchChange() {
		return maxPitchChange;
	}

	/**
	 * @param maxPitchChange the maxPitchChange to set
	 */
	public void setMaxPitchChange(int maxPitchChange) {
		this.maxPitchChange = maxPitchChange;
	}

	/**
	 * @return the proportion of full legato that a note sustains
	 */
	public float getLegato() {
		return legato;
	}

	/**
	 * Set the proportion of full legato that a note should sustain.
	 * @param legato the proportion of full legato, 0..1f
	 */
	public void setLegato(float legato) {
		this.legato = legato;
	}

	/**
	 * @return the maxPoly
	 */
	public int getMaxPoly() {
		return maxPoly;
	}

	/**
	 * @param maxPoly the maxPoly to set
	 */
	public void setMaxPoly(int maxPoly) {
		this.maxPoly = maxPoly;
	}

	/**
	 * @return the minPoly
	 */
	public int getMinPoly() {
		return minPoly;
	}

	/**
	 * @param minPoly the minPoly to set
	 */
	public void setMinPoly(int minPoly) {
		this.minPoly = minPoly;
	}

	/**
	 * @return the melody
	 */
	public float getMelodyProbability() {
		return melodyProbability;
	}

	/**
	 * @param melody the melody to set
	 */
	public void setMelodyProbability(float melody) {
		this.melodyProbability = melody;
	}

	/**
	 * @return the repeatPitchProbability
	 */
	public float getRepeatPitchProbability() {
		return repeatPitchProbability;
	}

	/**
	 * @param repeatPitchProbability the repeatPitchProbability to set
	 */
	public void setRepeatPitchProbability(float repeatPitchProbability) {
		this.repeatPitchProbability = repeatPitchProbability;
	}
}
