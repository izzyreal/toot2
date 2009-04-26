// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

public interface TempoDelayVariables extends DelayVariables
{
	float getDelayFactor(); // quarter note = 1, half note = 2 etc.
	
    float getFeedback();

    float getMix();
}