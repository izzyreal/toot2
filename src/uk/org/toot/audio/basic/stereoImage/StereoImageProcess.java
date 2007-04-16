// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.basic.stereoImage;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.SimpleAudioProcess;
import uk.org.toot.audio.core.ChannelFormat;

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
        if ( buffer.getChannelCount() < 2 ) // mono in
            buffer.convertTo(ChannelFormat.STEREO);
        float otherFactor = vars.getWidthFactor();
        boolean swap = vars.isLRSwapped();
        ChannelFormat format = buffer.getChannelFormat();
        // get left/right pairs
        int[] leftChans = format.getLeft();
        int[] rightChans = format.getRight();
        for ( int pair = 0; pair < leftChans.length; pair++ ) {
	   	    float[] left = buffer.getChannel(leftChans[pair]);
    	   	float[] right = buffer.getChannel(rightChans[pair]);
	        // first we process the L/R width
   	    	for ( int i = 0; i < nsamples; i++ ) {
    			left[i] += otherFactor * right[i];
	            right[i] += otherFactor * left[i];
   			}
   			// then we swap if necessary
            if ( swap ) buffer.swap(leftChans[pair], rightChans[pair]);
        }
        return AUDIO_OK;
    }
}
