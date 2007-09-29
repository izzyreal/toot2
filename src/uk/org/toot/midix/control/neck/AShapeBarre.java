// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

public class AShapeBarre extends ChordFamily {
    public AShapeBarre() {
        super("A");
        setRootString(1); // 'A' in standard tuning
        create("maj").add(0, 1).add(0, 1).add(2, 3).add(2, 3).add(2, 3).add(0, 1);
        create("m").add(0, 1).add(0, 1).add(2, 3).add(2, 4).add(1, 2).add(0, 1);
        create("dim").addMute().add(0, 1).add(1, 2).add(2, 3).add(1, 2).addMute();
        //        create("-5").addMute().add(0, 1).add(1, 2).add(2, 3).add(2, 4).addMute();
        create("dim7").addMute().add(0, 2).add(1, 3).add(-1, 1).add(1, 4).addMute();
        create("m7").add(0, 1).add(0, 1).add(2, 3).add(0, 1).add(1, 2).add(0, 1);
        create("maj7").add(0, 1).add(0, 1).add(2, 3).add(1, 2).add(2, 4).add(0, 1);
        create("7").add(0, 1).add(0, 1).add(2, 3).add(0, 1).add(2, 4).add(0, 1);
        create("7+5").addMute().add(0, 1).add(3, 4).add(0, 1).add(2, 3).add(1, 2);
        create("7-5").addMute().add(0, 1).add(1, 2).add(0, 1).add(2, 3).addMute();
        create("sus4").add(0, 1).add(0, 1).add(2, 2).add(2, 3).add(3, 4).add(0, 1);
        create("7sus4").add(0, 1).add(0, 1).add(2, 3).add(0, 1).add(3, 4).add(0, 1);
        create("6").add(0, 1).add(0, 1).add(2, 3).add(2, 3).add(2, 3).add(2, 3);
        create("m6").add(0, 1).add(0, 1).add(2, 3).add(2, 3).add(1, 2).add(2, 4);
        create("maj9").addMute().add(0, 2).add(-1, 1).add(1, 4).add(0, 3).addMute();
        create("7+9").addMute().add(0, 2).add(-1, 1).add(0, 3).add(1, 4).addMute();
        create("11").add(0, 1).add(0, 1).add(0, 1).add(0, 1).add(0, 1).add(0, 1);
        create("13").add(0, 2).add(0, 2).add(-1, 1).add(0, 3).add(0, 3).add(2, 4);
        checkAll();
    }

    public ChordFamily other() { return named("C"); }

    protected void checkShape(String type) {
        ChordShape shape = get(type);
        if (shape.bias() < 0) System.err.println("A-Shape " + type + " appears to be C-Shape");
        ChordShape.Fretting f = shape.getFretting(getRootString());
        if (f.fret != 0) {
            System.err.println("A-Shape " + type + ", Bad Fret (require 0): " + f.fret);
        }
    }
}
