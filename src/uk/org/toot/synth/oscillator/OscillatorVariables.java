package uk.org.toot.synth.oscillator;

public interface OscillatorVariables 
{
	boolean isMaster();
	float getLevel();
	float getDetuneFactor();
	float getEnvelopeDepth();
	float getSyncThreshold();
}
