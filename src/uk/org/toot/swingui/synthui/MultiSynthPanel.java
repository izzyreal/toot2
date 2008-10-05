package uk.org.toot.swingui.synthui;

import java.util.Vector;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.service.ServiceDescriptor;
import uk.org.toot.service.ServiceVisitor;
import uk.org.toot.synth.SynthChannelServices;
import uk.org.toot.synth.synths.multi.MultiSynthControls;

public class MultiSynthPanel extends MultiControlPanel
{
	private static Vector<String> selectionNames = new Vector<String>();

	static {
		selectionNames.add(NONE);
		SynthChannelServices.accept( // subclass
			new ServiceVisitor() {
				public void visitDescriptor(ServiceDescriptor d) {
					selectionNames.add(d.getName());
				}
			}, CompoundControl.class
		);
	}

	public MultiSynthPanel(MultiSynthControls controls) {
		super(controls, 16, "Channels");
	}
	
	protected Vector<String> getSelectionNames() {
		return selectionNames;
	}
	
	private MultiSynthControls getControls() {
		return (MultiSynthControls)multiControls;
	}
	
	protected String getAnnotation(int chan) { // subclass
		return String.valueOf(1+chan);
	}

	protected void setControls(int chan, CompoundControl controls) { // subclass
		try {
			getControls().setChannelControls(chan, controls);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	protected CompoundControl getControls(int chan) { // subclass
		return getControls().getChannelControls(chan);
	}
	
	protected CompoundControl createControls(String name) { // subclass
		return SynthChannelServices.createControls(name);
	}
	
}
