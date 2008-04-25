// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.swingui.audioui.serverui;

import javax.swing.*;

public class SampleRateCombo extends JComboBox
{
    public SampleRateCombo(String sampleRate) {
        addItem("44100");
        addItem("48000");
        addItem("88200");
        addItem("96000");
        addItem("176400");
		addItem("192000");
		if ( sampleRate != null ) {
			setSelectedItem(sampleRate);
		}
    }
}
