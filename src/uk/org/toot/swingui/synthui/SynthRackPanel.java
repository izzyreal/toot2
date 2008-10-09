package uk.org.toot.swingui.synthui;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.service.ServiceDescriptor;
import uk.org.toot.service.ServiceVisitor;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.SynthServices;
import uk.org.toot.synth.SynthRackControls;
import uk.org.toot.synth.synths.multi.MultiSynthControls;

public class SynthRackPanel extends MultiControlPanel 
{
	private static Vector<String> selectionNames = new Vector<String>();
	
	private SynthRackControls rackControls;
	private JLabel statusLabel;
    private Observer statusObserver = null;

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
		rackControls = controls;
		add(createToolBar(), BorderLayout.NORTH);
        statusObserver = new Observer() {
           	public void update(Observable obs, Object arg) {
               	if ( arg != null && arg instanceof Control ) {
               		updateStatusLabel((Control)arg);
				}
           	}
        };
	}

	protected JComponent createToolBar() {
		JToolBar bar = new JToolBar();
		statusLabel = new JLabel("Label");
        bar.add(Box.createHorizontalStrut(200));
        bar.add(Box.createGlue());
		bar.add(statusLabel);
        bar.add(Box.createGlue());
		return bar;
	}
	
    protected void updateStatusLabel(Control c) {
        statusLabel.setText(c.getControlPath(rackControls, ", ")+"  "+c.getValueString());
    }
    
    public void addNotify() {
        super.addNotify();
        rackControls.addObserver(statusObserver);
    }

    public void removeNotify() {
        rackControls.deleteObserver(statusObserver);
        if ( doDispose ) statusObserver = null;
        super.removeNotify();
    }

	@Override
	protected void dispose() {
		super.dispose();
		rackControls = null;
	}

	@Override
	protected Vector<String> getSelectionNames() {
		return selectionNames;
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
	
	@Override
	protected CompoundControl createControls(String name) {
		return SynthServices.createControls(name);
	}
	
	protected JPanel createUI(CompoundControl controls) {
		if ( controls instanceof MultiSynthControls ) {
			return new MultiSynthPanel((MultiSynthControls)controls);
		}
		return super.createUI(controls);
	}
}