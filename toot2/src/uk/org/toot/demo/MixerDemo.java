// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.demo;

import uk.org.toot.audio.core.*;
import uk.org.toot.audio.mixer.AudioMixer;

/**
 * MixerDemo simply displays the automated mixer and starts the (unstoppable)
 * transport after a short delay, if you pass any command line arguments the
 * so-called FullMixerPanel will be used instead of the preferred
 * CompactMixerPanel.
 */
public class MixerDemo extends AbstractDemo 
{
	public MixerDemo(String[] args) {
        super(args);
        transport.play();
    }

    protected void create(String[] args) {
        hasMultiTrack = false;
        super.create(args);
    }

    protected void connect(AudioMixer mixer) throws Exception {
        super.connect(mixer);
        int s = 2;
        AudioProcess p;
		int nsilence = 30;
        System.out.println("Creating "+nsilence+" Silent Inputs");
   	    for ( int i = 0; i < nsilence; i++ ) {
    		p = new SilentInputAudioProcess(ChannelFormat.STEREO, "S"+(1+i));
       	    mixer.getStrip(String.valueOf(s++)).setInputProcess(p);
        }
    }

    protected void createUI(String[] args) {
        super.createUI(args);
/*	    // pass an arg for full miser, nothing for compact mixer!
        JPanel panel;
        if ( args.length > 0 ) { // !!!
      		panel = new FullMixerPanel(mixerControls);
        } else {
       		panel = new CompactMixerPanel(mixerControls);
        }
       	frame(panel, "Toot Mixer"); */
    }

    public static void main(String[] args) {
        new MixerDemo(args);
    }
}
