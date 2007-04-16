// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

public interface ModulatedDelayVariables extends DelayVariables
{
    float getDelayMilliseconds(); // !!! should be samples for efficiency
    float getPhaseRadians();
    float getRate();
    float getDepth();
    float getFilterFrequency();
    float getFeedback();
    float getMix();
    boolean isTape();
    int getLFOShape();
    int getFilterType();

    boolean isWetInverted();
}
