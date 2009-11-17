package uk.org.toot.synth.modules.filter;

public abstract class AbstractFilter implements Filter
{
	protected FilterVariables vars;
	protected float fs = 44100;
	
	public AbstractFilter(FilterVariables filterVariables) {
		vars = filterVariables;
	}
	
	public void setSampleRate(int rate) {
		vars.setSampleRate(rate);
		fs = rate;
/*		float f = freq * 2 / fs;
		f *= vars.getKeyTrack();
		fc = f + vars.getFrequency();
		fc *= 1 - vars.getVelocityTrack() * (1f - amp); // !!! TODO
		if ( fc >= 1 ) fc = 1f;
		float fED = vars.getEvelopeDepth();
		float fMin = 2f * 20f / rate;
		float fERange = fED < 0 ? fc - fMin : 1 - fc;
		// normalise the filter env depth to ensure 0 < fc < 1
		envDepth = fED * fERange * amp; */		
	}
}
