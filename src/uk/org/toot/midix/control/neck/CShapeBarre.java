// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

public class CShapeBarre extends ChordFamily {
    public CShapeBarre() {
        super("C");
        setRootString(1); // A string played by 4th finger
        // Major						  3
        create("maj").addMute().add(0, 4).add(-1, 3).add(-3, 1).add(-2, 2).add(-3, 1);
        // Minor                          b3
        create("m").addMute().add(0, 4).add(-2, 2).add(-3, 1).add(-2, 3).addMute();
        // Augmented
        create("aug").addMute().add(0, 3).add(-1, 2).add(-2, 1).add(-2, 1).addMute();
        // Diminished
        create("dim").addMute().add(0, 4).add(-2, 2).add(-4, 1).add(-2, 3).add(-4, 1);
        // sevenths
        create("maj7").addMute().add(0, 4).add(-1, 3).add(-3, 1).add(-3, 1).add(-3, 1);
        create("m7").addMute().add(0, 4).add(-2, 1).add(0, 3).add(-2, 1).add(0, 2);
        // ninths
        create("6/9").add(0, 2).add(0, 2).add(-1, 1).add(-1, 1).add(0, 3).add(0, 4);
        create("7-9").addMute().add(0, 2).add(-1, 1).add(0, 3).add(-1, 1).addMute();
        create("9-5").add(-1, 1).add(0, 2).add(-1, 1).add(0, 3).add(0, 4).add(-1, 1);
        create("9").add(0, 2).add(0, 2).add(-1, 1).add(0, 3).add(0, 4).add(0, 4);
        create("m9").add(0, 2).add(0, 2).add(-2, 1).add(0, 3).add(0, 4).add(0, 4);
        checkAll();
    }

    public ChordFamily other() { return named("A"); }

    protected void checkShape(String type) {
        ChordShape shape = get(type);
        if (shape.bias() > 0) { 
        	System.err.println("C-Shape "+type+" appears to be A-Shape");
        }
        ChordShape.Fretting f = shape.getFretting(getRootString());
        if (f.fret != 0) {
            System.err.println("C-Shape "+type+", Bad Fret (require 0): "+f.fret);
        }
    }
}
