package uk.org.toot.synth.modules.oscillator;

/**
 * Avoids passing lots of stuff into constructors
 * @author st
 *
 */
public class LFOConfig 
{
	public float rateMin = 0.01f;
	public float rateMax = 1f;
	public float rate = 0.1f;
	public float deviationMax = 0f;
	public float deviation = 0f;
	public boolean hasLevel = false;
	public float delayMin = 0.1f;
	public float delayMax = 10f;
	public float delay = 0.5f;
	public float attackMin = 0.1f;
	public float attackMax = 10f;
	public float attack = 1;
}
