// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.eq;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.core.SimpleAudioProcess;
import uk.org.toot.audio.filter.ToneStackSection;

public class ToneStackEQProcess extends SimpleAudioProcess
{
	private Variables vars;
	private ToneStackSection stack;
	private int sampleRate = -1; // force initialisation
    private boolean wasBypassed;
	
	public ToneStackEQProcess(Variables vars) {
		this.vars = vars;
		stack = new ToneStackSection();
	}
	
	public int processAudio(AudioBuffer buffer) {
        boolean bypassed = vars.isBypassed();
        if ( bypassed ) {
            if ( !wasBypassed ) {
                stack.clear(); // clear filter history on transition to bypassed
                wasBypassed = true;
            }
            return AUDIO_OK;
        }
        wasBypassed = bypassed;
		int sr = (int)buffer.getSampleRate();
		if ( sr != sampleRate ) {
			sampleRate = sr;
			stack.updateCoefficients(vars.setSampleRate(sampleRate));
		} else if ( vars.hasChanged() ) {
			stack.updateCoefficients(vars.getCoefficients());
		}
		if ( buffer.getChannelCount() > 1 ) {
			buffer.convertTo(ChannelFormat.MONO);
		}
		stack.filter(buffer.getChannel(0), buffer.getSampleCount());
		return AUDIO_OK;
	}
	
	public interface Variables
	{
		boolean isBypassed();
		boolean hasChanged();
		ToneStackSection.Coefficients setSampleRate(float rate);
		ToneStackSection.Coefficients getCoefficients();
	}
}
