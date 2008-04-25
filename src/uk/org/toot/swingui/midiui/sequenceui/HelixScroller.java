/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.swingui.midiui.sequenceui;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import uk.org.toot.midi.sequence.SequencePosition;

/**
 * Scroll a Helix view by Bars
 */
public class HelixScroller extends JPanel
{
    public SequenceSpiralView view;

    public HelixScroller(SequenceSpiralView view) {
        super(new BorderLayout());
        this.view = view;
        add(view, BorderLayout.CENTER);
        add(new TimeScrollBar(), BorderLayout.SOUTH);
        setBorder(BorderFactory.createLoweredBevelBorder());
    }

    private class TimeScrollBar extends JScrollBar
    {
        private SequencePosition pos = new SequencePosition(0);

        public TimeScrollBar() {
            super(HORIZONTAL, 0, 4, 0, view.getSequence().getExtent().bar); // !!! 4 bars
            addAdjustmentListener(
                new AdjustmentListener() {
                	public void adjustmentValueChanged(AdjustmentEvent ae) {
                    	pos.bar = ae.getValue();
                    	view.setStartTick(view.getSequence().getTick(pos));
                	}
            	}
        	);
        }
    }
}
