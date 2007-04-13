/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.midi.message;

import javax.sound.midi.MidiMessage;

/**
 * The class for creating accessing and mutating 1, 2 and 3 bytes MidiMessages
 * representing System Common messages without knowledge of the implementation
 * class. 
 */
public class CommonMsg extends ShortMsg
{
    static public boolean isCommon(MidiMessage msg) {
        return isCommon(getStatus(msg));
    }

    static public boolean isCommon(int status) {
        // note 0xF7 is EOX so we reject it
        return isShort(status) && status >= 0xF1 && status < 0xF7;
    }

    static public boolean isMTCQuarterFrame(MidiMessage msg) {
        int status = getStatus(msg);
        return isMTCQuarterFrame(status);
    }

    static public boolean isMTCQuarterFrame(int status) {
        return status == MTC_QUARTER_FRAME;
    }

    static public MidiMessage createMTCQuarterFrame(int part, int value) {
        return null; // !!!
    }

    static public int getMTCQuarterFrame(MidiMessage msg) {
        return 0; // !!! return what? need a tuple? !!!
    }

    /**
     * Return the 14 bit Song Position Pointer.
     * This is the number of beats since the start of the sequence.
     */
    static public int getSongPositionPointer(MidiMessage msg) {
        if ( getStatus(msg) == SONG_POSITION_POINTER ) {
            return getData1and2(msg);
        }
        return -1;
    }

    // System common messages

    /**
     * Status byte for MIDI Time Code Quarter Frame message (0xF1, or 241).
     */
    public static final int MTC_QUARTER_FRAME			= 0xF1; // 241

    /**
     * Status byte for Song Position Pointer message (0xF2, or 242).
     */
    public static final int SONG_POSITION_POINTER		= 0xF2;	// 242

    /**
     * Status byte for MIDI Song Select message (0xF3, or 243).
     */
    public static final int SONG_SELECT					= 0xF3; // 243

    /**
     * Status byte for Tune Request message (0xF6, or 246).
     */
    public static final int TUNE_REQUEST				= 0xF6; // 246
}
