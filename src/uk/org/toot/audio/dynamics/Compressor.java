/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import uk.org.toot.audio.core.KVolumeUtils;
import org.tritonus.share.sampled.TVolumeUtils;

import static uk.org.toot.localisation.Localisation.*;

public class Compressor extends DynamicsProcess
{
    public Compressor(ProcessVariables vars) {
		super(vars, false); // RMS, not peak
    }

    protected float function(float value) {
        if ( value > threshold ) { // -knee/2 etc. interpolate around knee !!!
        	float valdB = (float)KVolumeUtils.lin2log(value);
            float overdB = valdB - thresholddB;
            float gainReductiondB = -overdB * (ratio - 1f) / ratio;
            float gain = (float)TVolumeUtils.log2lin(gainReductiondB);
            return gain;
        }
        return 1f;
    }

    public static class Controls extends DynamicsControls
    {
        public Controls() {
            super(DynamicsIds.COMPRESSOR_ID, getString("Compressor"));
        }

        public Controls(String name, int idOffset) {
            super(DynamicsIds.COMPRESSOR_ID, name, idOffset);
        }

		protected boolean hasGainReductionIndicator() { return true; }

	    protected boolean hasRatio() { return true; }

	    protected boolean hasGain() { return true; }
    }
}
