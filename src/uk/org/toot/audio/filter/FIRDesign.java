// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.filter;

public class FIRDesign extends AbstractFilterDesign
{
    private int order = -1; // estimated by design()
    private float transitionBandwidth;
    private float attenuation = -60.0f;
    private float alpha;
    private float[] a;

    public FIRDesign(FilterSpecification spec) {
        super(spec);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public float getTransitionBandwidth() {
        return transitionBandwidth;
    }

    public void setTransitionBandwidth(float transitionBandwidth) {
        this.transitionBandwidth = transitionBandwidth;
    }

    public float getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(float attenuation) {
        this.attenuation = attenuation;
    }

    public void design(int sampleRate) {
        float fNyquist = sampleRate / 2f;
        float frequency = spec.getFrequency();
        if (order < 1)
            order = estimatedOrder(fNyquist);
        // estimate Kaiser window parameter (alpha):
        if (getAttenuation() >= 50.0f)
            alpha = 0.1102f * (getAttenuation() - 8.7f);
        else if (getAttenuation() > 21.0f)
            alpha = 0.5842f * (float)Math.exp(0.4f *
            	(float)Math.log(getAttenuation() - 21.0f)) + 0.07886f *
            	(getAttenuation() - 21.0f);
        if (getAttenuation() <= 21.0f)
            alpha = 0.0f;
        // window function values
        float I0alpha = I0(alpha);
        int m = order / 2;
        float[] win = new float[m + 1];
        for (int n = 1; n <= m; n++)
            win[n] = I0(alpha * (float)Math.sqrt(1.0f - sqr((float)n / m))) / I0alpha;
        float ft = getTransitionBandwidth();
        float w0 = 0.0f;
        float w1 = 0.0f;
        switch ( spec.getClassicType() ) {
            case LPF:
                w0 = 0.0f;
                w1 = (float)Math.PI * (frequency + 0.5f * ft) / fNyquist;
                break;
            case HPF:
                w0 = (float)Math.PI;
                w1 = (float)Math.PI * (1.0f - (frequency - 0.5f * ft) / fNyquist);
                break;
            case BPF:
//                w0 = 0.5f * (float)Math.PI * (fl + fh) / fNyquist;
                w0 = (float)Math.PI * frequency / fNyquist;
            	float fb = frequency / 4; // !!!
                w1 = 0.5f * (float)Math.PI * (fb + ft) / fNyquist;
                break;
        }
        // filter coefficients (NB not normalised to unit maximum gain)
        a = new float[order + 1];
        a[0] = w1 / (float)Math.PI;
        for (int n = 1; n <= m; n++)
            a[n] = (float)Math.sin(n * w1) * (float)Math.cos(n * w0) * win[n] / (n * (float)Math.PI);
        // shift impulse response to make filter causal
        for (int n = m + 1; n <= order; n++) a[n] = a[n - m];
        for (int n = 0; n <= m - 1; n++) a[n] = a[order - n];
        a[m] = w1 / (float)Math.PI;
    }

    protected int estimatedOrder(float fNyquist) {
        // estimate filter order
        int o = 2 * (int)((getAttenuation() - 7.95) / (14.36 * getTransitionBandwidth() / fNyquist) + 1.0f);
        //System.out.println("KF: order="+o+" fN="+fN) ;
        return o;
    }

    /**
     * Calculate the zero order Bessel function of the first kind
     */
    protected float I0(float x) {
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

    protected float sqr(float x) { return x * x; }

    public float[] getCoefficients() {
        return a;
    }
}


