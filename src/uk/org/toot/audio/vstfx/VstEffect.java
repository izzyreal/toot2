// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.vstfx;

import java.util.Arrays;

import com.synthbot.audioplugin.vst.vst2.*;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.misc.TempoListener;
import uk.org.toot.misc.plugin.Plugin;
import uk.org.toot.misc.plugin.PluginSupport;

/**
 * This class has a single audio output, for mono and stereo VST effects.
 * @author st
 *
 */
public class VstEffect implements AudioProcess
{
	private int nOutChan;
	private int nInChan;
	private float[][] inSamples;
	private float[][] outSamples;
	private int nsamples;
	private int sampleRate;
	private boolean mustClear = false;
	private VstEffectControls controls;
	private JVstHost2 vstfx;
	private boolean bypassed;
	private boolean wasBypassed = false;
	private PluginSupport support;
	private TempoListener tempoListener;
	
	public VstEffect(VstEffectControls controls) {
		this.controls = controls;
		vstfx = controls.getVst();
		nsamples = vstfx.getBlockSize();
		sampleRate = (int)vstfx.getSampleRate();
		
		nOutChan = vstfx.numOutputs();
		outSamples = new float[nOutChan][nsamples];
		if ( nOutChan >= 2 ) {
			nOutChan = 2;
		}
		                          
		nInChan = vstfx.numInputs(); // TODO == nOutChan?
		inSamples = new float[nInChan][nsamples];

		mustClear = vstfx.getVendorName().indexOf("Steinberg") >= 0;
/*		System.out.print("Using "+controls.getName());
		if ( mustClear ) System.err.println(" !!! Must Clear");
		else System.out.println(); */
		support = Plugin.getPluginSupport();
		tempoListener = new TempoListener() {
			public void tempoChanged(float newTempo) {
				vstfx.setTempo(newTempo);				
			}			
		};
	}

	public void open() throws Exception {
		System.out.print("Opening audio: "+controls.getName()+" ... ");
		vstfx.turnOn();
		System.out.println("opened");
		support.addTempoListener(tempoListener);
	}

	public int processAudio(AudioBuffer buffer) {
		bypassed = controls.isBypassed();
		if ( bypassed != wasBypassed ) {
			if ( bypassed ) vstfx.turnOff(); 
			else vstfx.turnOn();
			wasBypassed = bypassed;
		}
		if ( bypassed ) return AUDIO_OK;
        if ( buffer.getChannelCount() < 2 ) { // !!! must be mono
        	buffer.convertTo(ChannelFormat.STEREO);
        }
		int ns = buffer.getSampleCount();
		if ( ns != nsamples ) {
			nsamples = ns;
			outSamples = new float[nOutChan][nsamples];
			inSamples = new float[nInChan][nsamples];
			vstfx.turnOff();
			vstfx.setBlockSize(nsamples);
			vstfx.turnOn();
		}
		int sr = (int)buffer.getSampleRate();
		if ( sr != sampleRate ) {
			sampleRate = sr;
			vstfx.turnOff();
			vstfx.setSampleRate(sampleRate);
			vstfx.turnOn();
		}
		if ( mustClear ) {
			for ( int i = 0; i < nOutChan; i++ ) {
				Arrays.fill(outSamples[i], 0f);
			}
		}
		for ( int i = 0; i < nInChan; i++ ) {
			float[] from = buffer.getChannel(i);
			float[] to = inSamples[i];
			System.arraycopy(from, 0, to, 0, nsamples);
		}
		vstfx.processReplacing(inSamples, outSamples, nsamples);
		for ( int i = 0; i < nOutChan; i++ ) {
			float[] from = outSamples[i];
			float[] to = buffer.getChannel(i);
			System.arraycopy(from, 0, to, 0, nsamples);
		}
		return AUDIO_OK;
	}

	public void close() throws Exception {
		System.out.print("Closing audio: "+controls.getName()+" ... ");
		vstfx.turnOffAndUnloadPlugin();
		System.out.println("closed");
		support.removeTempoListener(tempoListener);
	}
}
