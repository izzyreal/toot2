// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.basic.stereoImage;

import java.awt.Color;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.control.BooleanControl;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.LinearLaw;

import static uk.org.toot.localisation.Localisation.*;

/**
 * Implements stereo image controls and obeys the process variables contract
 * @author st
 *
 */
public class StereoImageControls extends AudioControls
    implements StereoImageProcessVariables
{
    public static final int STEREO_IMAGE = 0x30;
    public static final int LR_SWAP = 1;
    public static final int LR_WIDTH = 2;

    private BooleanControl swap;
    private FloatControl width;

    private final static ControlLaw WIDTH_LAW = new LinearLaw(0f, 2f, "");

    public StereoImageControls() {
        super(STEREO_IMAGE, getString("Stereo"));
        width = new FloatControl(LR_WIDTH, getString("Width"), WIDTH_LAW, 0.01f, 1f);
        width.setInsertColor(Color.orange);
        add(width);
        swap = new BooleanControl(LR_SWAP, getString("Swap"), false); // initially not swapped
        swap.setStateColor(true, Color.red);
        add(swap);
    }

    public float getWidthFactor() {
        return -(width.getValue()-1);
    }

    public boolean isLRSwapped() {
        return swap.getValue();
    }

    public boolean isAlwaysVertical() { return true; }
}
