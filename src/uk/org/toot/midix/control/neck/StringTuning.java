// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

import uk.org.toot.music.tonality.Pitch;

public class StringTuning {
    private int polyphony;
    private int[] pitchClasses;
    private int[] regOffsets;
    private int register;
    private boolean low = false;

    public StringTuning(String tuning, int reg) {
        if (tuning.indexOf("Low") >= 0) {
            reg -= 1; // that pesky B
            low = true;
        }
        String t = tuning.substring(0, tuning.indexOf(" -"));
        String[] p = t.split("\\s");
        polyphony = p.length;
        pitchClasses = new int[polyphony];
        regOffsets = new int[polyphony];
        register = reg;
        for (int i = 0; i < polyphony; i++) {
            pitchClasses[i] = Pitch.classValue(p[i]);
            regOffsets[i] = reg - register;
            //            System.out.print(pitches[i]+", ");
            if (i < polyphony - 1) {
                if (Pitch.classValue(p[i + 1]) < pitchClasses[i])
                    reg += 1;
            }
        }
    }

    /** Return the number of unrelated pitches can can be played concurrently */
    public int getPolyphony() { return polyphony; }

    public int getRegister() { return register; }

    public void setRegister(int reg) { register = reg; }

    //    public int getPitch(int string) { return pitches[string]; }
    public int getPitch(int string) {
        return Pitch.value(pitchClasses[string], register + regOffsets[string]);
    }

    // quick hack for traditional string doubling
    // 4 lowest strings are octave doubled, 2 highest are unison doubled
    public int getDoubleInterval(int string) { return string > 3 ? 0 : 12; }

    public boolean isLow() { return low; }
}
