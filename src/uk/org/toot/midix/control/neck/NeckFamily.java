// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

import java.util.List;
import java.util.ArrayList;
import java.util.Observable;
import uk.org.toot.midi.misc.GM;

public class NeckFamily extends Observable {
    private int register;
    private int GMfamily;
    private Tunings tunings;
    private static ArrayList < NeckFamily > families = new ArrayList < NeckFamily > ();

    static {
        families.add(new GuitarFamily());
        families.add(new BassFamily());
        //        families.add(new SoloStringsFamily());
    }

    protected NeckFamily(int reg, int family, Tunings t) {
        register = reg;
        GMfamily = family;
        tunings = t;
    }

    public static NeckFamily named(String name) {
        for (NeckFamily f : families) {
            if (name.equals(f.getName())) return f;
        }
        return families.get(0); // Guitars are better than nothing ;)
    }

    public static List < NeckFamily > families() { return families; }

    public int getRegister() { return register; }

    public int getGMFamily() { return GMfamily; }

    public String getName() { return GM.melodicFamilyName(GMfamily); }

    public Tunings getTunings() { return tunings; }
}
