package uk.org.toot.synth.filter;

import uk.org.toot.synth.envelope.EnvelopeGenerator;

public abstract class AbstractFilter implements Filter
{
	protected FilterVariables vars;
	protected EnvelopeGenerator cutoffEnv;
	protected float freq; 		// signal fundamental, Hz
	protected float amp;		// signal amplitude, 0..1
	protected int fs;			// sample rate, 0..1
	protected float fc;			// nominal filter freq 0..1
	protected float envDepth; 	// normalised envelope depth
	
	public AbstractFilter(FilterVariables filterVariables, EnvelopeGenerator eg, float freq, float amp) {
		vars = filterVariables;
		cutoffEnv = eg;
		this.freq = freq;
		this.amp = amp;
	}
	
	public void setSampleRate(int rate) {
		fs = rate;
		float f = freq * 2 / fs;
		f *= vars.getKeyTrack();
		fc = f + vars.getFrequency();
		fc *= 1 - vars.getVelocityTrack() * (1f - amp); // !!! TODO
		if ( fc >= 1 ) fc = 1f;
		float fED = vars.getEvelopeDepth();
		float fERange = fED < 0 ? fc - 0.0025f : 1 - fc;
		// normalise the filter env depth to ensure 0 < fc < 1
		envDepth = fED * fERange * amp;		
	}
}
