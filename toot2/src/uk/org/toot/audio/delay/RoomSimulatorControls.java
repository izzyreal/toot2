// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import java.util.List;
import uk.org.toot.control.*;
import java.awt.Color;

/**
 *
 */
public class RoomSimulatorControls extends AbstractDelayControls
//    implements DelayVariables
{
    private static final ControlLaw LENGTH_LAW = new LinearLaw(2f, 200f, "m");
    private static final ControlLaw RELATIVE_SIZE_LAW = new LinearLaw(0.2f, 5f, "");

    private static final int LENGTH_ID = 1;
    private static final int WIDTH_ID = 2;
    private static final int HEIGHT_ID = 3;

    private List<DelayTap>[] taps;
    private FloatControl lengthControl;
    private FloatControl widthControl;
    private FloatControl heightControl;

    public RoomSimulatorControls() {
        super(DelayIds.ROOM_SIMULATOR, "Room Simulator");
        ControlColumn roomg = new ControlColumn();
        lengthControl = new FloatControl(LENGTH_ID, "Length", LENGTH_LAW, 0.1f, 20f);
        lengthControl.setInsertColor(Color.red.darker());
        roomg.add(lengthControl);
        widthControl = new FloatControl(WIDTH_ID, "Width", RELATIVE_SIZE_LAW, 0.1f, 1f);
        widthControl.setInsertColor(Color.magenta.darker());
        roomg.add(widthControl);
        heightControl = new FloatControl(HEIGHT_ID, "Height", RELATIVE_SIZE_LAW, 0.1f, 1f);
        heightControl.setInsertColor(Color.magenta.darker());
        roomg.add(heightControl);
        add(roomg);
        // invert, feedback
        // invert, mix
        add(createCommonControlColumn(false));
    }

    public float getLength() { return lengthControl.getValue(); }

    public float getWidth() { return getRelativeWidth() * getLength(); }

    public float getHeight() { return getRelativeHeight() * getLength(); }

    public float getRelativeWidth() { return widthControl.getValue(); }

    public float getRelativeHeight() { return heightControl.getValue(); }

    public boolean canBypass() { return true; }
}
