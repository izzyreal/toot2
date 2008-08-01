package uk.org.toot.synth.oscillator;

public interface MultiWaveOscillatorVariables extends OscillatorVariables
{
	MultiWave getMultiWave();
	int getOctave();
	float getWidth();			// 0..1
	float getWidthLFODepth(); 	// 0..1 modulates width
}
