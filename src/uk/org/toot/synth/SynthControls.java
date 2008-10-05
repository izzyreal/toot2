package uk.org.toot.synth;

import uk.org.toot.control.CompoundControl;

/**
 * An ordered list of 1 global and 16 channel controls.
 * Global controls have instanceIndex 0
 * Channel controls have instanceIndex 1..16
 * @author st
 *
 */
public class SynthControls extends CompoundControl
{
	private CompoundControl globalControls;
	private CompoundControl[] channelControls = new CompoundControl[16];
	
	public SynthControls(int id, String name) {
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
