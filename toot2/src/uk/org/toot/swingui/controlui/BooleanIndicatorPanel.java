// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.controlui;

import java.awt.Dimension;
//import java.awt.event.FocusListener;
import java.util.Observable;
import uk.org.toot.control.Control;
import uk.org.toot.control.BooleanControl;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

public class BooleanIndicatorPanel extends ControlPanel
{
    private final BooleanControl control;
    private JLabel label;

    public BooleanIndicatorPanel(final BooleanControl control) {
        super(control);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.control = control;
        String name = abbreviate(control.getAnnotation());
        final boolean small = name.length() < 2;
        label = new JLabel(name, JLabel.CENTER) {
   	        public Dimension getMaximumSize() {
   	            Dimension size = super.getPreferredSize();
                if ( control.isWidthLimited() ) {
	                size.width = small ? 21 : 42;
                } else {
                    size.width = 128;
                }
               	return size;
           	}
		    public Dimension getMinimumSize() {
    	    	Dimension size = super.getPreferredSize();
		        size.width = small ? 18 : 36;
       			return size;
   			}
    	};

        label.setAlignmentX(0.5f);
        label.setOpaque(true); // for background color
        update(null, null);
        add(label);
    }

/*		    public Dimension getMinimumSize() {
    	    	Dimension size = super.getPreferredSize();
		        size.width = 22;
       			return size;
   			} */

    public void update(Observable obs, Object arg) {
        boolean val = control.getValue();
		label.setBackground(val ? control.getStateColor(control.getValue()) : getBackground());
    }
}
