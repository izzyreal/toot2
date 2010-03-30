// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.distort;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.SimpleAudioProcess;
import uk.org.toot.dsp.DCBlocker;
import uk.org.toot.dsp.filter.FIRDesigner;
import uk.org.toot.dsp.filter.FIROverSampler2;
import uk.org.toot.dsp.filter.OverSampler;
import static uk.org.toot.dsp.FastMath.tanh;

/*
 * A distortion effect which uses oversampling to significantly reduce aliasing.
 */
public class Distort1Process extends SimpleAudioProcess
{
	private Distort1Variables vars;
	private OverSampler overSampler;
	private DCBlocker dc;
	private int sampleRate = -1;
	
	public Distort1Process(Distort1Variables vars) {
		this.vars = vars;
	}

	private void design() {
		final int R = 8;				// oversample Rate
		final int A = 60;				// Attenuation
		final int NN = sampleRate / 2;	// Nyquist Normal
		final int NO = NN * R;			// Nyquist Oversampled
		
		final float FI = 7000f;		
		float[] ia = FIRDesigner.designLowPass(FI/NO, (NN-FI)/NO, A);
		
		final float FD = 14000f;
		float[] da = FIRDesigner.designLowPass(FD/NO, (NN-FD)/NO, A);

		overSampler = new FIROverSampler2(R, 2, ia, da); // !!! STEREO
		dc = new DCBlocker();
	}
	
	/**
	 * Our rms 0dB is typically 0.1 so we multiply by 2 before applying the function,
	 * which maxes out at output 1 for input 1. We also apply a variable input gain to allow the user to select
	 * the sweet spot. Afterwards we divide by 10 to get back to our nominal 0dB. 
	 */
	public int processAudio(AudioBuffer buffer) {
		if ( vars.isBypassed() ) return AUDIO_OK;
		int srate = (int)buffer.getSampleRate();
		if ( srate != sampleRate ) {
			sampleRate = srate;
			design();
		}
        int nsamples = buffer.getSampleCount();
        int nchans = buffer.getChannelCount() > 2 ? 2 : 1; // only mono or stereo
        float bias = vars.getBias();
        float gain = vars.getGain();
        float inverseGain = 1f / gain;
        // attempt to maintain contant rms level as signal saturates
        if ( inverseGain < 0.2f ) inverseGain = 0.2f;
        float[] samples;
        float[] upSamples;
        float sample;
        for ( int c = 0; c < nchans; c++ ) {
        	samples = buffer.getChannel(c);
        	for ( int s = 0; s < nsamples; s++ ) {
        		upSamples = overSampler.interpolate(bias + gain * samples[s], c);
        		for ( int i = 0; i < upSamples.length; i++ ) {
        			sample = tanh(upSamples[i]);
        			sample += 0.1f * sample * sample; // 10% 2nd harmonic, like valves
        			upSamples[i] = sample;
        		}
        		samples[s] = dc.block(inverseGain * overSampler.decimate(upSamples, c));
        	}
        }
		return AUDIO_OK;
	}	
}
