// Copyright (C) 2006, 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.dynamics;

import static uk.org.toot.misc.Localisation.*;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.LinearLaw;

public class Gate extends DynamicsProcess
{
    private int holdCount = 0;
    private int hold;
    private boolean wasOpen = false;
    private float depth;

    public Gate(Variables vars) {
        super(vars, false); // rms detection
    }

    @Override
    protected void cacheProcessVariables() {
        super.cacheProcessVariables();
        depth = vars.getDepth();
        hold = vars.getHold();
    }
    
    // gate
    protected float function(float value) {
        return value < threshold ? depth : 1f;
    }

    // implements hold
    // gate must open before it can close !!!
    // but it can open before it has closed
    protected float dynamics(float target) {
        if ( holdCount > 0 ) {
            holdCount -= 1;
            return super.dynamics(1f); // hold envelope
        }
        boolean isOpen = target > 0.9f;
        // on transition to release (close)
        if ( !isOpen && wasOpen ) {
            holdCount = hold;
            wasOpen = false;
            return super.dynamics(1f);
        }
		wasOpen = isOpen;
		return super.dynamics(target);
    }

    public static class Controls extends DynamicsControls
    {
    	private final static ControlLaw THRESH_LAW = new LinearLaw(-80f, 20f, "dB");

        public Controls() {
            super(DynamicsIds.GATE_ID, getString("Gate"));
        }

        protected ControlLaw getThresholdLaw() { return THRESH_LAW; }

	    protected boolean hasHold() { return true; }

	    protected boolean hasDepth() { return true; }
    }
}
