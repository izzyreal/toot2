package uk.org.toot.synth.modules.filter;

public interface Filter 
{
	void setSampleRate(int rate);
	// returns normalised static filter frequency
	float update(); // called once per buffer, prior to the getSamples()
	float filter(float sample, float f);
}
