// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.reverb;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.core.SimpleAudioProcess;

import static uk.org.toot.audio.core.FloatDenormals.*;

/**
 * A literal implementation of the network diagram from Jon Dattorro's Effect Design Part 1,
 * Reverberator and Other Filters. Allegedly this is based on Griesinger's work, as were
 * Lexicon hardware reverberators.
 * @author st
 */
public class PlateProcess extends SimpleAudioProcess
{
	private PlateVariables vars;
	private float tank1zm1 = 0f;
	private Tank tank1, tank2;
	
	private Delay ipd;
	private Filter bw;
	private Diffuser id1a, id1b, id2a, id2b;
	
	private int preDelaySamples;
	private float bandwidth;
	private float inputDiffusion1, inputDiffusion2;
	private float damping, decay;
	private float decayDiffusion1, decayDiffusion2;
	
	private float[] samplesL, samplesR;
	
	public PlateProcess(PlateVariables vars) {
		this.vars = vars;
		tank1 = new Tank(true);
		tank2 = new Tank(false);
		ipd = new Delay(1+vars.getMaxPreDelaySamples());
		bw = new Filter();
		id1a = new Diffuser(142);
		id1b = new Diffuser(107);
		id2a = new Diffuser(379);
		id2b = new Diffuser(277);
	}
	
	private void cacheVariables() {
		preDelaySamples = vars.getPreDelaySamples();
		bandwidth = vars.getBandwidth();
		inputDiffusion1 = vars.getInputDiffusion1();
		inputDiffusion2 = vars.getInputDiffusion2();
		damping = vars.getDamping();
		decay = vars.getDecay();
		decayDiffusion1 = vars.getDecayDiffusion1();
		decayDiffusion2 = vars.getDecayDiffusion2();
	}
	
	public int processAudio(AudioBuffer buffer) {
		if ( vars.isBypassed() ) return AUDIO_OK;
		cacheVariables();
		int ns = buffer.getSampleCount();
		int nc = buffer.getChannelCount();
		if ( nc != 2 ) {
			buffer.convertTo(ChannelFormat.STEREO);
			if ( nc > 2 ) nc = 2;
		}
		samplesL = buffer.getChannel(0);
		samplesR = buffer.getChannel(1);
		for ( int i = 0; i < ns; i++ ) {
			float in = samplesL[i];
			if ( nc == 2 ) {
				in += samplesR[i];
				in *= 0.5f;
			}
			reverb(in * 0.6f);
			samplesL[i] = tank1.output(0) + tank2.output(0);
			samplesR[i] = tank1.output(1) + tank2.output(1);
		}
		return AUDIO_OK;
	}

	private void reverb(float sample) {
		sample = idiffuse(sample);
		tank1zm1 = tank1.tick(sample + tank2.tick(sample + tank1zm1));
	}
	
	private float idiffuse(float sample) {
		// pre delay
		ipd.delay(sample);
		// bandwidth
		// input diffusion 1 x 2
		// input diffusion 2 x 2
		return id2b.diffuse(
				id2a.diffuse(
					id1b.diffuse(
						id1a.diffuse(
							bw.filter(
								ipd.tap(preDelaySamples), 1-bandwidth), 
								inputDiffusion1), 
								inputDiffusion1), 
								inputDiffusion2), 
								inputDiffusion2);
	}
	
	private class Tank
	{
		private boolean first;
		
		private Diffuser dif1, dif2;
		private Delay del1, del2;
		private Filter filter;
		
		public Tank(boolean first) {
			this.first = first;
			dif1 = new Diffuser(first ? 672 : 908);
			del1 = new Delay(first ? 4453 : 4217);
			filter = new Filter();
			dif2 = new Diffuser(first ? 1800 : 2656);
			del2 = new Delay(first ? 3720 : 3163);
		}
		
		public float tick(float sample) {
			// decay diffusion 1, note sign, and delay
			// damping
			// decay diffusion 2 and delay
			return decay * del2.delay(
				dif2.diffuse(
					filter.filter(
						del1.delay(
							dif1.diffuse(sample, -decayDiffusion1)), 
							damping) * decay, decayDiffusion2));
		}
		
		public float output(int chan) {
			if ( first ) {
				if ( chan == 0 ) {
					return -del2.tap(1066) - del1.tap(1990) - dif2.tap(187);					
				} else {
					return del1.tap(353) + del1.tap(3627) - dif2.tap(1228) + del2.tap(2673);
				}
			} else {
				if ( chan == 0 ) {
					return del1.tap(266) + del1.tap(2974) - dif2.tap(1913) + del2.tap(1996);
				} else {
					return -del1.tap(2111) - dif2.tap(335) - del2.tap(121); 
				}
				
			}
		}
	}
	
	private class Filter
	{
		private float zm1 = 0;
		
		public float filter(float sample, float k) {
			return zm1 = zeroDenorm(k * (zm1 - sample) + sample);
		}
	}
	
	/**
	 * A fixed length delay that can be tapped
	 * @author st
	 */
	private class Delay
	{
		private float[] line;
		private int head = 0;
		
		public Delay(int length) {
			line = new float[length];
		}
		
		public float delay(float sample) {
			float s = line[head];
			line[head++] = sample;
			if ( head > line.length-1 ) head = 0;
			return s;
		}
		
		public float tap(int zm) {
			assert zm < line.length;
			int p = head - zm;
			if ( p < 0 ) p += line.length;
			return line[p];
		}
	}
	
	private class Diffuser extends Delay
	{
		private float b = 0;
		
		public Diffuser(int length) {
			super(length-1);
		}
		
		public float diffuse(float sample, float k) {
			float a = sample - k * b;
			float out = k * a + b;
			b = zeroDenorm(delay(a));
			return out;
		}
	}
}
