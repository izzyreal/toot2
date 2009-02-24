// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.swingui.controlui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import uk.org.toot.control.BooleanControl;
import javax.swing.*;

public class BooleanControlPanel extends ControlPanel
{
    private final BooleanControl control;
    private AbstractButton button;
    private ActionListener buttonListener;
    private Color buttonBackgroundColor;

    public BooleanControlPanel(final BooleanControl control) {
        super(control);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.control = control;
        String name = abbreviate(control.getAnnotation());
        final boolean small = name.length() < 2;
		buttonBackgroundColor = control.getStateColor(control.getValue()); 
        if ( !control.isMomentary() ) {
	        button = new JButton(name) {
	        	@Override
    	        public Dimension getMaximumSize() {
    	            Dimension size = super.getPreferredSize();
                    if ( control.isWidthLimited() ) {
	                	size.width = small ? 21 : 42;
                    } else {
                        size.width = 128;
                    }
                	return size;
            	}
	        	@Override
    	        public Dimension getMinimumSize() {
    	            Dimension size = super.getPreferredSize();
	                size.width = small ? 18 : 36;
                	return size;
            	}
    	        @Override
    	        public Color getBackground() {
    	        	return buttonBackgroundColor;
    	        }
        	};
    	    buttonListener = new ActionListener() {
           		public void actionPerformed(ActionEvent ae) {
           	        control.setValue(!control.getValue()); // toggle
       	    	}
    		};
        } else {
	        button = new JButton(name) {
	        	@Override
    	        public Dimension getMaximumSize() {
    	            Dimension size = super.getPreferredSize();
                    if ( control.isWidthLimited() ) {
		                size.width = 45;
                    } else {
                        size.width = 128;
                    }
                	return size;
            	}
        	};
    	    buttonListener = new ActionListener() {
           		public void actionPerformed(ActionEvent ae) {
           	        control.momentaryAction();
       	    	}
   	    	};
        }
        button.setBorder(BorderFactory.createRaisedBevelBorder());
//		button.setMargin(new Insets(0, 0, 0, 0));
        button.setAlignmentX(0.5f);
        add(button);
    }

    public void update(Observable obs, Object arg) {
		buttonBackgroundColor = control.getStateColor(control.getValue());
		repaint();
    }

    public void addNotify() {
        super.addNotify();
   	    button.addActionListener(buttonListener);
    }

    public void removeNotify() {
   	    button.removeActionListener(buttonListener);
        super.removeNotify();
    }
}
