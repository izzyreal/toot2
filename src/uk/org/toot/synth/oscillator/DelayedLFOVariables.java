package uk.org.toot.synth.oscillator;

public interface DelayedLFOVariables extends LFOVariables 
{
	float getDelay();		// seconds
	float getAttack();	// seconds
	float getLevel();	// 0..1
}
