package uk.org.toot.swingui.midiui.sequenceui;

import uk.org.toot.midi.sequence.MidiTrack;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComponent;
import javax.sound.midi.MidiMessage;
import uk.org.toot.midi.message.ChannelMsg;

public class RidmView extends AbstractRidmView
{
    protected RidmHeader ridmHeader ;
    protected int[] ridmPercs; // midi notes I display

    public RidmView(Viewer viewer) {
        super(viewer);
        updateRidmPercs() ;
        getSequence().getChangeSupport().addChangeListener(
            new ChangeListener() {
            	public void stateChanged(ChangeEvent ce) {
                	updateRidmPercs();
            	}
        	}
        );
    }

    private void updateRidmPercs() {
        ridmPercs = new int[128];
        MidiTrack[] tracks = getSequence().getMidiTracks();
        for ( int t = 1 ; t < tracks.length ; t++ ) {
            if ( !tracks[t].isDrumTrack() ) continue ;
	        MidiTrack track = tracks[t];
    	    // for each note on
        	for ( int i = 0 ; i < track.size() ; i++ ) {
            	MidiMessage msg = track.get(i).getMessage();
	            if ( ChannelMsg.isChannel(msg) ) { // !!! NoteMsg ????
			        // remember each different note
        	        ridmPercs[ChannelMsg.getData1(msg)]++;
            	}
	        }
        }
    	// order the used notes consecutively from 1, 0 means not used
        int y = 1 ; // reset for first used and recount
        for ( int i = 0 ; i < 128 ; i++ ) {
        	if ( ridmPercs[i] > 0 ) ridmPercs[i] = y++;
	    }
        yCount = --y;
//  	      setSize(getPreferredSize());
    }

    public JComponent getYHeader()
    {
        if ( ridmHeader == null ) ridmHeader = new RidmHeader() ;
        return ridmHeader;
    }

    // override to suit ridm view
    protected int y(int note) {
        return (yCount-drum(note)) * pixelsPerY;
    }

    // override to suit ridm view
    protected int midiy(int y) {
        return noteForDrum(yCount - (y / pixelsPerY));
    }

    protected int drum(int note) {
        return ridmPercs[note];
    }

    protected int noteForDrum(int drum) {
        int note;
        for ( note = 0; note < ridmPercs.length; note++ ) {
            if ( ridmPercs[note] == drum ) return note;
        }
        return -1;
    }

    protected void paintNote(Graphics g, int note, int velocity, long onTick, long offTick, boolean bOutline) {
        int x = x(onTick) ;
        int y = y(note) ;
        int w = x(offTick)-x;
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
        for (int p = 0; p <= yCount; p++) {
            g.setColor(lineblack);
            y = y(noteForDrum(p)) ;
            y += pixelsPerY / 2 ; // centre line on drum
            y -= 1 ; // !! compensate for view border thickness
            g.drawLine(drawHere.x, y, drawHere.x+drawHere.width, y);
        }

        super.paintGrid(g); // draw time lines over ridm lines
    }

    protected boolean isVelocity() { return false; }

    public class RidmHeader extends JComponent
    {
        private int SIZE = 24 ;

        public Dimension getPreferredSize() {
            return new Dimension(SIZE, yCount*pixelsPerY);
        }

        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            int ppp = pixelsPerY ; // for thread safety
            // ticks and labels
            for (int d = 0; d <= yCount; d++) // each drum
            {
                int y = y(noteForDrum(d)) ;
                Color color = Color.white; ///
                g.setColor(color);
                g.fillRect(0, y, SIZE-1, ppp) ;
	            g.setColor(Color.black);
                g.drawRect(0, y, SIZE-1, ppp) ;
//                g.drawLine(SIZE-1, i, SIZE-tickLength-1);
            }
        }
    }
}
