package uk.org.toot.synth.filter;

public interface FilterVariables 
{
	void setSampleRate(int rate);
	float getFrequency(); 		//  0..1
	float getResonance(); 		//  0..1
	float getEvelopeDepth();	// -1..1
	float getVelocityTrack();   //  0..5?
}