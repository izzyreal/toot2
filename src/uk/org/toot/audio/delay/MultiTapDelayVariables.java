// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import java.util.List;

/**
 * 
 */
public interface MultiTapDelayVariables extends DelayVariables
{

    /**
     * Provide a list of delay taps.
     * Parameterisation by channel index ALLOWS per channel delay taps but
     * also ALLOWS a single list of taps to be used for all channels.
     * Allocation of taps to channels is the responsibility of the
     * implementation so other allocations are also possible.
     */
    List<DelayTap> getTaps(int chan);

    float getFeedback();

    float getMix();

    int getChannelCount();

    float getDelayFactor();
}
