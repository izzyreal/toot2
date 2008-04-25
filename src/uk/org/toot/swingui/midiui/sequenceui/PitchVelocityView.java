package uk.org.toot.swingui.midiui.sequenceui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Graphics;

public class PitchVelocityView extends AbstractPitchView
{
    public PitchVelocityView(Viewer sequence) {
        super(sequence);
        this.pixelsPerY = 1; // !!
    }

    protected void paintGrid(Graphics g)
    {
        Rectangle drawHere = g.getClipBounds();
        // pitch lines
        int y ;
        Color lineblack = Color.lightGray.darker() ;
        for (int p = 0; p < 128; p+=8) {
            g.setColor(lineblack);
			y = y(p);
//            y -= 1 ; // !! compensate for view border thickness
            g.drawLine(drawHere.x, y, drawHere.x+drawHere.width, y);
            }

        super.paintGrid(g);
    }

    // velocity display DOES track viewport height!
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }
}
