// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.dsp.FastMath;

import static uk.org.toot.audio.core.FloatDenormals.*;
/**
 * A Modulated Delay Process
 * Currently very crude with no interpolation so it's noisey at longer delays.
 */
public class ModulatedDelayProcess implements AudioProcess
{
    /**
     * @link aggregationByValue
     * @supplierCardinality 1 
     */
    private DelayBuffer wetBuffer; // needs to interpolate !!! !!!

    /**
     * Used in 'Tape' mode for 'through-the-null' modulation.
     * Allows the wet delay to be negative (less than the dry delay).
     * @link aggregationByValue
     * @supplierCardinality 1 
     */
    private DelayBuffer dryBuffer;

    /**
     * @link aggregation
     * @supplierCardinality 1 
     */
    private final ModulatedDelayVariables vars;
    protected int[] modulatorMap;
    protected float modulatorPhase;

    protected ChannelFormat format;

    private boolean wasBypassed;

    public ModulatedDelayProcess(ModulatedDelayVariables vars) {
        this.vars = vars;
        modulatorMap = new int[8]; // !!! 8 channel max
        wasBypassed = !vars.isBypassed(); // force update
    }

    public void open() {
        // buffer allocation is deferred until sample rate is known
    }

    // costly algorithm, but typically <1% of 2GHz CPU
    public int processAudio(AudioBuffer buffer) {
        float sampleRate = buffer.getSampleRate();
        int ns = buffer.getSampleCount();
        int nc = buffer.getChannelCount();
        if ( wetBuffer == null ) {
	        wetBuffer = new DelayBuffer(nc,
                msToSamples(vars.getMaxDelayMilliseconds(), sampleRate),
                sampleRate);
        }
        if ( dryBuffer == null ) {
            dryBuffer = new DelayBuffer(nc,
                msToSamples(vars.getMaxDelayMilliseconds()/2, sampleRate),
                sampleRate);
        }
        // append buffer to conformed dry buffer, anti-denorm when tapped
      	dryBuffer.append(buffer);
		// we don't bypass the dry buffer to minimise bypass glitches ???
        boolean bypassed = vars.isBypassed();
        if ( bypassed ) {
            if ( !wasBypassed ) {
                // silence wet buffer on transition to bypassed ??? ??? !!! !!!
                wetBuffer.makeSilence();
                wasBypassed = true;
            }
            return AUDIO_OK;
        }

        float depth = vars.getDepth();
        float feedback = vars.getFeedback();
        float mix = vars.getMix();
        float wetMix = vars.isWetInverted() ? -mix : mix;
        int staticDelay = (int)(dryBuffer.msToSamples(vars.getDelayMilliseconds()));

		wetBuffer.conform(buffer);

        ChannelFormat f = buffer.getChannelFormat();
        if ( format != f ) {
            format = f;
	        buildModulatorMap(buffer);
        }

        // calculate delays, including modulation
        float timeDelta = 1 / sampleRate; // seconds
//        ChannelFormat format = buffer.getChannelFormat();
        float scaledDepth = staticDelay * depth;
        float out;
        float in;
        float[] buf;
        // evaluate one sample at a time
        for ( int s = 0; s < ns; s++ ) {
            incrementModulators(timeDelta);
	        for ( int ch = 0; ch < nc; ch++ ) {
                buf = buffer.getChannel(ch);
		        float modulatedDelay = modulation(ch) * scaledDepth;
                out = wetBuffer.out(ch, staticDelay + modulatedDelay);
                out = zeroDenorm(out); // solves internal denormal
                float fb = feedback * out;
                in = buf[s];
                wetBuffer.append(ch, in + fb); // input + feedback
                buf[s] = out * wetMix; // wet output rewrites buffer
            }
            wetBuffer.nudge(1);
        }

        dryBuffer.tap(buffer, vars.isTape() ? staticDelay : 0, 1-mix);
        wasBypassed = bypassed;
        return AUDIO_OK;
    }

    public void close() {
        wetBuffer = null;
        dryBuffer = null;
    }

    protected void buildModulatorMap(AudioBuffer buffer) {
        // don't modulate LFE, constructive interference could blow woofers
       	int nc = buffer.getChannelCount();
        for ( int ch = 0; ch < nc; ch++ ) {
   	        if ( format.isLFE(ch) ) modulatorMap[ch] = -1;
           	else modulatorMap[ch] = 0;
        }
    }

    protected void incrementModulators(float timeDelta) {
        double phaseDelta = timeDelta * vars.getRate() * 2 * Math.PI;
        modulatorPhase += phaseDelta;
        if ( modulatorPhase > Math.PI ) {
   	        modulatorPhase -= 2 * Math.PI;
       	}
    }

    // -1 >=  modulation <= +1
    protected float modulation(int chan) {
        if ( modulatorMap[chan] < 0 ) return 0f;
        int shape = vars.getLFOShape();
        float mod = (shape == 0) ? 
        		FastMath.sin(modulatorPhase) : 
        		FastMath.triangle(modulatorPhase);
        // clamp the cheapo algorithm which goes outside range a little
        if ( mod < -1f ) mod = -1f;
        else if ( mod > 1f ) mod = 1f;
        return mod;
    }

    protected int msToSamples(float ms, float sr) {
        return Math.round((ms * sr) / 1000);
    }
}
