package uk.org.toot.synth.oscillator;

public interface Oscillator 
{
	void setSampleRate(int rate);
	void update(); // called once per buffer
	float getSample(float vib, OscillatorControl control, boolean release);
}
