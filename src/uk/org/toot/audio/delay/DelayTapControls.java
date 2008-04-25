// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import uk.org.toot.control.*;
import uk.org.toot.audio.core.AudioControls;
import java.awt.Color;

import static uk.org.toot.localisation.Localisation.*;

public class DelayTapControls extends AudioControls
    implements DelayTap
{
    private static final ControlLaw levelLaw = new LinearLaw(0f, 1f, ""); // !!! should be log but from zero!
    private FloatControl delayControl;
    private FloatControl levelControl;

    // because we're used more than once
    // our user has to tell us our id
    // which should be incremented by 2 for each one of us
    public DelayTapControls(int id, float msMax) {
        super(id, ""); // ??? ??? id ??? and used below
        // add delay control (ms)
        ControlLaw delayLaw = new LinearLaw(0.1f, msMax, "ms"); // !!! pass from superclass
        delayControl = new FloatControl(id, getString("Delay"), delayLaw, 0.1f, msMax/4); // !!! initial value
        delayControl.setInsertColor(Color.red.darker());
        add(delayControl);
        // add feedback control
        levelControl = new FloatControl(id+1, getString("Level"), levelLaw, 0.01f, 0f);
        levelControl.setInsertColor(Color.black);
        add(levelControl);
    }

    public boolean isAlwaysVertical() { return true; }

    public float getDelayMilliseconds() {
        return delayControl.getValue();
    }

    public float getLevel() {
        return levelControl.getValue();
    }
}
