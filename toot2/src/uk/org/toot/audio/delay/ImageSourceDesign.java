// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import java.util.List;

/**
 * Links SimpleEarlyReflectionControls to MultiTapDelayProcess
 */
public class ImageSourceDesign implements MultiTapDelayVariables
{
    /**
     * @link aggregation
     * @supplierCardinality 1 
     */
    private RoomSimulatorControls roomControls;

    public ImageSourceDesign(RoomSimulatorControls controls) {
        roomControls = controls;
    }

    public boolean isBypassed() { return roomControls.isBypassed(); }

    public float getMaxDelayMilliseconds() { return 200; }

    public float getFeedback() { return roomControls.getFeedback(); }

    public float getMix() { return roomControls.getMix(); }

    public List<DelayTap> getTaps(int chan) {
        return null; // !!! !!! TODO
    }

    public int getChannelCount() { return 2; }

    public float getDelayFactor() { return 1f; }
}
