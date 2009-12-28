package uk.org.toot.audio.delay;

public interface PhaserVariables
{
	boolean isBypassed();
	float getRate();		// Hz
	float getDepth();		// 0..1
	float getFeedback();	// 0..1
}
