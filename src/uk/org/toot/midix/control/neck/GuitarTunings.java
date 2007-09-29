// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

import java.util.ArrayList;
import java.util.List;

public class GuitarTunings implements Tunings 
{
    static private ArrayList<String> tunings = new ArrayList<String>();

    public List <String> getTunings() {
        return tunings;
    }

    public StringTuning createTuning(String tuning) {
        return new StringTuning(tuning, 2);
    }

    static {
        tunings.add("E A D G B E - Standard");
        tunings.add("C G C G C E - Open C");
        tunings.add("D A D F# A D - Open D");
        tunings.add("D A D G A D - D modal");
        tunings.add("E B E G# B E - Open E");
        tunings.add("D G D G B D - Open G");
        tunings.add("E A C# E A E - Open A");
        tunings.add("D A D F A D - Crossnote D");
        tunings.add("E B E G B E - Crossnote E");
        tunings.add("D A D G B E - Dropped D");
        tunings.add("D A D G A E - Dropped D #2");
        tunings.add("D G D G C D - Sawmill");
        tunings.add("E A D E E A - Balalaika");
        tunings.add("C F C G C D - Cittern #1");
        tunings.add("C G C G C G - Cittern #2");
        tunings.add("G B D G B D - Dobro");
        tunings.add("C E G A# C D - Overtone");
        tunings.add("A C D E G A - Pentatonic");
        tunings.add("C D# F# A C D# - Minor Third");
        tunings.add("C E G# C E G# - Major Third");
        tunings.add("E A D G C F - All Fourths");
        tunings.add("C F# C F# C F# - Aug Fourths");
        tunings.add("C G D A E B - Mandoguitar");
        tunings.add("C G# E C G# E - Minor Sixth");
        tunings.add("C A F# D# C A - Major Sixth");
        tunings.add("C G D G B C - Admiral");
        tunings.add("C F C G A# F - Buzzard");
        tunings.add("C G D G A D - Face");
        tunings.add("A B E F# A D - Hot Type");
        tunings.add("D A C G C E - Layover");
        tunings.add("C F C G A E - Magic Farmer");
        tunings.add("D A D E A D - Pelican");
        tunings.add("D G D F A A# - Processional");
        tunings.add("D G D F C D - Slow Motion");
        tunings.add("C# A C# G# A E - Spirit");
        tunings.add("C A# C F A# F - Tarboulton");
        tunings.add("E C D F A D - Toulouse");
        tunings.add("D G D F# A B - Triqueen");
    }
}
