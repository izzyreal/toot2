// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.control;

/**
 * A linear control law.
 */
public class LinearLaw extends AbstractLaw
{
    public LinearLaw(float min, float max, String units) {
        super(min, max, units);
    }

    public int intValue(float v) {
        return (int)((resolution-1) * (v - min) / (max - min));
    }

    public float userValue(int v) {
        return min + (max - min) * ( (float)v / (resolution-1) );
    }
}
