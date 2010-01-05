// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.misc.plugin.Plugin;
import uk.org.toot.misc.plugin.PluginSupport;
import uk.org.toot.misc.TempoListener;

/**
 * A Tempo linked Delay Process
 * Basically delegating to DelayBuffer
 */
public class TempoDelayProcess implements AudioProcess
{
    /**
     * @link aggregationByValue
     * @supplierCardinality 1 
     */
    private DelayBuffer delayBuffer;

    /**
     * @link aggregationByValue
     * @supplierCardinality 1 
     */
    private DelayBuffer tappedBuffer; // just for conform()

    private PluginSupport support;
	private TempoListener tempoListener;

	/**
     * @link aggregation
     * @supplierCardinality 1 
     */
    private final TempoDelayVariables vars;

    private boolean wasBypassed;

    private float bpm = 120f;
    
    public TempoDelayProcess(TempoDelayVariables vars) {
        this.vars = vars;
        wasBypassed = !vars.isBypassed(); // force update
		support = Plugin.getPluginSupport();
		tempoListener = new TempoListener() {
			public void tempoChanged(float newTempo) {
				bpm = newTempo;				
			}			
		};		
    }

    public void open() {
		support.addTempoListener(tempoListener);
        // defer delay buffer allocation until sample rate known
    }

    /*
   	If all taps delays are > buffer time
    the delayed output is independent of input
	but need 3 buffers: buffer (in/out), delayBuffer, tappedBuffer
   	*/
    public int processAudio(AudioBuffer buffer) {
        boolean bypassed = vars.isBypassed();
        if ( bypassed ) {
            if ( !wasBypassed ) {
                if ( delayBuffer != null ) {
                    // silence delay buffer on transition to bypassed
                    delayBuffer.makeSilence();
                }
                wasBypassed = true;
            }
            return AUDIO_OK;
        }

        float sampleRate = buffer.getSampleRate();
        int ns = buffer.getSampleCount();
        int nc = buffer.getChannelCount();

        float feedback = vars.getFeedback();
		float mix = vars.getMix();

        if ( delayBuffer == null ) {
	        delayBuffer = new DelayBuffer(nc,
                msToSamples(vars.getMaxDelayMilliseconds(), sampleRate),
                sampleRate);
        } else {
            delayBuffer.conform(buffer);
        }

        if ( tappedBuffer == null ) {
	        tappedBuffer = new DelayBuffer(nc, ns, sampleRate);
        } else {
            tappedBuffer.conform(buffer);
            // conform only changes number of channels and sample rate
            if ( tappedBuffer.getSampleCount() != ns ) {
                tappedBuffer.changeSampleCount(ns, false);
            }
        }

    	// tapped from delay
    	tappedBuffer.makeSilence();
		int delay = (int)msToSamples(60000*vars.getDelayFactor()/bpm, sampleRate);
        for ( int c = 0; c < nc; c++ ) {
            if ( delay < ns ) continue; // can't evaluate. push down to called method?
    		delayBuffer.tap(c, tappedBuffer, delay, 1f); // optimised mix
		}
    	// delay append process + tapped * feedback
    	delayBuffer.append(buffer, tappedBuffer, feedback);
    	// process mixed from process and tapped
        for ( int c = 0; c < nc; c++ ) {
            float[] samples = buffer.getChannel(c);
            float[] tapped = tappedBuffer.getChannel(c);
            for ( int i = 0; i < ns; i++ ) {
                samples[i] += mix * tapped[i];
            }
        }

        wasBypassed = bypassed;
        return AUDIO_OK;
    }

    public void close() {
        delayBuffer = null;
        tappedBuffer = null;
		support.removeTempoListener(tempoListener);
    }

    protected int msToSamples(float ms, float sr) {
        return (int)((ms * sr) / 1000); // !!! !!! move elsewhere
    }
}
