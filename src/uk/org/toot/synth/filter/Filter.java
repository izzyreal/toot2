package uk.org.toot.synth.filter;

public interface Filter 
{
	float filter(float sample, float freq, float res);
}
