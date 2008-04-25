package uk.org.toot.swingui.midiui.sequenceui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import uk.org.toot.midi.sequence.MidiTrack;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JViewport;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import uk.org.toot.swingui.midiui.MidiColor;

public class ViewScrollPane extends TimeScrollPane
                       // implements ItemListener
{
//    private JToggleButton timeRep;
//    protected MidiSequence sequence ;
	private TopTrackCorner topTrackCorner;

    public ViewScrollPane(SequenceTimeView view, boolean tHeader)
    {
        this.view = view;
//        this.sequence = sequence;
        getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        //Set up the scroll pane.
        setViewportView(view);
        setPreferredSize(new Dimension(600, 250)); // port size
        setViewportBorder(BorderFactory.createLineBorder(Color.black));

        //Create the row and column headers.
        if ( tHeader ) {
        	timeHeader = view.getTimeHeader();
        	setColumnHeaderView(timeHeader);
            topTrackCorner = new TopTrackCorner();
	        setCorner(JScrollPane.UPPER_LEFT_CORNER, topTrackCorner);
        }
        yHeader = view.getYHeader();
        setRowHeaderView(yHeader);

    }

    /**
     * Inner class for corner showing top track hue
     */
    private class TopTrackCorner extends JComponent {

        public TopTrackCorner() {
//	        addMouseListener(new ClickAdapter(createPopupMenu()));
            view.addPropertyChangeListener("topTrack",
                new PropertyChangeListener() {
                	public void propertyChange(PropertyChangeEvent ev) {
                    	repaint();
                	}
            	}
            );
        }

/*        protected JPopupMenu createPopupMenu() {
            return new TopTrackLabel.ViewTrackPopup(view);
        } */

        protected void paintComponent(Graphics g) {
            MidiTrack track = view.getTopTrack();
            Color color;
	        float hue = (Float)track.getClientProperty("Hue");
    	    color = MidiColor.asHSB(hue, 0.42f, 1.0f);
            g.setColor(color);
            Rectangle drawRect = g.getClipBounds();
            g.fillRect(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
        }
    }
}
