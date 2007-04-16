/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import static uk.org.toot.localisation.Localisation.*;

public class Expander extends DynamicsProcess
{
    public Expander(ProcessVariables vars) {
        super(vars, false); // RMS, not peak
    }

    // expand
    protected float function(float value) {
/*        if ( value < threshold ) {
        	float valdB = (float)KVolumeUtils.lin2log(value);
            float underdB = thresholddB - valdB;
            copied from Compressor !!! needs changing
            float gainReductiondB = -overdB * (ratio - 1f) / ratio;
            float gain = (float)TVolumeUtils.log2lin(gainReductiondB);
            return gain;
        } */
        return 1f;
    }

    public static class Controls extends DynamicsControls
    {
        public Controls() {
            super(DynamicsIds.EXPANDER_ID, getString("Expander"));
        }

        protected float getMinimumThreshold() { return -60f; }

	    protected boolean hasRatio() { return true; }
    }
}
