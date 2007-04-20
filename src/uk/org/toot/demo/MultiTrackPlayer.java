// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import java.io.File;
import java.util.List;
import uk.org.toot.control.*;
import uk.org.toot.audio.core.*;
import uk.org.toot.audio.meter.*;
import uk.org.toot.audio.server.AudioClient;
import uk.org.toot.transport.*;

/**
 * MultiTrackPlayer is an experimental multi-track player that has been
 * generalised from the Release 1 demo code.
 */
public class MultiTrackPlayer implements AudioClient
{
    private Transport transport;
    private TransportListener transportListener;
    private List<PlayerProcess> filePlayers; // !!!
    private int trackLimit = 0;
    private boolean playing = false;
    private boolean playRequest = false;
    private boolean stopRequest = false;
//    private long locateRequest = -1;

    /**
     * @link aggregation
     * @supplierCardinality 0..1 
     */
    protected MultiTrackControls multiTrackControls = null;

    public MultiTrackPlayer(Transport t) {
        transport = t;
		transportListener = new TransportListener() {
            public void play() {
                playRequest = true;
            }
			public void stop() {
                stopRequest = true;
            }
			public void record(boolean rec) {
            }
            public void locate(long microseconds) {
                MultiTrackPlayer.this.locate(microseconds);
            }
        };
        transport.addTransportListener(transportListener);
        filePlayers = new java.util.ArrayList<PlayerProcess>();
    }

    public MultiTrackPlayer(Transport t, MultiTrackControls mtc) {
        this(t);
        multiTrackControls = mtc;
        Control[] controls = multiTrackControls.getMemberControls();
        trackLimit = controls.length;
        for ( int i = 0; i < trackLimit; i++ ) {
            filePlayers.add(new PlayerProcess((AudioControlsChain)controls[i]));
        }
//		filePlayers.get(0).debug = true;
    }

    public void work(int nFrames) {
        // sync transport with dsp
        if ( stopRequest ) {
            playing = false;
        } else if ( playRequest ) {
            playing = true;
        }
        stopRequest = false;
        playRequest = false;
    }

    protected void locate(long microseconds) {
//        System.out.println("Locate "+microseconds);
        for ( PlayerProcess player : filePlayers ) {
            player.locate(microseconds);
        }
    }

    public void setTrack(int trk, File f, String name) {
        filePlayers.get(trk).setFile(f, name);
    }

    public List<PlayerProcess> getProcesses() {
        return filePlayers;
    }

    public int getTrackLimit() { return trackLimit; }

    /**
     * PlayerProcess extends AudioFilePlayerProcess with a K-System meter and
     * for efficiency avoids metering if there is no audio connected.
     */
    public class PlayerProcess extends AudioFilePlayerProcess
    {
        private AudioControlsChain chain;
        private MeterProcess meter;
        private AudioBuffer.MetaInfo metaInfo = null;

        public PlayerProcess(AudioControlsChain controls) {
            chain = controls;
            // find MeterControls in controls
            for ( Control c : controls.getControls() ) {
                if ( c instanceof MeterControls ) {
		            meter = new MeterProcess((MeterControls)c);
                }
            }
        }

        // !!! !!! may need resetting on setFile ???
        private int prevPlayRet = AUDIO_DISCONNECT;

        public int processAudio(AudioBuffer buffer) {
            int ret = prevPlayRet;
            if ( playing ) {
                ret = super.processAudio(buffer); // may be AUDIO_DISCONNECT !
                prevPlayRet = ret;
            } else if ( prevPlayRet != AUDIO_DISCONNECT ) {
                // if we are stopped and we were previously playing
                // we need to return silence to allow effects decays!
                buffer.makeSilence();
		        attachMetaInfo(buffer);
            }

            if ( ret != AUDIO_DISCONNECT ) {
	            if ( metaInfo != buffer.getMetaInfo() ) {
   		            metaInfo = buffer.getMetaInfo();
     		        if ( metaInfo != null ) { // !!! !!! why necessary ???
           		    	chain.setSourceLabel(metaInfo.getSourceLabel());
//                        System.out.println(metaInfo.getSourceLabel());
             		}
	        	}
	            if ( meter != null ) {
    	   	        meter.processAudio(buffer);
        	   	}
            }

            return ret;
        }
    }
}
