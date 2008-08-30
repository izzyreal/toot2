package uk.org.toot.synth.modules.envelope;

public interface EnvelopeVariables 
{
	void setSampleRate(int rate);
	int getDelayCount();		// samples
	float getAttackCoeff();		// 0+..1
	int getHoldCount();			// samples
	float getDecayCoeff();		// 0+..1
	float getSustainLevel();	// 0..1
	float getReleaseCoeff();	// 0+..1
}
