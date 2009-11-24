package uk.org.toot.synth.modules.filter;

public abstract class AbstractFilter
{
	protected FilterVariables vars;
	protected float fs = 44100;
	
	public AbstractFilter(FilterVariables filterVariables) {
		vars = filterVariables;
	}
	
	public void setSampleRate(int rate) {
		vars.setSampleRate(rate);
		fs = rate;
	}
}
