// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import uk.org.toot.swingui.audioui.AudioCompoundStripPanel;
import uk.org.toot.swingui.audioui.meterui.MeterPanelFactory;
import uk.org.toot.swing.DisposablePanel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

/**
 * MultiTrackPanel is an experimental multi-track recorder/player UI
 */
public class MultiTrackPanel extends DisposablePanel
{
    private JScrollPane scrollPane;
    private JPanel panel;

    public MultiTrackPanel(MultiTrackControls mtc) {
        setLayout(new BorderLayout());
        panel = new AudioCompoundStripPanel(mtc, new MeterPanelFactory());
        scrollPane = new JScrollPane(panel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);
    }

    protected void dispose() {
        // naive
        panel.removeAll();
        scrollPane.removeAll();
        removeAll();
    }
}
