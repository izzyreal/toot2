// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

public class EShapeBarre extends ChordFamily {
    public EShapeBarre() {
        super("E");
        setRootString(0); // 'E' in standrad tuning
        // add takes fret and finger, addMute for a mute
        // major
        create("maj").add(0, 1).add(2, 3).add(2, 4).add(1, 2).add(0, 1).add(0, 1);
        // minor                                  _________
        create("m").add(0, 1).add(2, 3).add(2, 4).add(0, 1).add(0, 1).add(0, 1);
        // diminished
        create("dim").add(0, 1).add(1, 2).add(2, 3).add(0, 1).addMute().addMute();
        // augmented
        // seventh                      _________           _________
        create("7").add(0, 1).add(2, 3).add(0, 1).add(1, 2).add(3, 4).add(0, 1);
        // minor seventh
        create("m7").add(0, 1).add(2, 3).add(0, 1).add(0, 1).add(3, 4).add(0, 1);
        create("m7-5").add(0, 1).add(1, 2).add(0, 1).add(0, 1).addMute().addMute();
        create("m7-9").add(0, 1).addMute().add(0, 1).add(0, 1).add(0, 1).add(1, 2);
        // major seventh   muted       muted
        create("maj7").add(0, 1).add(2, 4).add(1, 2).add(1, 3).addMute().addMute();
        create("maj9").add(0, 1).addMute().add(1, 2).add(1, 3).add(0, 1).add(2, 4);
        // suspended fourth
        create("sus4").add(0, 1).add(0, 1).add(2, 3).add(2, 4).add(0, 1).add(0, 1);
        // seventh suspended fourth
        create("7sus4").add(0, 1).add(2, 3).add(0, 1).add(2, 4).add(0, 1).add(0, 1);
        // sixth
        create("6").add(0, 1).addMute().add(2, 3).add(1, 2).add(2, 4).add(0, 1);
        // minor sixth
        create("m6").add(0, 1).addMute().add(2, 3).add(0, 1).add(2, 4).add(0, 1);
        // minor ninth
        create("m9").add(0, 1).add(2, 3).add(0, 1).add(0, 1).add(0, 1).add(2, 4);
        checkAll();
    }

    protected void checkShape(String type) {
        ChordShape shape = get(type);
        if (shape.bias() < 0) System.err.println("E-Shape " + type + " appears to be G-Shape");
        ChordShape.Fretting f = shape.getFretting(getRootString());
        if (f.fret != 0 || f.finger != 1) {
            System.err.println("E-Shape " + type + ", " + ((f.fret != 0) ? ("Bad Fret (require 0): " + f.fret) : "OK Fret") + ", " +
                ((f.finger != 1) ? ("Bad Finger (require 1): " + f.finger) : "OK Finger"));
        }
    }

    public ChordFamily other() { return named("G"); }
}
