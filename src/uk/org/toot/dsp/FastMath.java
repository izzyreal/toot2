package uk.org.toot.dsp;

import static java.lang.Math.PI;

public class FastMath 
{
    // http://www.devmaster.net/forums/showthread.php?t=5784
    private static final float S_B = (float)(4 / PI);
    private static final float S_C = (float)(-4 / (PI*PI));
    // -PI < x < PI
    public static float sin(float x) {
        return S_B * x + S_C * x * abs(x);
    }

    private static final float TWODIVPI = (float)(2 / PI);
    // -PI < x < PI
    // thanks scoofy[AT]inf[DOT]elte[DOT]hu
    // for musicdsp.org pseudo-code improvement
    public static float triangle(float x) {
        x += PI;			// 0 < x < 2*PI
        x *= TWODIVPI;   	// 0 < x < 4
        x -= 1;				// -1 < x < 3
        if ( x > 1 ) x -= 4f;
        return abs(-(abs(x)-2)) - 1;
    }
    	
	// provide a faster abs, no NaN handling
    public static float abs(float x) {
    	return x < 0 ? -x : x;
    }
    
	// provide a faster min, no NaN handling
	public static float min(float a, float b) {
		return a < b ? a : b;
	}

	// provide a faster max, no NaN handling
	public static float max(float a, float b) {
		return a > b ? a : b;
	}
}
