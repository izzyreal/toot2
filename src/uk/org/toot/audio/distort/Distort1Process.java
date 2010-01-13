package uk.org.toot.audio.distort;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.SimpleAudioProcess;

public class Distort1Process extends SimpleAudioProcess
{
	private Distort1Variables vars;
	
	public Distort1Process(Distort1Variables vars) {
		this.vars = vars;
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
        float[] samples;
        for ( int c = 0; c < nchans; c++ ) {
        	samples = buffer.getChannel(c);
        	for ( int s = 0; s < nsamples; s++ ) {
        		samples[s] = inverseGain * distort(samples[s] * gain);
        	}
        }
		return AUDIO_OK;
	}
	
	public float distort(float x) {
		return 1.5f * x - 0.5f * x * x * x;
	}

}
