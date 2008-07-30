package uk.org.toot.synth.filter;

public interface FilterVariables 
{
	void setSampleRate(int rate);
	float getFrequency();		// filter dependent meaning
	float getResonance();		// filter dependent meaning
	float getEvelopeDepth();	// -1..1
	float getVelocityTrack();   //  0..5?
	float getKeyTrack();
}