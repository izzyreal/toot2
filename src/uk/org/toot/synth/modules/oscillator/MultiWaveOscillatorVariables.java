package uk.org.toot.synth.modules.oscillator;

public interface MultiWaveOscillatorVariables extends OscillatorVariables
{
	MultiWave getMultiWave();
	int getOctave();
	float getWidth();			// 0..1
}
