// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.tool;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.core.SimpleAudioProcess;
import uk.org.toot.dsp.Sine;

/**
 * @author st
 *
 */
public class SineProcess extends SimpleAudioProcess
{
	private SineControls controls;
    private Sine sine;
	
	public SineProcess(SineControls controls) {
		this.controls = controls;
        sine = new Sine(910*2*Math.PI/44100);
	}
	
	public int processAudio(AudioBuffer buffer) {
		if ( controls.isBypassed() ) return AUDIO_OK;
		buffer.setChannelFormat(ChannelFormat.MONO);
		int ns = buffer.getSampleCount();
		float[] samples = buffer.getChannel(0);
		for ( int i = 0; i < ns; i++ ) {
			samples[i] = sine.out() * 0.1f;
		}
		return AUDIO_OK;
	}
}
