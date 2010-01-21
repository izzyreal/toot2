// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.distort;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.SimpleAudioProcess;
import uk.org.toot.dsp.filter.FIRDesigner;
import uk.org.toot.dsp.filter.FIROverSampler;
import uk.org.toot.dsp.filter.OverSampler;

/*
 * A distortion effect which uses 4x oversampling to significantly reduce aliasing.
 */
public class Distort1Process extends SimpleAudioProcess
{
	private Distort1Variables vars;
	private OverSampler overSampler;
	private int sampleRate = -1;
	
	public Distort1Process(Distort1Variables vars) {
		this.vars = vars;
	}

	private void design() {
		final int R = 5;				// oversample Rate
		final int A = 60;				// Attenuation
		final int NN = sampleRate / 2;	// Nyquist Normal
		final int NO = NN * R;			// Nyquist Oversampled
		
		final float FI = 7000f;		
		float[] ia = FIRDesigner.designLowPass(FI/NO, (NN-FI)/NO, A);
		
		final float FD = 14000f;
		float[] da = FIRDesigner.designLowPass(FD/NO, (NN-FD)/NO, A);

		overSampler = new FIROverSampler(R, 2, ia, da); // !!! STEREO		
	}
	
	/**
	 * Our 0dB is typically 0.1 so we multiply by 10 before applying the function,
	 * which maxes out at output 1 for input 1. Afterwards we divide by 10 to get back
	 * to our nominal 0dB. Wel also apply a variable input gain to allow the user to select
	 * the sweet spot.
	 */
	public int processAudio(AudioBuffer buffer) {
		if ( vars.isBypassed() ) return AUDIO_OK;
		int srate = (int)buffer.getSampleRate();
		if ( srate != sampleRate ) {
			sampleRate = srate;
			design();
		}
        int nsamples = buffer.getSampleCount();
        int nchans = buffer.getChannelCount();
        float gain = vars.getGain() * 10f;
        float inverseGain = 1f / gain;
        if ( inverseGain < 0.07f ) inverseGain = 0.07f;
        float[] samples;
        float[] upSamples;
        for ( int c = 0; c < nchans; c++ ) {
        	samples = buffer.getChannel(c);
        	for ( int s = 0; s < nsamples; s++ ) {
        		upSamples = overSampler.interpolate(samples[s], c);
        		for ( int i = 0; i < upSamples.length; i++ ) {
        			upSamples[i] = inverseGain * distort(upSamples[i] * gain);
        		}
        		samples[s] = overSampler.decimate(upSamples, c);
        	}
        }
		return AUDIO_OK;
	}
	
	/**
	 * Distort an input sample using the pade-approximation to tanh.
	 * public static to allow external code to call it.
	 * final to allow it to be inlined in processAudio().
	 * @param x input
	 * @return distorted output
	 */
	public static final float distort(float x) {
		// clamping is advisable because |x| > 5 does cause very high output
		// we clamp at 3 because that's where output is unity and C1/C2 continuous
		if ( x < -3 ) return -1;
		if ( x > 3 ) return 1;
		float x2 = x * x;
		return x * (27 + x2) / (27 + 9 * x2);
	}

}
