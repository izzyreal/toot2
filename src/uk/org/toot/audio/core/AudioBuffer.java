// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.core;

import org.tritonus.share.sampled.FloatSampleBuffer;

/**
 * Encapsulates buffered multi-channel sampled audio.
 * 
 * It has a ChannelFormat and enables meta information to be attached to buffers.
 * It can convert to another ChannelFormat (1->N and N->1 only)
 * 
 * It has a real-time property to allow AudioProcesses to discriminate
 * between real-time and non-real-time for quality purposes etc.
 * @see uk.org.toot.audio.server.NonRealTimeAudioServer
 * 
 * It can swap channel pairs.
 */
public class AudioBuffer extends FloatSampleBuffer
{
    private MetaInfo metaInfo;

    /**
     * @link aggregation
     * @supplierCardinality 0..1 
     */
    private ChannelFormat channelFormat;
    private boolean realTime = true;
    private String name = "unknown";

    public AudioBuffer(String name, int channelCount, int sampleCount, float sampleRate) {
        super(channelCount, sampleCount, sampleRate);
        this.name = name;
    }

    public String getName() { return name; }

    protected void setChannelCount(int count) {
        if ( count == getChannelCount() ) return;
        if ( count < getChannelCount() ) {
        	for ( int ch = getChannelCount() - 1; ch > count - 1; ch-- ) {
            	removeChannel(ch);
        	}
        } else {
            while ( getChannelCount() < count ) {
                addChannel(false);
            }
        }
    }

    public void setMetaInfo(MetaInfo info) {
        metaInfo = info;
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    /**
    * This method may be used by an AudioProcess to determine whether it can use
    * high quality algorithms that would be impossible in real-time.
    * If it's not in real-time an AudioProcess may take as long as it needs.
    */
    public boolean isRealTime() {
        return realTime;
    }

    /**
    * This method is intended for use by
    * uk.org.toot.audio.server.NonRealTimeAudioServer
    * No good will come from you calling it.
    */
    public void setRealTime(boolean realTime) {
        this.realTime = realTime;
    }

    protected ChannelFormat guessFormat() {
//        System.out.println(getName()+" format guessed");
        switch ( getChannelCount() ) {
        case 1: return ChannelFormat.MONO;
        case 2: return ChannelFormat.STEREO;
        case 4: return ChannelFormat.QUAD;
        }
        return ChannelFormat.STEREO;

    }

    /**
     * Guesses format if unset.
     */
    public ChannelFormat getChannelFormat() {
        if ( channelFormat == null ) channelFormat = guessFormat();
        return channelFormat;
    }

    /**
     * May call setChannelCount accordingly
     */
    public void setChannelFormat(ChannelFormat format) {
        channelFormat = format;
        if ( channelFormat != null ) {
        	setChannelCount(channelFormat.getCount());
        }
    }

    public void convertTo(ChannelFormat format) {
        if ( channelFormat == null ) channelFormat = guessFormat();
        if ( channelFormat == format ) return; // already requested format
        if ( format.getCount() == 1 ) { 				// N -> 1
            mixDownChannels();
            channelFormat = format;
        } else if ( channelFormat.getCount() == 1 ) {	// 1 -> N
            this.expandChannel(format.getCount());
            // does LFE need tweaking ??? !!!
            channelFormat = format;
        } else {										// N -> M
	        // how do we convert other formats ???
	        // get format with highest channel count to do it
    	    // because only it knows about that many channels
        	@SuppressWarnings("unused")
			ChannelFormat convertingFormat =
                channelFormat.getCount() > format.getCount()
            		? channelFormat : format;
//        	if ( convertingFormat.convertTo(format) ) {
//        	}
        }
    }

    public void swap(int a, int b) {
        int ns = getSampleCount();
        float[] asamples = getChannel(a);
        float[] bsamples = getChannel(b);
        float tmp;
        for ( int s = 0; s < ns; s++ ) {
			tmp = asamples[s];
            asamples[s] = bsamples[s];
            bsamples[s] = tmp;
        }
    }

    /**
     * MetaInfo holds meta information for an AudioBuffer.
     * MetaInfo is intentionally immutable.
     * 'observers' will be able to simply detect a different MetaInfo
     * if any information is changed.
     * TODO It does not scale and should be replaced with a source label property?
     */
    static public class MetaInfo
    {
        private String sourceLabel;

        public MetaInfo(String sourceLabel) {
            this.sourceLabel = sourceLabel;
        }

        public String getSourceLabel() {
            return sourceLabel;
        }
    }
}
