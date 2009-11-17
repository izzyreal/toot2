// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.eq;

import uk.org.toot.audio.filter.Filter;

import static uk.org.toot.misc.Localisation.*;

/**
 * A serial cut-only EQ to provide band-limiting effects.
 * Just remember the Low filter is HighPass and the High filter is LowPass.
 */
public class CutEQ extends AbstractSerialEQ
{
    /**
     * Create a CutEQ with default controls.
     */
    public CutEQ() {
        this(new Controls());
    }

    /**
     * Create a CutEQ with the specified controls.
     */
    public CutEQ(Controls controls) {
        super(controls, true);
    }

    /**
     * The controls for a CutEQ.
     */
    public static class Controls extends EQ.Controls
    {
        public Controls() {
            super(EQIds.CUT_EQ_ID, getString("Cut.EQ"));
            float Q = 1.1f;
            float L = 0f;
            ControlColumn g = new ControlColumn();
            g.add(new ClassicFilterControls("High", 4, // !!! !!!
                	Filter.Type.LPF, true,
                    40f, 12000f, 12000f, false,
                    Q, Q, Q, true,
                    L, L, L, true));
            g.add(new ClassicFilterControls("Low", 0, // !!! !!!
                	Filter.Type.HPF, true,
                    20f, 5000f, 20f, false,
                    Q, Q, Q, true,
                    L, L, L, true));
            add(g);
        }
    }
}
