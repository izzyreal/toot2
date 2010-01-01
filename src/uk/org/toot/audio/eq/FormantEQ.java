// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.eq;

import uk.org.toot.audio.filter.Filter;

import static uk.org.toot.misc.Localisation.*;

/**
 * A formant filter.
 */
public class FormantEQ extends AbstractParallelEQ
{
    /**
     * Creates a default FormantFilter object.
     */
    public FormantEQ() {
        this(new Controls());
    }

    /**
     * Creates a FormantFilter object with the specified controls.
     */
    public FormantEQ(Controls spec) {
        super(spec, false); // false means levels are absolute
    }

    /**
     * The Controls for a 4 band formant filter.
     */
    static public class Controls extends EQ.Controls
    {
        protected float R = 15f;	// dB range, +/-

        public Controls() {
            super(EQIds.FORMANT_EQ_ID, getString("Formant.EQ"));
            add(new ClassicFilterControls("1", 0,
                	Filter.Type.BPF, true,
                    125f, 500f, 250, false,
                    0.5f, 10f, 2f, false,
                    -R, R, 0f, false));
            add(new ClassicFilterControls("2", 4,
                	Filter.Type.BPF, true,
                    250f, 1000f, 500, false,
                    0.5f, 10f, 2f, false,
                    -R, R, 0f, false));
            add(new ClassicFilterControls("3", 8,
                	Filter.Type.BPF, true,
                    500f, 2000f, 1000, false,
                    0.5f, 10f, 2f, false,
                    -R, R, 0f, false));
            add(new ClassicFilterControls("4", 16,
                	Filter.Type.BPF, true,
                    1000f, 4000f, 2000, false,
                    0.5f, 10f, 2f, false,
                    -R, R, 0f, false));
        }
    }
}
