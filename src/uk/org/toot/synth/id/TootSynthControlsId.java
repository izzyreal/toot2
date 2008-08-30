// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.id;

public interface TootSynthControlsId
{
	// ids of the plugin synths
	final static int EXAMPLE_1_SYNTH_ID = 1;
	final static int EXAMPLE_2_SYNTH_ID = 2;
	final static int EXAMPLE_3_SYNTH_ID = 3;
	
	// ids of the synth module categories
    // allow for 16 in each category
    // THESE CONSTANTS MUST NEVER BE CHANGED !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	final static int OSCILLATOR_BASE_ID = 0x00; // 32 oscillators
    final static int FILTER_BASE_ID = 0x20;		// 32 filters
    final static int ENVELOPE_BASE_ID = 0x40;	// 32 envelopes
    final static int AMPLIFIER_BASE_ID = 0x60;
    final static int MIXER_BASE_ID = 0x68;
}
