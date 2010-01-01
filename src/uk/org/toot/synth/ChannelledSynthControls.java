// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth;

import uk.org.toot.control.CompoundControl;

public class ChannelledSynthControls extends SynthControls
{
	private CompoundControl globalControls;
	private CompoundControl[] channelControls = new CompoundControl[16];
	
	public ChannelledSynthControls(int id, String name) {
		super(id, name);
	}
	
	public CompoundControl getGlobalControls() {
		return globalControls;
	}
	
	public CompoundControl getChannelControls(int chan) {
		return channelControls[chan];
	}

	protected void setGlobalControls(CompoundControl controls) {
		globalControls = controls;
		add(controls);
	}
	
	protected void setChannelControls(int chan, CompoundControl controls) {
		channelControls[chan] = controls;
		add(controls);
	}
}
