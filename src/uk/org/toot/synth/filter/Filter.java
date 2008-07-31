package uk.org.toot.synth.filter;

public interface Filter 
{
	void setSampleRate(int rate);
	void update(); // called once per buffer, prior to the getSamples();
	float filter(float sample, float freq, float res);
	float filter(float sample, boolean release);
}
