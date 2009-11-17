package uk.org.toot.synth.modules.filter;

/**
 * A simple single pole low pass filter with no changing parameters.
 * @author st
 *
 */
public class LP1pFilter
{
	private float y1 = 0f;
	private float g;
	
	public LP1pFilter(float freq, int rate) {
		g = 1f - (float)Math.exp(-2.0*Math.PI*freq/rate);
	}
	
	public float filter(float sample) {
		y1 += g*(sample - y1);
	    return y1;	
	}

}
