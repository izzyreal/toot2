/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.SimpleAudioProcess;

abstract public class DynamicsProcess extends SimpleAudioProcess
{
    protected float envelope = 0f;

    protected boolean isPeak = false;
    protected float threshold;
    protected float thresholddB;
    protected float ratio;
    protected float attack, release;
    protected float makeupGain;

    protected ProcessVariables vars;

    private boolean wasBypassed;

    public DynamicsProcess(ProcessVariables vars) {
        this(vars, false);
        wasBypassed = !vars.isBypassed(); // force update
    }

    public DynamicsProcess(ProcessVariables vars, boolean peak) {
        this.vars = vars;
        this.isPeak = peak;
    }

    public void clear() {
        envelope = 1f; // envelope of gain
       	vars.setDynamicGain(1f);
    }

    /**
     * Called once per AudioBuffer
     */
    protected void cacheProcessVariables() {
        // update local variables
        threshold = vars.getThreshold();
        thresholddB = vars.getThresholddB();
        ratio = vars.getRatio();
        attack = vars.getAttack();
        release = vars.getRelease();
        makeupGain = vars.getGain();
    }

    /**
     * Called once per AudioBuffer
     */
	public int processAudio(AudioBuffer buffer) {
        boolean bypassed = vars.isBypassed();
        if ( bypassed ) {
            if ( !wasBypassed ) {
                clear();
            }
            wasBypassed = true;
            return AUDIO_OK;
        }
        vars.update(buffer.getSampleRate()); // rederives attack, release
        cacheProcessVariables();
        int nc = buffer.getChannelCount();
        int len = buffer.getSampleCount();
        int mslen = (int)(buffer.getSampleRate() / 1000);

        float[]/*[]*/ samples; // = getAllChannelsSamples(buffer); // !!!
        float dynamicGain = 1f; // unity
        float gain = dynamicGain * makeupGain;

        for (int i = 0; i < len; i++) {
            // the side chain calculations, every millisecond
            if ( (i % mslen) == 0  ) {
	            double key = 0;
                if ( isPeak ) {
/*                	for ( int j = 0; j < mslen; j++ ) {
    	    	    	for ( int c = 0; c < nc; c++ ) {
        		        	key = Math.max(key, Math.abs(samples[c][i+j]));
    	        		}
	                } */
   	    	    	for ( int c = 0; c < nc; c++ ) {
    					samples = buffer.getChannel(c);
	                	for ( int j = 0; j < mslen; j++ ) {
        		        	key = Math.max(key, Math.abs(samples[i+j]));
    	        		}
	                }
                } else { // rms
                	float sample;
                	float sumOfSquares = 0f;
/*                	for ( int j = 0; j < mslen; j++ ) {
    	    	    	for ( int c = 0; c < nc; c++ ) {
        		        	sample = samples[c][i+j];
                            sumOfSquares += sample * sample;
    	        		}
	                } */
   	    	    	for ( int c = 0; c < nc; c++ ) {
    					samples = buffer.getChannel(c);
	                	for ( int j = 0; j < mslen; j++ ) {
        		        	sample = samples[i+j];
                            sumOfSquares += sample * sample;
    	        		}
	                }
                	key = Math.sqrt(sumOfSquares / (mslen * nc));
                }
		        dynamicGain = evaluateSideChain((float)(key));
	            gain = dynamicGain * makeupGain;
            }
            // affect all channels identically to preserve positional image
            for ( int c = 0; c < nc; c++ ) {
                buffer.getChannel(c)[i] *= gain;
                // samples[c][i] *= gain;
            }
        }
        // we only announce the final value at the end of the buffer
        // this effectively subsamples the dynamic gain
        // but typically attack and release will provide sufficient smoothing
        // for the avoidance of aliasing
		vars.setDynamicGain(dynamicGain);
        wasBypassed = bypassed;
        return AUDIO_OK;
    }

	protected float evaluateSideChain(float key) {
        return dynamics(function(key));
    }

    // effect of comparison of detected against threshold - subclass issue
    protected abstract float function(float value);

    // hold is a gate subclass issue
    protected float dynamics(float target) {
        // anti-denormal not needed, decays to unity
        // seems back to front because (>)0 is max and 1 is min gain reduction
        float factor = target < envelope ?  attack : release;
		envelope = factor * (envelope - target) + target;
        return envelope;
    }

    /**
     * Specifies parameters in implementation terms
     */
    public interface ProcessVariables {
        void update(float sampleRate);
        boolean isBypassed();
        float getThreshold(); 	//  NOT dB, the actual level
        float getThresholddB(); //  dB
        float getRatio();
        float getKnee();		//	NOT dB, the actual level
        float getAttack();		//	NOT ms, the exponential coefficient
        int getHold();			//	NOT ms, samples
        float getRelease();		//	NOT ms, the exponential coefficient
        float getDepth();		//	NOT dB, the actual level
        float getGain();		// 	NOT dB, the actual static makeup gain
        void setDynamicGain(float gain); // NOT dB, the actual (sub sampled) dynamic gain
    }
}
