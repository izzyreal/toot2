package uk.org.toot.synth;

import uk.org.toot.control.CompoundControl;

public class SynthControls extends CompoundControl
{
	protected SynthControls(int id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	protected SynthControls(int id, int instanceIndex, String name) {
		super(id, instanceIndex, name);
		// TODO Auto-generated constructor stub
	}

    // return a domain specific string for preset organisation
	// to avoid id collisions from different domains
    // i.e. audio, synth
    public String getPersistenceDomain() {
    	return "synth";
    }
}
