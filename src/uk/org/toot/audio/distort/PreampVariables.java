// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.distort;

/**
 * @author st
 *
 */
public interface PreampVariables extends Distort1Variables
{
	float getGain2();
	float getBias2();
	float getMaster();
}
