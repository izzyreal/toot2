// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import uk.org.toot.control.*;
import uk.org.toot.audio.core.*;
import uk.org.toot.audio.meter.MeterControls;

/**
 * MultiTrackControls is an experimental CompoundControl that add controls
 * for stereo metering for each track of a multi-track player/recorder.
 */
public class MultiTrackControls extends CompoundControl
{
    private int trackCount;

    public MultiTrackControls(int nTracks) {
        super(2, "MultiTrack"); // !!! !!! MIXER is 1 !!! !!! !!!
        trackCount = nTracks;
        for ( int i = 0; i < trackCount; i++ ) {
//            add(new MeterControls(2, String.valueOf(i+1)));
			createTrackControls(2, i); // !!! !!!
        }
    }

    public AudioControlsChain createTrackControls(int id, int index) {
        String name = String.valueOf(index+1);
        AudioControlsChain chain = new AudioControlsChain(id, index, name, ChannelFormat.STEREO); // !!! !!!
        chain.add(new MeterControls(ChannelFormat.STEREO, "")); // !!! !!!!
        add(chain);
        return chain;
    }

    public int getTrackCount() {
        return trackCount;
    }
}
