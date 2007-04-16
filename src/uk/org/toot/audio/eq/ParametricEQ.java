// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.eq;

import uk.org.toot.audio.filter.Filter;

import static uk.org.toot.localisation.Localisation.*;

/**
 * A parametric EQ.
 */
public class ParametricEQ extends AbstractParallelEQ
{
    /**
     * Creates a default ParametricEQ object.
     */
    public ParametricEQ() {
        super(new Controls());
    }

    /**
     * Creates a ParemetricEQ object with the specified controls.
     */
    public ParametricEQ(Controls spec) {
        super(spec);
    }

    /**
     * The Controls for a 4 band ParametricEQ.
     */
    static public class Controls extends EQ.Controls
    {
        protected float R = 15f;	// dB range, +/-

        public Controls() {
            super(EQIds.PARAMETRIC_EQ_ID, getString("Parametric.EQ"));
            add(new ClassicFilterControls(getString("Low"), 0,
                	Filter.Type.LPF, true,
                    40f, 3000f, 80, false,
                    1f, 1f, 1f, true,
                    -R, R, 0f, false));
            add(new ClassicFilterControls(getString("Lo.Mid"), 4,
                	Filter.Type.BPF, true,
                    40f, 3000f, 600, false,
                    0.5f, 10f, 1f, false,
                    -R, R, 0f, false));
            add(new ClassicFilterControls(getString("Hi.Mid"), 8,
                	Filter.Type.BPF, true,
                    3000f, 20000f, 4000, false,
                    0.5f, 10f, 1f, false,
                    -R, R, 0f, false));
            add(new ClassicFilterControls(getString("High"), 16,
                	Filter.Type.HPF, true,
                    3000f, 20000f, 12000, false,
                    1f, 1f, 1f, true,
                    -R, R, 0f, false));
        }
    }
}
