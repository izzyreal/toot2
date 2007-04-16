/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import static uk.org.toot.localisation.Localisation.*;

public class Gate extends DynamicsProcess
{
    private int hold = 0;
    private boolean wasOpen = false;

    public Gate(ProcessVariables vars) {
        super(vars, true); // peak detection
    }

    // gate
    protected float function(float value) {
        if ( value < threshold ) {
            return vars.getDepth(); // gated
        }
        return 1f; // not gated
    }

    // implements hold
    // gate must open before it can close !!!
    // but it can open before it has closed
    protected float dynamics(float target) {
        if ( hold > 0 ) {
            hold -= 1;
            return super.dynamics(1f); // hold envelope
        }
        boolean isOpen = target > 0.9f;
        // on transition to release (close)
        if ( !isOpen && wasOpen ) {
            hold = vars.getHold();
            wasOpen = false;
            return super.dynamics(1f);
        }
		wasOpen = isOpen;
		return super.dynamics(target);
    }

    public static class Controls extends DynamicsControls
    {
        public Controls() {
            super(DynamicsIds.GATE_ID, getString("Gate"));
        }

        protected float getMinimumThreshold() { return -60f; }

	    protected boolean hasHold() { return true; }

	    protected boolean hasDepth() { return true; }
    }
}
