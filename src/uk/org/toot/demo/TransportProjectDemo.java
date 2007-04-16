// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import uk.org.toot.swingui.projectui.*;
import uk.org.toot.swingui.audioui.mixerui.*;
import uk.org.toot.swingui.audioui.serverui.AudioServerPanel;

/**
 * TransportProjectDemo displays a full user interface over the underlying
 * demonstration problem domain model, with the multi-track player and automated
 * mixer in separate tabs.
 */
public class TransportProjectDemo extends AbstractDemo 
{
    public TransportProjectDemo(String[] args) {
        super(args);
    }

    protected void createUI(String[] args) {
        super.createUI(args);
        SingleTransportProjectPanel panel = new SingleTransportProjectPanel(project);
//        panel.addTab("MultiTrack", new MultiTrackPanel(multiTrackControls));
        panel.addTab("Audio Mixer", new CompactMixerPanel(mixerControls));
        panel.addTab("Audio Server", new AudioServerPanel(extendedServer)); // !!! not non-real-time 
       	frame(panel, "Toot Transport Project");
        // add the source demo panel as a separate frame
//        frame(new DemoSourcePanel(demoSourceControls), "Source Demo");
		project.openProject("default");
    }

    public static void main(String[] args) {
        new TransportProjectDemo(args);
    }
}
