// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.reverb;

/**
 * @author st
 *
 */
public interface PlateVariables
{
	boolean isBypassed();
	int getPreDelaySamples();
	float getBandwidth();		// 0..1
	float getInputDiffusion1();	// 0..1
	float getInputDiffusion2();	// 0..1
	float getDecayDiffusion1();	// 0..1
	float getDecayDiffusion2();	// 0..1
	float getDamping();			// 0..1
	float getDecay();			// 0..1
}
