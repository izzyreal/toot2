// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.modules.filter;

public class StateVariableFilterElement
{
	private float prev = 0f;
	private float low, high, band, notch;

	boolean bp = false;
	float mix = 0f;
	
	public float filter(float in, float freq, float damp) {
		float i1 = (prev + in) * 0.5f; // linearly interpolated double sampled
		prev = in;
		notch = i1 - damp * band;
		low   = low + freq * band;								
		high  = notch - low;									
		band  = freq * high + band; // - drive*band*band*band;	
		notch = in - damp * band;
		low   = low + freq * band;								
		high  = notch - low;									
		band  = freq * high + band; // - drive*band*band*band;	
		return bp ? band : (1f-mix)*low + mix*high;					
	}

}
