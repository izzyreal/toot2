/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.midi.sequence;

import uk.org.toot.music.tonality.Pitch;
import uk.org.toot.midi.misc.GM;

public class PitchTrackType implements MidiTrackType
{
    public String noteName(int note) {
        return Pitch.name(note);
    }

    public String programName(int prg) {
        return GM.melodicProgramName(prg);
    }
}
