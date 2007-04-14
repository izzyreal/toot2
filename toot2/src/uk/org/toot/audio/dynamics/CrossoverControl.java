/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import java.awt.Color;
import uk.org.toot.control.*;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.filter.*;

public class CrossoverControl extends FloatControl
{
    private static ControlLaw xoLaw = new LogLaw(100f, 10000f, "Hz");

    public CrossoverControl(String name, float freq) {
        // this will need an idOffset for > dual band controls
        super(DynamicsControlIds.CROSSOVER_FREQUENCY, name, xoLaw, 1.0f, freq);
        setInsertColor(Color.yellow);
    }

    public int getFrequency() {
        return (int)getValue();
    }
}


