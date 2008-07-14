package uk.org.toot.synth.oscillator;

import uk.org.toot.synth.SynthChannel;

public class WaveOscillator implements Oscillator
{
	private WaveOscillatorVariables vars;
	private float index = 0f;
	private float increment = 1f;
	private boolean sub; 		// sub-oscillator using same wave
	private boolean sync;		// hard sync of sub-oscillator
	private float subIndex = 0f;
	private float subLevel = 1f;
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
		sync = syncEnvDepth > 0.01f;
		subLevel = vars.getSubLevel();
		sub = subLevel > 0.01f;
	}
	
	public float getSample(float vibratoFactor, float syncEnv) {
		// lookup with linear interpolation
		int ix = (int)index;
		float frac = index - ix;
		float sample = (1f - frac) * wave[ix] + frac * wave[ix+1];
		// lookup sync with linear interpolation
		if ( sub ) {
			ix = (int)subIndex;
			frac = subIndex - ix;
			sample += subLevel * ((1f - frac) * wave[ix] + frac * wave[ix+1]);
		}
		// prepare next lookup index
		float factor = bendFactor * vibratoFactor;
		index += increment * factor;
		if ( index >= waveSize ) {
			index -= waveSize;
			if ( sync ) subIndex = 0f; // hard sync
		} else if ( sub ) {
			subIndex += increment * factor * (sync ? (2 + (syncEnvDepth * syncEnv)) : 1.003f);
			if ( subIndex >= waveSize ) {
				subIndex -= waveSize;
			}
		}

		return sample ;
	}
}
