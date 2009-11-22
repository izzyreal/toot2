package uk.org.toot.synth.modules.filter;

public interface FilterVariables 
{
	void setSampleRate(int rate);
	float getCutoff();			// semitones relative to signal fundamental
	float getResonance();		// filter dependent meaning, typically 0 .. 1
}