/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import uk.org.toot.control.*;

public class CrossoverControl extends FloatControl
{
    private final static ControlLaw XO_LAW = new LogLaw(100f, 10000f, "Hz");

    public CrossoverControl(String name, float freq) {
        // this will need an idOffset for > dual band controls
        super(DynamicsControlIds.CROSSOVER_FREQUENCY, name, XO_LAW, 1.0f, freq);
    }

    public int getFrequency() {
        return (int)getValue();
    }
}


