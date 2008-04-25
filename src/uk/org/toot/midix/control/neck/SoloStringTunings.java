// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

import java.util.List;

public class SoloStringTunings implements Tunings {
    static private List<String> tunings = new java.util.ArrayList<String>();

    public List<String> getTunings() {
        return tunings;
    }

    public StringTuning createTuning(String tuning) {
        return new StringTuning(tuning, 1);
    }

    static {
        tunings.add("G D A E - Violin");
        tunings.add("C G D A - Cello/Viola");
        tunings.add("E A D G - Contrabass");
        tunings.add("E A D G B E - (standard)");
    }
}
