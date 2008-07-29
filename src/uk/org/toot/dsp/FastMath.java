package uk.org.toot.dsp;

public class FastMath 
{
    // http://www.devmaster.net/forums/showthread.php?t=5784
    private static final float S_B = (float)(4 /  Math.PI);
    private static final float S_C = (float)(-4 / (Math.PI*Math.PI));
    // -PI < x < PI
    public static float sin(float x) {
        return S_B * x + S_C * x * Math.abs(x);
    }

    // -PI < x < PI
    // thanks scoofy[AT]inf[DOT]elte[DOT]hu
    // for musicdsp.org pseudo-code improvement
    public static float triangle(float x) {
        x += Math.PI;		// 0 < x < 2*PI
        x /= Math.PI / 2;   // 0 < x < 4
        x -= 1;				// -1 < x < 3
        if ( x > 1 ) x -= 4f;
        return Math.abs(-(Math.abs(x)-2)) - 1;
    }
}
