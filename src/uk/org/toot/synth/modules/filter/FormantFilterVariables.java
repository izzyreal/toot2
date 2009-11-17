package uk.org.toot.synth.modules.filter;

public interface FormantFilterVariables
{
	public int size();
	public float getFrequency(int n);	// 0..1
	public float getLevel(int n);		// 0..1
	public float getFreqencyShift();	// 0.25..4
	public float getResonance();		// 0..1
	public void setSampleRate(int rate);
}
