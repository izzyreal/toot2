// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.basic.stereoImage;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.SimpleAudioProcess;

public class StereoImageProcess extends SimpleAudioProcess
{
    /** @link aggregation
     * @supplierCardinality 1 */
    private StereoImageProcessVariables vars;

    public StereoImageProcess(StereoImageProcessVariables variables) {
        vars = variables;
    }

    public int processAudio(AudioBuffer buffer) {
        int nsamples = buffer.getSampleCount();
   	    float[] left = buffer.getChannel(0);
       	float[] right = buffer.getChannel(1);
        // first we process the L/R width
        float otherFactor = vars.getWidthFactor();
   	    for ( int i = 0; i < nsamples; i++ ) {
    		left[i] += otherFactor * right[i];
            right[i] += otherFactor * left[i];
   		}
   		// then we swap channels if necessary
        if ( vars.isLRSwapped() )  {
	        float tmp;
    	    for ( int i = 0; i < nsamples; i++ ) {
        	    tmp = left[i];
            	left[i] = right[i];
	            right[i] = tmp;
    	    }
        }
        return AUDIO_OK;
    }
}
