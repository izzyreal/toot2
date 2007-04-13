/* Copyright Steve Taylor 2006 */

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
