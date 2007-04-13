// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.core;

public class ClipState
{
    private boolean clipping;

    /**
     * @link aggregationByValue
     * @supplierCardinality 1
     * @label ON 
     */
	public static final ClipState ON = new ClipState(true);

    /**
     * @link aggregationByValue
     * @supplierCardinality 1
     * @label OFF 
     */
    public static final ClipState OFF = new ClipState(false);

    protected ClipState(boolean clipping) {
        this.clipping = clipping;
    }

    public boolean isClipping() { return clipping; }
}


