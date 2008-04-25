package uk.org.toot.swingui.midiui.sequenceui;

import uk.org.toot.midi.sequence.MidiTrack;
import uk.org.toot.music.tonality.Pitch;
import uk.org.toot.swingui.midiui.MidiColor;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;

public class PitchView extends AbstractPitchView
{
    protected PitchHeader pitchHeader ;

    public PitchView(Viewer sequence) {
        super(sequence);
    }

	// override to suit pitch view
    protected int y(int note) {
        return (127-note) * pixelsPerY;
    }

	// override to suit pitch view
    protected int midiy(int y) {
        return 127 - (y / pixelsPerY);
    }

    protected void paintNote(Graphics g, int note, int velocity, long onTick, long offTick, boolean bOutline) {
        int x = x(onTick) ;
        int y = y(note) ; // invert axis
        int w = x(offTick-onTick) ;
        int h = pixelsPerY ;
        if ( w < 3 ) w = 3; // ensure minimum visible width !!! what about match ? !!!
        if ( !bOutline ) g.fillRect(x, y, w, h);
        g.setColor(bOutline ? Color.white : getForeground());
        g.drawRect(x, y, w, h);
    }

    protected void paintGrid(Graphics g) {
        Rectangle drawHere = g.getClipBounds();
        // pitch lines
        int y ;
        Color lineblack = Color.lightGray.darker() ;
        for (int p = 0; p < 128; p++) {
            g.setColor(Pitch.isWhite(p) ? Color.white : lineblack);
            y = y(p) ;
            y += pixelsPerY / 2 ; // centre line on pitch
//            y -= 1 ; // !! compensate for view border thickness
            g.drawLine(drawHere.x, y, drawHere.x+drawHere.width, y);
            }

        super.paintGrid(g);
    }

    public JComponent getYHeader() {
        if ( pitchHeader == null ) pitchHeader = new PitchHeader() ;
        return pitchHeader ;
    }

    protected boolean isVelocity() { return false; }

    public class PitchHeader extends JComponent
    {
        private int SIZE = 24 ;
        private int cursor = 0; // none to begin
        private boolean[] active = new boolean[128];

        public Dimension getPreferredSize() {
                return new Dimension(SIZE, 128*pixelsPerY);
        }

        public void setCursorAt(int midiy) {
            cursor = midiy;
            repaint();
        }

        public boolean isActive(int midiy) {
            return active[midiy];
        }

        public void setActive(int midiy) {
            active[midiy] = true;
            repaint();
        }

        private int y(int midiy) {
            return (127-midiy)*pixelsPerY ;
        }

        protected void paintComponent(Graphics g)
        {
//            Rectangle clip = g.getClipBounds();
            // Fill clipping area
            g.setColor(getBackground());
//            g.fillRect(clip.x, clip.y, clip.width, clip.height);
            g.fillRect(0, 0, getWidth(), getHeight());

            Color cursorColor = Color.yellow;
            MidiTrack track = getTopTrack();
            if ( cursor >= 0 ) {
            	cursorColor =
                	MidiColor.asHSB((Float)track.getClientProperty("Hue"), 0.42f, Pitch.isWhite(cursor) ? 1.0f : 0.7f);
        	}
            int ppp = pixelsPerY ; // for thread safety
            // ticks and labels
            for (int p = 0; p < 128; p++) // each semitone bondary
            {
                Color color = Pitch.isWhite(p) ? Color.white : Color.black;
                if ( p == cursor ) color = cursorColor;
                if ( isActive(p) ) color = Color.yellow;
                int y = y(p) ;
                g.setColor(color);
                g.fillRect(0, y, SIZE-1, ppp) ;
	            g.setColor(Color.black);
                g.drawRect(0, y, SIZE-1, ppp) ;
//                g.drawLine(SIZE-1, i, SIZE-tickLength-1);
            }
        }
    }
}
