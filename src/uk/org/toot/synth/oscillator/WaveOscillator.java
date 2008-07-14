package uk.org.toot.synth.oscillator;

import uk.org.toot.synth.SynthChannel;

public class WaveOscillator implements Oscillator
{
	private WaveOscillatorVariables vars;
	private float index = 0f;
	private float increment = 1f;
	private boolean sync = true;
	private float syncIndex = 0f;
	private float[] wave;
	private int waveSize;
	private SynthChannel channel;
	private float bendFactor = 1f;
	private float syncEnvDepth = 2;
	private float k;
	
	public WaveOscillator(SynthChannel channel, WaveOscillatorVariables oscillatorVariables, int pitch) {
		this.channel = channel;
		vars = oscillatorVariables;
		Wave wave = vars.getWave();
		this.wave = wave.getData();
		waveSize = this.wave.length - 1;
		k = wave.getPeriod() * midiFreq(pitch);
	}
	
	// TODO move elsewhere
	protected float midiFreq(int pitch) { 
		return (float)(440.0 * Math.pow( 2.0, ((double)pitch - 69.0) / 12.0 )); 
	}
	 
	public void setSampleRate(int sampleRate) {
		increment = k / sampleRate;
	}
	
	public void update() {
		bendFactor = channel.getBendFactor();
		syncEnvDepth = vars.getEnvelopeDepth();
		sync = syncEnvDepth > 0.1f;
	}
	
	public float getSample(float vibratoFactor, float syncEnv) {
		// lookup with linear interpolation
		int ix = (int)index;
		float frac = index - ix;
		float sample = (1f - frac) * wave[ix] + frac * wave[ix+1];
		// lookup sync with linear interpolation
		if ( sync ) {
			ix = (int)syncIndex;
			frac = syncIndex - ix;
			sample += (1f - frac) * wave[ix] + frac * wave[ix+1];
		}
		// prepare next lookup index
		float factor = bendFactor * vibratoFactor;
		index += increment * factor;
		if ( index >= waveSize ) {
			index -= waveSize;
			syncIndex = 0f;
		} else if ( sync ) {
			syncIndex += increment * (2 + (syncEnvDepth * syncEnv)) * factor;
			if ( syncIndex >= waveSize ) {
				syncIndex -= waveSize;
			}
		}

		return sample ;
	}
}
