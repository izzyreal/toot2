// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;

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

    private float[] modulatorPhase;
    private int[] modulatorMap;

    private boolean wasBypassed;

    public ModulatedDelayProcess(ModulatedDelayVariables vars) {
        this.vars = vars;
        modulatorPhase = new float[2]; // STEREO controls
        modulatorMap = new int[8]; // !!! !!!
        wasBypassed = !vars.isBypassed(); // force update
    }

    public void open() {
        // buffer allocation is deferred until sample rate is known
    }

    // costly algorithm, but typically <1% of 2GHz CPU
    public int processAudio(AudioBuffer buffer) {
        float sampleRate = buffer.getSampleRate();
        int bchans = buffer.getChannelCount() < 2 ? 2 : buffer.getChannelCount();
        if ( wetBuffer == null ) {
	        wetBuffer = new DelayBuffer(bchans,
                msToSamples(vars.getMaxDelayMilliseconds(), sampleRate),
                sampleRate);
        }
        if ( dryBuffer == null ) {
            dryBuffer = new DelayBuffer(bchans,
                msToSamples(vars.getMaxDelayMilliseconds()/2, sampleRate),
                sampleRate);
        }
        if ( buffer.getChannelCount() == 1 ) {
            buffer.convertTo(ChannelFormat.STEREO);
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
        // calculate delays, including modulation
        float timeDelta = 1 / sampleRate; // seconds
        int ns = buffer.getSampleCount();
        int nc = buffer.getChannelCount();
        ChannelFormat format = buffer.getChannelFormat();
        for ( int c = 0; c < nc; c++ ) {
            if ( format.isLeft(c) ) modulatorMap[c] = 0; // !!!
            else if ( format.isRight(c) ) modulatorMap[c] = 1; // !!!
            else modulatorMap[c] = -1;
        }
        float scaledDepth = staticDelay * depth;
        float out;
        float in;
        float[] buf;
        // evaluate one sample at a time
        for ( int s = 0; s < ns; s++ ) {
	        for ( int ch = 0; ch < nc; ch++ ) {
                buf = buffer.getChannel(ch);
		        float modulatedDelay =
                    modulation(modulatorMap[ch], timeDelta) * scaledDepth;
                out = wetBuffer.out(ch, staticDelay + modulatedDelay);
                if ( isDenormal(out) ) out = 0f; // solves internal denormal
                float fb = feedback * out;
                in = buf[s];
                if ( isDenormal(in) ) in = 0f; // ??? doesn't seem to help
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

    // -1 >=  modulation <= +1
    protected float modulation(int chan, float timeDelta) {
        if ( chan < 0 ) return 0f;
        double phaseDelta = timeDelta * vars.getRate() * 2 * Math.PI;

        if ( chan == 0 ) {
	        modulatorPhase[chan] += phaseDelta;
    	} else if ( chan == 1 ) {
        	modulatorPhase[chan] = modulatorPhase[0] + vars.getPhaseRadians();
    	}

        if ( modulatorPhase[chan] > Math.PI ) {
   	        modulatorPhase[chan] -= 2 * Math.PI;
       	}

        int shape = vars.getLFOShape();
        float mod = (shape == 0) ? sine(modulatorPhase[chan])
                                 : triangle(modulatorPhase[chan]);
        // clamp the cheapo algorithm which goes outside range a little
        if ( mod < -1f ) mod = -1f;
        else if ( mod > 1f ) mod = 1f;
        return mod;
//        return (float)Math.sin(modulatorPhase[chan]);
    }

    // http://www.devmaster.net/forums/showthread.php?t=5784
    private static final float S_B = (float)(4 /  Math.PI);
    private static final float S_C = (float)(-4 / (Math.PI*Math.PI));

    // -PI < x < PI
    protected float sine(float x) {
        return S_B * x + S_C * x * Math.abs(x);
    }

    // -PI < x < PI
    // thanks scoofy[AT]inf[DOT]elte[DOT]hu
    // for musicdsp.org pseudo-code improvement
    protected float triangle(float x) {
        x += Math.PI;		// 0 < x < 2*PI
        x /= Math.PI / 2;   // 0 < x < 4
        x -= 1;				// -1 < x < 3
        if ( x > 1 ) x -= 4f;
        return Math.abs(-(Math.abs(x)-2)) - 1;
    }

    protected int msToSamples(float ms, float sr) {
        return Math.round((ms * sr) / 1000);
    }
}
