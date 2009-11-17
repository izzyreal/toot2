package uk.org.toot.synth.modules.filter;

public interface FilterElement
{
	public float filter(float sample, float fc, float res);
}
