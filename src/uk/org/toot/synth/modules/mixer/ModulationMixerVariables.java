package uk.org.toot.synth.modules.mixer;

public interface ModulationMixerVariables 
{
	int getCount();
	float getDepth(int n); 	// -1 .. 1
	float[] getDepths();	// -1 .. 1
}
