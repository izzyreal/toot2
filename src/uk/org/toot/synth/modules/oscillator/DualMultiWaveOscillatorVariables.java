package uk.org.toot.synth.modules.oscillator;

public interface DualMultiWaveOscillatorVariables
{
	float getSawLevel();
	float getSquareLevel();
//	int getOctave();
	float getWidth();			// 0 .. 1
	float getTuningFactor();	// 0.25 .. 4 
	boolean isMaster();
}
