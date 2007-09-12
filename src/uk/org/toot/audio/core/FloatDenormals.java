// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.core;

/**
 * Provides ways to deal with FPU denormals, which, if left
 * in audio data tend to cause exceptionally high CPU uage.
 * Floating point denormals typically occur when a feedback
 * network causes an exponential decay. Eventually the value
 * becomes so small it cannot be represented efficiently by the
 * FPU.
 */
public class FloatDenormals
{
    public static final float THRESHOLD = 1e-15f;

    /**
     * Detect a denormal float (excluding zero).
     */
    public static boolean isDenormal(float x) {
        return x != 0f && isDenormalOrZero(x);
    }

    /**
     * Detect a denormal (or zero) float.
     * Faster than isDenormal() if appropriate.
     */
    public static boolean isDenormalOrZero(float x) {
        return Math.abs(x) < THRESHOLD;
    }

    /**
     * Replace a denormal float with zero.
     */
    public static float zeroDenorm(float x) {
        // isDenormalOrZero is slightly more efficient in this use.
        // the test is quicker but the result is the same.
        return isDenormalOrZero(x) ? 0f : x;
    }

    /**
     * Replace denormal floats in an array with zeros.
     */
    public static void zeroDenorms(float[] array, int len) {
        for ( int i = 0; i < len; i++ ) {
            array[i] = zeroDenorm(array[i]);
        }
    }

    /**
     * Count denormal floats in an array.
     */
    public static int countDenorms(float[] array, int len) {
        int count = 0;
        for ( int i = 0; i < len; i++ ) {
            if ( isDenormal(array[i]) ) count++;
        }
        return count;
    }
}
