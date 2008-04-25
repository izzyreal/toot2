package uk.org.toot.synth.oscillator;

public interface Oscillator 
{
	void setSampleRate(int rate);
	float getSample(float fm, float pm);
}
