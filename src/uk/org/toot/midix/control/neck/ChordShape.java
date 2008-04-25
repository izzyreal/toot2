// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

import java.util.ArrayList;

public class ChordShape 
{
    private ArrayList<Fretting> fretting = new ArrayList<Fretting>(); // !!

    public Fretting getFretting(int string) {
        return fretting.get(string);
    }

    /**
     * The bias of a ChordShape is defined as the sum of the fret offsets
     * from the root note for every string. Technically the root string shouldn't
     * be included but as it's offset to itself is always zero it's easier to leave it.
     */
    public int bias() {
        int bias = 0;
        for (Fretting f : fretting) {
            if (f.isMuted() || f.finger < 0) continue; // no bias if open!
            bias += f.fret;
        }
        return bias;
    }

    /**
     * A fretting specification for a TunedString 
     * If finger < 0 fret position is relative to the nut, fret is normally 0
     * to represent an open string, but may be (possibly unplayably) elsewhere.
     * Because fret position is absolute, negative values are invalid as fret positions and so are encoded as follows:
     * -1, -1 is muted If finger >= 0 fret position is relative some other position, usually
     * the offset of the ChordShape which owns this Fretting. Negative values are valid relative fret positions.
     */
    public class Fretting {
        public int fret = 0;
        public int finger = 0;

        public Fretting(int aFret, int aFinger) {
            fret = aFret;
            finger = aFinger;
        }

        public boolean isMuted() { return (fret == -1 && finger == -1); }
    }


    public ChordShape add(int fret, int finger) {
        fretting.add(new Fretting(fret, finger));
        return this;
    }

    public ChordShape addMute() {
        fretting.add(new Fretting(-1, -1));
        return this;
    }

    public boolean isValidAt(int fret) {
        for (Fretting f : fretting) {
            if (f.isMuted()) continue;
            if (f.finger >= 0 && fret + f.fret < 0) return false; // past nut!!
        }
        return true;
    }
}
