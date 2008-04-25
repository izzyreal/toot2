/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.midi.sequence;

public interface MidiTrackType
{
    String noteName(int note);

    String programName(int prg);
}
