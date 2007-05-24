// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.ChannelFormat;

public class StereoModulatedDelayProcess extends ModulatedDelayProcess
{
    /**
     * @supplierCardinality 1
     * @link aggregation 
     */
    private StereoModulatedDelayVariables vars;

    private float modulatorPhaseRight;

    public StereoModulatedDelayProcess(StereoModulatedDelayVariables vars) {
        super(vars);
        this.vars = vars;
    }

    public int processAudio(AudioBuffer buffer) {
        if ( buffer.getChannelCount() == 1 ) {
            buffer.convertTo(ChannelFormat.STEREO);
        }
        return super.processAudio(buffer);
    }

    protected void buildModulatorMap(AudioBuffer buffer) {
        // don't modulate center, it will spil L/R phase quadrature imaging
        // don't modulate LFE, constructive interference could blow woofers
       	int nc = buffer.getChannelCount();
        for ( int ch = 0; ch < nc; ch++ ) {
   	        if ( format.isLeft(ch) ) modulatorMap[ch] = 0;
       	    else if ( format.isRight(ch) ) modulatorMap[ch] = 1;
           	else modulatorMap[ch] = -1; // center, LFE, no modulation
        }
    }

    protected void incrementModulators(float timeDelta) {
        super.incrementModulators(timeDelta);
       	modulatorPhaseRight = modulatorPhase + vars.getPhaseRadians();
        if ( modulatorPhaseRight > Math.PI ) {
   	        modulatorPhaseRight -= 2 * Math.PI;
       	}
    }

    protected float modulation(int chan) {
        float phase;
        if ( modulatorMap[chan] == 0 ) phase = modulatorPhase;
        else if ( modulatorMap[chan] == 1 ) phase = modulatorPhaseRight;
        else return 0f; // !!! !!! center, LFE no modulation, static delay
        int shape = vars.getLFOShape();
        float mod = (shape == 0) ? sine(phase) : triangle(phase);
        // clamp the cheapo algorithm which goes outside range a little
        if ( mod < -1f ) mod = -1f;
        else if ( mod > 1f ) mod = 1f;
        return mod;
    }
}
