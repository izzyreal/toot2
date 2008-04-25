// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.eq;

import uk.org.toot.audio.filter.Filter;

import static uk.org.toot.localisation.Localisation.*;

/**
 * An octave graphic EQ.
 */
public class GraphicEQ extends AbstractParallelEQ {

    public GraphicEQ() {
        this(new Controls());
    }

    /**
     * Creates a new GraphicEQ object.
     */
    public GraphicEQ(int flow) {
        this(new Controls(flow));
    }

    public GraphicEQ(Controls c) {
        super(c);
    }

    /**
     * The controls for a GraphicEQ.
     */
    static public class Controls extends EQ.Controls
    {
        private static final float Q = 1.4f;

        /**
         * Create default controls with ISO standard frequencies.
         */
        public Controls() {
            this(50);
        }

        public Controls(int flow) {
            super(EQIds.GRAPHIC_EQ_ID, getString("Graphic.EQ"));
            int fc = flow;
            int id = 1;
            while ( fc < 20001 ) {
                add(new ClassicFilterControls(String.valueOf(fc), id,
                    	Filter.Type.BPF, true,
                        fc, fc, fc, true,
                        Q, Q, Q, true,
                        -12f, 12f, 0f, false));
                fc += fc;
                id += 4; // !!! !!! 4 FFS!
            }
        }
    }
}
