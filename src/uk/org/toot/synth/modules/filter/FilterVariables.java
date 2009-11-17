package uk.org.toot.synth.modules.filter;

public interface FilterVariables 
{
	void setSampleRate(int rate);
	float getFrequency();		// filter dependent meaning
	float getResonance();		// filter dependent meaning
}