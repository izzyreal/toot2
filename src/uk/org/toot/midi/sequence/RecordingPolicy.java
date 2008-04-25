/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.midi.sequence;

import uk.org.toot.midi.core.MidiConnection;

/**
 * A RecordingPolicy determines what MIDI signals will be recorded.
 * It is passed each signal Connection and will typically use information
 * from this Connection to select which Connections are to be recorded. Choices for recording might be Live input (controller)
 * Live output (instrument) Playback output (master?)
 */
public abstract class RecordingPolicy 
{
    static private RecordingPolicy everything = null;

    abstract public boolean select(MidiConnection connection);

    static public RecordingPolicy everything() {
        if (everything == null) {
            everything = new Everything();
        }
        return everything;
    }

    static private class Everything extends RecordingPolicy {
        public boolean select(MidiConnection c) { return true; }
    }

}
