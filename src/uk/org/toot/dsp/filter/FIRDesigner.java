// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.dsp.filter;

/**
 * @author st
 *
 */
public class FIRDesigner
{
	public static float[] designLowPass(float frequency, float ft, float dBattenuation) {
		return design(FilterShape.LPF, frequency, ft, dBattenuation, -1);
	}
	
    /**
     * @param frequency normalised to Nyquist
     * @param ft normalised to Nyquist
     * @param dBattenuation attenuation in dB
     */
    public static float[] design(FilterShape shape, float frequency, float ft, float dBattenuation, int order) {
        float alpha = 0;
        if (order < 1)
            order = estimatedOrder(dBattenuation, ft);
        // estimate Kaiser window parameter (alpha):
        if (dBattenuation >= 50.0f)
            alpha = 0.1102f * (dBattenuation - 8.7f);
        else if (dBattenuation > 21.0f)
            alpha = 0.5842f * (float)Math.exp(0.4f *
            	(float)Math.log(dBattenuation - 21.0f)) + 0.07886f *
            	(dBattenuation - 21.0f);
        if (dBattenuation <= 21.0f)
            alpha = 0.0f;
        // window function values
        float I0alpha = I0(alpha);
        int m = order / 2;
        float[] win = new float[m + 1];
        for (int n = 1; n <= m; n++)
            win[n] = I0(alpha * (float)Math.sqrt(1.0f - sqr((float)n / m))) / I0alpha;
        float w0 = 0.0f;
        float w1 = 0.0f;
        switch ( shape ) {
            case LPF:
                w0 = 0.0f;
                w1 = (float)Math.PI * (frequency + 0.5f * ft);
                break;
            case HPF:
                w0 = (float)Math.PI;
                w1 = (float)Math.PI * (1.0f - (frequency - 0.5f * ft));
                break;
            case BPF:
//                w0 = 0.5f * (float)Math.PI * (fl + fh) / fNyquist;
                w0 = (float)Math.PI * frequency;
            	float fb = frequency / 4; // !!!
                w1 = 0.5f * (float)Math.PI * (fb + ft);
                break;
        }
        // filter coefficients (NB not normalised to unit maximum gain)
        float[] a = new float[order + 1];
        a[0] = w1 / (float)Math.PI;
        for (int n = 1; n <= m; n++)
            a[n] = (float)Math.sin(n * w1) * (float)Math.cos(n * w0) * win[n] / (n * (float)Math.PI);
        // shift impulse response to make filter causal
        for (int n = m + 1; n <= order; n++) a[n] = a[n - m];
        for (int n = 0; n <= m - 1; n++) a[n] = a[order - n];
        a[m] = w1 / (float)Math.PI;
        return a;
    }

    protected static int estimatedOrder(float dBattenuation, float ftransition) {
        // estimate filter order
        int o = 2 * (int)((dBattenuation - 7.95) / (14.36 * ftransition) + 1.0f);
        return o;
    }

    /**
     * Calculate the zero order Bessel function of the first kind
     */
    protected static float I0(float x) {
        float eps = 1.0e-6f; // accuracy parameter
        float fact = 1.0f;
        float x2 = 0.5f * x;
        float p = x2;
        float t = p * p;
        float s = 1.0f + t;
        for (int k = 2; t > eps; k++) {
            p *= x2;
            fact *= k;
            t = sqr(p / fact);
            s += t;
        }
        return s;
    }

    protected static float sqr(float x) { return x * x; }
}
