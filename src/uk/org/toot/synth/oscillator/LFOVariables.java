package uk.org.toot.synth.oscillator;

public interface LFOVariables 
{
	float getFrequency();
	float getDeviation();
	boolean isSine(); // otherwise Triangle
}
