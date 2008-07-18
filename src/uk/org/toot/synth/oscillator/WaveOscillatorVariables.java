package uk.org.toot.synth.oscillator;

public interface WaveOscillatorVariables 
{
	boolean isMaster();
	Wave getWave();
	float getLevel();
	float getDetuneFactor();
	float getEnvelopeDepth();
	float getSyncThreshold();
}
