package uk.org.toot.synth.modules.filter;

public interface Filter 
{
	void setSampleRate(int rate);
	void update(float freq); // called once per buffer, prior to the getSamples(), freq 0..1
	float filter(float sample, float fmod); // fmod -/+
}
