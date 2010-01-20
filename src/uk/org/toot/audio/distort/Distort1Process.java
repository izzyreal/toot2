// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.distort;

import java.util.Observer;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.SimpleAudioProcess;
import uk.org.toot.audio.filter.FIRDesign;
import uk.org.toot.audio.filter.FilterSpecification;
import uk.org.toot.dsp.filter.FIROverSampler;
import uk.org.toot.dsp.filter.FilterShape;
import uk.org.toot.dsp.filter.OverSampler;

public class Distort1Process extends SimpleAudioProcess
{
	private Distort1Variables vars;
	private OverSampler overSampler;
	
	public Distort1Process(Distort1Variables vars) {
		this.vars = vars;
		final int R = 4;
		final int A = 60;
		final int SR = 44100;
		final int nyquist = SR / 2;
		FilterSpecification iSpec = new FilterSpecification() {
			public void addObserver(Observer observer) {}
			public void deleteObserver(Observer observer) {}
			public FilterShape getShape() {	return FilterShape.LPF;	}
			public int getFrequency() {	return 7000; }
			public float getLevelFactor() {	return 1f; }
			public float getLeveldB() {	return 0f; }
			public float getResonance() { return 0.707f; }
		};
		FIRDesign iDesign = new FIRDesign(iSpec);
		iDesign.setTransitionBandwidth(nyquist - iSpec.getFrequency());
		iDesign.setAttenuation(A);
		iDesign.design(SR*R);
		float ia[] = iDesign.getCoefficients();
		System.out.println("I "+ia.length+" taps");
		scale(ia);
		
		FilterSpecification dSpec = new FilterSpecification() {
			public void addObserver(Observer observer) {}
			public void deleteObserver(Observer observer) {}
			public FilterShape getShape() {	return FilterShape.LPF;	}
			public int getFrequency() {	return 14000; }
			public float getLevelFactor() {	return 1f; }
			public float getLeveldB() {	return 0f; }
			public float getResonance() { return 0.707f; }
		};
		FIRDesign dDesign = new FIRDesign(dSpec);
		dDesign.setTransitionBandwidth(nyquist - dSpec.getFrequency());
		dDesign.setAttenuation(A);
		dDesign.design(SR*R);
		float da[] = dDesign.getCoefficients();
		System.out.println("D "+da.length+" taps");
		scale(da);

		overSampler = new FIROverSampler(4, 2, iDesign.getCoefficients(), dDesign.getCoefficients());
	}

	private float[] scale(float[] coeffs) {
		float sum = 0;
		for ( int i = 0; i < coeffs.length; i++ ) {
			sum += coeffs[i];
		}
		System.out.println("Sum "+sum);
		return coeffs;
	}
	
	/**
	 * Our 0dB is typically 0.1 so we multiply by 10 before applying the function,
	 * which maxes out at output 1 for input 1. Afterwards we divide by 10 to get back
	 * to our nominal 0dB. Wel also apply a variable input gain to allow the user to select
	 * the sweet spot.
	 */
	public int processAudio(AudioBuffer buffer) {
		if ( vars.isBypassed() ) return AUDIO_OK;
        int nsamples = buffer.getSampleCount();
        int nchans = buffer.getChannelCount();
        float gain = vars.getGain() * 10f;
        float inverseGain = 1f / gain;
        gain *= 6; // oversampling rate + 1 ?
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
	 * Distort an input sample.
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
