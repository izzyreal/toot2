// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.mixer;

import uk.org.toot.control.*;
import uk.org.toot.audio.core.*;
import static uk.org.toot.audio.mixer.MixerControlsIds.*;

/**
 * An AudioMixerStrip is an AudioProcessChain which can be connected to by
 * means of setInputProcess() and setDirectOutputProcess() and allows arbitrary
 * insertion and ordering of plugin modules.
 */
public class AudioMixerStrip extends AudioProcessChain {
    /**
     * @supplierCardinality 1
     * @link aggregation 
     */
    protected AudioMixer mixer;

    /**
     * @link aggregation
     * @supplierCardinality 0..1
     */
    private AudioBuffer buffer = null;

    private AudioBuffer.MetaInfo metaInfo;

    /**
     * @supplierCardinality 0..1
     * @link aggregation 
     * @label input
     */
    private AudioProcess input = null;

    private AudioProcess directOutput = null;

    private boolean isChannel = false;

    private ChannelFormat channelFormat;

    private int nmixed = 1;

    public AudioMixerStrip(AudioMixer mixer, AudioControlsChain controlsChain) {
        super(controlsChain);
        this.mixer = mixer;
        buffer = createBuffer(); // side effects !
		channelFormat = buffer.getChannelFormat();
    }

    public void setInputProcess(AudioProcess input) throws Exception {
        if ( controlChain.getId() != CHANNEL_STRIP ) {
            throw new Exception("No external input to this Strip type");
        }
        this.input = input;
        input.open();
//        System.out.println(getName()+" input process is "+this.input);
    }

    public void setDirectOutputProcess(AudioProcess output) throws Exception {
        this.directOutput = output;
    }

    public void silence() {
        if ( nmixed > 0 ) {
	        buffer.makeSilence();
            nmixed = 0;
        }
    }

    protected AudioBuffer createBuffer() {
        int id = controlChain.getId();
        if ( id == CHANNEL_STRIP ) {
            isChannel = true;
	        return mixer.getSharedBuffer();
        } else if ( id == GROUP_STRIP ) {
            AudioBuffer buf = mixer.createBuffer(getName());
            buf.setChannelFormat(mixer.getMainBus().getBuffer().getChannelFormat());
            return buf;
        } else if ( id == MAIN_STRIP ) {
	        return mixer.getMainBus().getBuffer();
        }
        // must be a bus strip, so bus called the same as this
        return mixer.getBus(getName()).getBuffer();
    }

	protected boolean processBuffer() {
        if ( isChannel ) {
			// fast exit if CHANNEL with no input
        	if ( input == null ) return false;
	        int ret = input.processAudio(buffer);
            if ( ret == AUDIO_DISCONNECT ) return false;
        }
        processAudio(buffer);
        if ( directOutput != null ) {
            directOutput.processAudio(buffer);
        }
        checkMetaInfo(buffer.getMetaInfo());
        return true;
    }

    protected void checkMetaInfo(AudioBuffer.MetaInfo info) {
        if ( metaInfo == info ) return; // no change
        metaInfo = info;
        controlChain.setSourceLabel(metaInfo.getSourceLabel());
    }

    // intercept the SPI mechanism to handle bus mix controls
	protected AudioProcess createProcess(AudioControls controls) {
        if ( controls instanceof MixVariables ) {
            MixVariables vars = (MixVariables)controls;
            AudioMixerStrip routedStrip;
            if ( vars.getName().equals(mixer.getMainBus().getName()) ) {
                routedStrip = mixer.getMainStrip();
//                System.out.println(getName()+"/"+vars.getName()+" routing to "+routedStrip.getName());
                return new MainMixProcess(routedStrip, (MainMixVariables)vars, mixer);
            } else {
//                System.out.println(getName()+"/"+vars.getName()+" routing to "+vars.getName());
				routedStrip = mixer.getStrip(vars.getName());
	            return new MixProcess(routedStrip, vars);
            }
        }
        return super.createProcess(controls);
	}

	public int mix(AudioBuffer bufferToMix, float[] gain) {
        if ( bufferToMix == null ) return 0;
        int ret = channelFormat.mix(buffer, bufferToMix, gain);
        if ( ret != 0 ) nmixed += 1;
        return ret;
    }
}


