// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.audioui.mixerui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import uk.org.toot.control.*;
import uk.org.toot.audio.core.*;
import uk.org.toot.audio.mixer.*;
import uk.org.toot.audio.fader.*;
import uk.org.toot.swingui.audioui.*;
import uk.org.toot.swingui.audioui.faderui.*;
//import uk.org.toot.swingui.audioui.meterui.*;

public class MixerPanelFactory extends AudioPanelFactory
{
    public JComponent createComponent(Control control, int axis, boolean hasHeader) {
        if ( control instanceof FaderControl ) {
            JPanel faderPanel = new FaderPanel((FaderControl)control, isFaderRotary(control));
            faderPanel.setAlignmentY(0.25f); // ??? !!!
            return faderPanel;
        }
        return super.createComponent(control, axis, hasHeader);
    }

    public boolean isFaderRotary(Control control) {
        return true;
    }
}
