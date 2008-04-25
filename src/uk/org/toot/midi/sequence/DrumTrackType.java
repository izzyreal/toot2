/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.midi.sequence;

import uk.org.toot.midi.misc.GM;

public class DrumTrackType implements MidiTrackType
{
    public String noteName(int note) {
        return GM.drumName(note);
    }

    public String programName(int prg) {
        return "Drums"; // !!!
    }
}
