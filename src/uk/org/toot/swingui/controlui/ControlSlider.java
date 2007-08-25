// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.controlui;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import uk.org.toot.control.*;
import javax.swing.JSlider;

public class ControlSlider extends JSlider implements ControlComponent, Observer
{
    private final FloatControl control;

	public ControlSlider(final FloatControl control) {
    	super(VERTICAL, 0, control.getLaw().getResolution()-1, 0);
        this.control = control;
        super.setValue(sliderValue(control.getValue()));
        setPaintTrack(false);
	}

	public Control getControl() {
		return control;
	}
	
	public void addNotify() {
        super.addNotify();
        control.addObserver(this);
    }

    public void removeNotify() {
        control.deleteObserver(this);
        super.removeNotify();
    }

   	public void update(Observable obs, Object obj) {
       	super.setValue(sliderValue(control.getValue())); // !!! avoid NEL?
//        repaint();
    }

	protected float userValue(int sliderVal) {
        return control.getLaw().userValue(sliderVal);
	}

	protected int sliderValue(float userVal) {
        return control.getLaw().intValue(userVal);
    }

	public void setValue(int sliderVal) {
        super.setValue(sliderVal);
//        control.setAdjusting(getValueIsAdjusting());
    	control.setValue(userValue(sliderVal));
	}

    public Color getInsertColor() { return control.getInsertColor(); }
}


