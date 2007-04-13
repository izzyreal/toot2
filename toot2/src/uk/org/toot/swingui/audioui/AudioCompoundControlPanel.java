// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.audioui;

import java.util.Observer;
import java.util.Observable;
import uk.org.toot.control.*;
import uk.org.toot.audio.core.*;

import java.awt.Color;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import uk.org.toot.swingui.controlui.*;

public class AudioCompoundControlPanel extends CompoundControlPanel implements Observer
{
    private Color originalBackground;

    public AudioCompoundControlPanel(CompoundControl control, int axis,
        	ControlSelector controlSelector, PanelFactory panelFactory, boolean hasBorder, boolean hasHeader) {
        super(control, axis, controlSelector, panelFactory, hasBorder, hasHeader);
        originalBackground = getBackground();
    }

    protected boolean reverseIfYAxis() {
        if ( control instanceof AudioControls ) {
            return ((AudioControls)control).hasOrderedFrequencies();
        }
        return super.reverseIfYAxis();
	}

    protected void create() {
        if ( target == null ) return;
        if ( axis == BoxLayout.X_AXIS && control instanceof CompoundControlChain ) {
    	    target.add(Box.createHorizontalGlue()); // left aligned
        }
        super.create();
        if ( axis != BoxLayout.Y_AXIS ) return;
        // we need to add vertical glue if
        // there aren't any child components
       	if ( target.getComponentCount() == 0 ) {
            target.add(Box.createVerticalGlue());
            target.add(Box.createHorizontalStrut(45));
//            System.out.println(control.getControlPath()+" VGlue & HStrut in ACCP.create()");
        } else if ( target.getComponentCount() == 1 && (control instanceof AudioControlsChain) ) {
	        // or there's just one and it's too small
            if ( target.getComponent(0).getMaximumSize().height < 200 ) {
	            target.add(Box.createVerticalGlue());
//	            System.out.println(control.getControlPath()+" VGlue in ACCP.create()");
            }
        }
    }
}
