// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.filter;

import uk.org.toot.dsp.filter.FilterShape;

/* Simple implementation of Biquad filters -- Tom St Denis
 *
 * Based on the work

Cookbook formulae for audio EQ biquad filter coefficients
---------------------------------------------------------
by Robert Bristow-Johnson

 * Enjoy.
 *
 * Tom St Denis -- http://tomstdenis.home.dhs.org
 */

public class BiQuadDesign extends AbstractFilterDesign 
{
    private static double M_LN2 = 0.69314718055994530942;
    double a0;double a1;double a2;double a3;double a4; // coefficients (faster than array)

    public BiQuadDesign(FilterSpecification spec) {
        super(spec);
    }

    public void design(int sampleRate) {
        // we design for 0dB gain
        design(spec.getShape(), 0f, spec.getFrequency(), (float)sampleRate, FilterTools.getOctaveBandwidth(spec.getResonance()));
    }

    // TODO move to dsp.filter.BiquadDesigner
    public void design(FilterShape type, float dbGain, float freq, float srate, float bandwidth) {
//        System.out.println("design t="+type+", f="+freq+", sr="+srate+", obw="+bandwidth);
        double A, omega, sn, cs, alpha, beta;
        double a0, a1, a2, b0, b1, b2;

        A = Math.pow(10, dbGain / 40);
        omega = 2 * Math.PI * freq / srate;
        sn = Math.sin(omega);
        cs = Math.cos(omega);
        alpha = sn * Math.sinh(M_LN2 / 2 * bandwidth * omega / sn);
        beta = Math.sqrt(A + A);
        switch (type) {
            case LPF:
                b0 = (1 - cs) / 2;
                b1 = 1 - cs;
                b2 = (1 - cs) / 2;
                a0 = 1 + alpha;
                a1 = -2 * cs;
                a2 = 1 - alpha;
                break;
            case HPF:
                b0 = (1 + cs) / 2;
                b1 = -(1 + cs);
                b2 = (1 + cs) / 2;
                a0 = 1 + alpha;
                a1 = -2 * cs;
                a2 = 1 - alpha;
                break;
            case BPF:
                b0 = alpha;
                b1 = 0;
                b2 = -alpha;
                a0 = 1 + alpha;
                a1 = -2 * cs;
                a2 = 1 - alpha;
                break;
            case NOTCH:
                b0 = 1;
                b1 = -2 * cs;
                b2 = 1;
                a0 = 1 + alpha;
                a1 = -2 * cs;
                a2 = 1 - alpha;
                break;
            case PEQ:
                b0 = 1 + (alpha * A);
                b1 = -2 * cs;
                b2 = 1 - (alpha * A);
                a0 = 1 + (alpha / A);
                a1 = -2 * cs;
                a2 = 1 - (alpha / A);
                break;
            case LSH:
                b0 = A * ((A + 1) - (A - 1) * cs + beta * sn);
                b1 = 2 * A * ((A - 1) - (A + 1) * cs);
                b2 = A * ((A + 1) - (A - 1) * cs - beta * sn);
                a0 = (A + 1) + (A - 1) * cs + beta * sn;
                a1 = -2 * ((A - 1) + (A + 1) * cs);
                a2 = (A + 1) + (A - 1) * cs - beta * sn;
                break;
            case HSH:
                b0 = A * ((A + 1) + (A - 1) * cs + beta * sn);
                b1 = -2 * A * ((A - 1) + (A + 1) * cs);
                b2 = A * ((A + 1) + (A - 1) * cs - beta * sn);
                a0 = (A + 1) - (A - 1) * cs + beta * sn;
                a1 = 2 * ((A - 1) - (A + 1) * cs);
                a2 = (A + 1) - (A - 1) * cs - beta * sn;
                break;
            default:
                return;
        }

	    /* precompute the coefficients */
        this.a0 = b0 / a0;
        this.a1 = b1 / a0;
        this.a2 = b2 / a0;
        this.a3 = a1 / a0;
        this.a4 = a2 / a0;
    }
}
