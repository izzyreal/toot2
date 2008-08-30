package uk.org.toot.synth.modules.oscillator;

public interface LFOVariables 
{
	float getFrequency();
	float getDeviation();
	boolean isSine(); // otherwise Triangle
}
