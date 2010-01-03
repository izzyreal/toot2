/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import static uk.org.toot.misc.Localisation.*;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.LogLaw;

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
        private final static ControlLaw ATTACK_LAW = new LogLaw(1f, 100f, "ms");
        private final static ControlLaw RELEASE_LAW = new LogLaw(20f, 2000f, "ms");

        public Controls() {
            super(DynamicsIds.LIMITER_ID, getString("Limiter"));
        }
        
        protected ControlLaw getAttackLaw() { return ATTACK_LAW; }
        
        protected ControlLaw getRelaseLaw() { return RELEASE_LAW; }
        
		protected boolean hasGainReductionIndicator() { return true; }
    }
}


