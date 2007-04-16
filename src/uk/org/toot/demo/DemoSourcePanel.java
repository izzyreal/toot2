// Copyright (C) 2007 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import javax.swing.*;
import uk.org.toot.swingui.controlui.*;

// a trivial explicit UI for borrowed mute/solo controls
public class DemoSourcePanel extends JPanel
{
    public DemoSourcePanel(DemoSourceControls controls) {
        add(new BooleanControlPanel(controls.muteControl));
        add(new BooleanControlPanel(controls.soloControl));
    }
}
