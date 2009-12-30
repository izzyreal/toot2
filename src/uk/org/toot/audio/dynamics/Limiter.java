/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import static uk.org.toot.misc.Localisation.*;

public class Limiter extends DynamicsProcess
{
    public Limiter(ProcessVariables vars) {
        super(vars, true); // peak detection
    }

    // limit
    protected float function(float value) {
        if ( value > threshold ) {
            return threshold / value; // infinity ratio limit
        }
        return 1f; // not limited
    }

    public static class Controls extends DynamicsControls
    {
        public Controls() {
            super(DynamicsIds.LIMITER_ID, getString("Limiter"));
        }
        
		protected boolean hasGainReductionIndicator() { return true; }
    }
}


