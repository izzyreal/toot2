package uk.org.toot.synth.modules.filter;

public class MoogFilter2 extends AbstractFilter
{
	private MoogFilterElement element;
	private float fc;
	private float res;

	public MoogFilter2(FilterVariables variables) {
		super(variables);
		element = new MoogFilterElement();
	}
	
	public void update(float freq) {
		fc = vars.getFrequency() + freq * 2 / fs;
		res  = vars.getResonance();
	}

	public float filter(float sample, float fmod) {
		float f = fc + fmod;
		if ( f > 1 ) f = 1;
		if ( f < 0.004f ) f = 0.004f; // !!! 44.1k gives 80Hz approx
		return element.filter(sample, f, res);
	}
}
