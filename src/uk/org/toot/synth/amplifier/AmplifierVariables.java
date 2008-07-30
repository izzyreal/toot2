package uk.org.toot.synth.amplifier;

public interface AmplifierVariables 
{
	void setSampleRate(int rate);
	float getVelocityTrack();   //  0..5?
	float getLevel();			// >0..1
}