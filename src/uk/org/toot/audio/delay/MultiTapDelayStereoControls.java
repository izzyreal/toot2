// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import java.awt.Color;
import java.util.List;
import uk.org.toot.control.*;

import static uk.org.toot.localisation.Localisation.*;

/**
 * Provides tap list per channel.
 * Implemented to experiment with per channel control handling.
 */
public class MultiTapDelayStereoControls extends AbstractDelayControls
    implements MultiTapDelayVariables
{
    private List<MultiTapDelayControls> perChannelControls;
    private float msMax;
    private static ControlLaw delayFactorLaw = new LogLaw(0.2f, 5f, "");
    private FloatControl delayFactorControl;

    public MultiTapDelayStereoControls() {
        this(3, 2000f); // 3 taps, 2 seconds max delay
    }

	public MultiTapDelayStereoControls(int ntaps, float ms) {
        super(DelayIds.MULTI_TAP_DELAY_ID, getString("Stereo.Multi.Tap.Delay"));
        msMax = ms;
        perChannelControls = new java.util.ArrayList<MultiTapDelayControls>();
        for ( int a = 0; a < 2 ; a++ ) {
            String name = (a == 0) ? getString("Left") : getString("Right"); // !!! !!!
            MultiTapDelayControls c = new MultiTapDelayControls(a*16, ntaps, msMax, name);
            add(c);
            perChannelControls.add(c);
        }
        delayFactorControl = new FloatControl(DELAY_FACTOR_ID, getString("Delay"), delayFactorLaw, 0.01f, 1f);
        delayFactorControl.setInsertColor(Color.RED.darker());
        add(delayFactorControl);
        // feedback
        // mix
        add(createCommonControlColumn(false)); // no inverts
    }

    public float getMaxDelayMilliseconds() { return msMax; }

    public boolean canBypass() { return true; }

    public List<DelayTap> getTaps(int chan) {
        // no validation, called on server thread, time critical
        return perChannelControls.get(chan).getTaps();
    }

    public int getChannelCount() { return 2; } // !!! !!!

    public float getDelayFactor() {
		return delayFactorControl.getValue();
    }
}
