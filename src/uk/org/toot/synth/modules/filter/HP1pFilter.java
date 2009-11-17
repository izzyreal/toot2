package uk.org.toot.synth.modules.filter;

public class HP1pFilter extends LP1pFilter
{
	public HP1pFilter(float freq, int rate) {
		super(freq, rate);
	}
	
	public float filter(float sample) {
		return sample - super.filter(sample);
	}

}
