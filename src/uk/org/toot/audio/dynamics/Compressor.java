// Copyright (C) 2006, 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.dynamics;

import org.tritonus.share.sampled.TVolumeUtils;

import static uk.org.toot.misc.Localisation.*;

public class Compressor extends DynamicsProcess
{
    public Compressor(Variables vars) {
		super(vars, false); // RMS, not peak
    }

    protected float function(float value) {
        if ( value > threshold ) { // -knee/2 etc. interpolate around knee !!!
        	float overdB = (float)TVolumeUtils.lin2log(value/threshold);
            return (float)TVolumeUtils.log2lin(overdB * ratio2);
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
	    
	    protected boolean hasKey() { return true; }
    }
}
