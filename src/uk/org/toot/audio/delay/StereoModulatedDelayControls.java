// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import java.awt.Color;

import static uk.org.toot.misc.Localisation.*;

public class StereoModulatedDelayControls extends ModulatedDelayControls implements StereoModulatedDelayVariables
{
    private static final ControlLaw phaseLaw = new LinearLaw(0f, 180f, "degrees");
    private FloatControl phaseControl;

    public StereoModulatedDelayControls() {
        super(DelayIds.STEREO_MODULATED_DELAY_ID, getString("Stereo.Modulated.Delay"));
    }

    public float getPhaseRadians() {
        return (float)(phaseControl.getValue() * Math.PI / 90);
    }

    protected ControlColumn createControlColumn1() {
        phaseControl = new FloatControl(PHASE_ID, getString("Phase"), phaseLaw, 1f, 0f);
        phaseControl.setInsertColor(Color.BLUE.darker());
        ControlColumn cc = super.createControlColumn1();
		cc.add(phaseControl);
        return cc;
    }
}
