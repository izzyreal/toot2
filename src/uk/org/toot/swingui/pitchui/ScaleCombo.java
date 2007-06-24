// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.pitchui;

import java.awt.Dimension;
import javax.swing.JComboBox;
import uk.org.toot.pitch.Scales;

public class ScaleCombo extends JComboBox
{
    public ScaleCombo() {
        super(Scales.getScaleNames().toArray());
        setPrototypeDisplayValue("Lydian Dominant ##"); // !!! !!!
        setMaximumSize(new Dimension(120, 50));
    }
}
