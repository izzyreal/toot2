// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.tool;

import uk.org.toot.audio.core.*;
import static uk.org.toot.audio.core.FloatDenormals.*;

/**
 * This class
 */
public class DenormalProcess extends SimpleAudioProcess
{
    public final static int COUNT = 0;
    public final static int DAZ = 1; // Denormal As Zero

    /**
     * @link aggregation
     * @supplierCardinality 1 
     */
    private DenormalControls controls;

    private int mode = COUNT;

    public DenormalProcess(DenormalControls c) {
        controls = c;
    }

    public int processAudio(AudioBuffer buffer) {
        if ( controls.isBypassed() ) return AUDIO_OK;
        switch ( mode ) {
        case COUNT: return countDenormals(buffer);
        case DAZ: return zeroDenormals(buffer);
        }
        return AUDIO_OK;
    }

    protected int countDenormals(AudioBuffer buffer) {
        int ns = buffer.getSampleCount();
        int nc = buffer.getChannelCount();
        int count = 0;
        for ( int c = 0; c < nc; c++ ) {
            count += countDenorms(buffer.getChannel(c), ns);
        }
        // !!! need to do something with count !!! !!!
        return AUDIO_OK;
    }

    protected int zeroDenormals(AudioBuffer buffer) {
        int ns = buffer.getSampleCount();
        int nc = buffer.getChannelCount();
        for ( int c = 0; c < nc; c++ ) {
            zeroDenorms(buffer.getChannel(c), ns);
        }
        return AUDIO_OK;
    }
}
