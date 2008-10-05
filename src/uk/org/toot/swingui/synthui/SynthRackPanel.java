package uk.org.toot.swingui.synthui;

import java.util.Vector;

import javax.swing.JPanel;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.service.ServiceDescriptor;
import uk.org.toot.service.ServiceVisitor;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.SynthServices;
import uk.org.toot.synth.SynthRackControls;
import uk.org.toot.synth.synths.multi.MultiSynthControls;

public class SynthRackPanel extends MultiControlPanel 
{
	private static Vector<String> selectionNames = new Vector<String>();

	static {
		selectionNames.add(NONE);
		try {
			SynthServices.accept( // subclass
				new ServiceVisitor() {
					public void visitDescriptor(ServiceDescriptor d) {
						selectionNames.add(d.getName());
					}
				}, SynthControls.class
			);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	public SynthRackPanel(SynthRackControls controls) {
		super(controls, controls.size(), "Synths");
	}

	protected Vector<String> getSelectionNames() {
		return selectionNames;
	}
	
	@Override
	protected CompoundControl createControls(String name) {
		return SynthServices.createControls(name);
	}
	
	@Override
	protected String getAnnotation(int synth) {
		return String.valueOf((char)('A'+synth));
	}
	
	private SynthRackControls getControls() {
		return (SynthRackControls)multiControls;
	}
	
	@Override
	protected SynthControls getControls(int synth) {
		return getControls().getSynthControls(synth);
	}
	
	@Override
	protected void setControls(int synth, CompoundControl controls) {
		try {
			getControls().setSynthControls(synth, (SynthControls)controls);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	protected JPanel createUI(CompoundControl controls) {
		if ( controls instanceof MultiSynthControls ) {
			return new MultiSynthPanel((MultiSynthControls)controls);
		}
		return super.createUI(controls);
	}
}