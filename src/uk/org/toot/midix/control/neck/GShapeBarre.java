// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

public class GShapeBarre extends ChordFamily {
    public GShapeBarre() {
        super("G");
        setRootString(0);
        // Major
        create("maj").add(0, 4).add(-1, 3).add(-3, 1).add(-3, 1).add(-3, 1).addMute();
        create("m").add(0, 4).add(-2, 3).add(-3, 1).add(-3, 1).addMute().addMute();
        create("dim").add(0, 4).add(-2, 1).addMute().add(0, 3).add(-1, 2).addMute();
        create("aug").add(0, 4).add(-1, 3).add(-2, 2).add(-2, 1).addMute().addMute();
        //        create("aug").add(0, 3).add(-1, 2).add(-2, 1).addMute().addMute().addMute();
        create("m7-5").add(0, 4).add(-2, 1).addMute().add(0, 3).add(-1, 2).add(-2, 1); // ??? is it really m7-5?
        create("maj9").add(0, 2).add(-1, 1).add(1, 4).add(-1, 1).add(0, 3).addMute();
        create("9").add(0, 2).add(-1, 1).add(0, 3).add(-1, 1).add(0, 4).addMute();
        create("11").add(0, 3).addMute().add(0, 3).add(-1, 2).add(-2, 1).add(-2, 1);
        create("13").add(0, 4).addMute().add(0, 3).add(-1, 2).add(-3, 1).add(-3, 1);
        checkAll();
    }

    public ChordFamily other() { return named("E"); }

    protected void checkShape(String type) {
        ChordShape shape = get(type);
        if (shape.bias() > 0) {
        	System.err.println("G-Shape " + type + " appears to be E-Shape");
        }
        ChordShape.Fretting f = shape.getFretting(getRootString());
        if (f.fret != 0) {
            System.err.println("G-Shape "+type+ ", Bad Fret (require 0): "+f.fret);
        }
    }
}
