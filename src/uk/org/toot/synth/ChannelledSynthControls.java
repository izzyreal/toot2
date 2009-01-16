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
		// TODO set instance index
		globalControls = controls;
		add(controls);
	}
	
	protected void setChannelControls(int chan, CompoundControl controls) {
		// TODO set instance index
		channelControls[chan] = controls;
		add(controls);
	}
}
