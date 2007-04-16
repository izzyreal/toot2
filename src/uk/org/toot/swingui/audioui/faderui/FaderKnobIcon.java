// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.audioui.faderui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
//import javax.swing.JSlider;
import javax.swing.plaf.UIResource;

public class FaderKnobIcon implements Icon, UIResource { // was Serializable
	private static int IW = 24; // icon width
    private static int IH = 31; // icon height
    private static int IH2 = 15; // < half width

    public FaderKnobIcon() {
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.translate(x, y);

        // Fill in the background
        g.setColor(Color.gray);
        g.fillRect(0, 0, IW-1, IH-1);

        Color lineColor = Color.WHITE;
        // Fill in the insert
        if ( c instanceof Fader ) {
            Fader f = (Fader)c;
            Color insertColor = f.getInsertColor();
	        g.setColor(insertColor);
            g.fillOval(1, 1, IW-3, IH-3);
            if ( lineColor == insertColor ) lineColor = Color.BLACK;
    	}

        // draw reference line
        g.setColor(lineColor);
        g.drawLine(0, IH2, IW-1, IH2);

        g.translate(-x, -y);
    }

    public int getIconWidth() {
        return IW;
    }

    public int getIconHeight() {
        return IH;
    }
}
