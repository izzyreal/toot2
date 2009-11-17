package uk.org.toot.synth.modules.envelope;

public interface ASREnvelopeVariables 
{
	void setSampleRate(int rate);
	float getAttackCoeff();		// 0+..1
	boolean getSustain();
	float getReleaseCoeff();	// 0+..1
}
