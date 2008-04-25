/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.swingui.midiui.sequenceui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import uk.org.toot.midi.sequence.MidiSequence;
import uk.org.toot.midi.sequence.edit.Cut;
import uk.org.toot.swingui.miscui.TootBar;

public class TrackTableView extends JPanel
{
	private TrackTable trackTable;

    public TrackTableView(TrackTable trackTable) {
        super(new BorderLayout());
        this.trackTable = trackTable;
        add(new TrackTableBar(), BorderLayout.NORTH);
        add(new ScrollView(trackTable), BorderLayout.CENTER);
    }

    private class TrackTableBar extends TootBar implements ActionListener
    {
    	static final private String NEW = "New Track";
        static final private String CUT = "Cut Track";

	    private JButton newB;
    	private JButton delB;

        public TrackTableBar() {
        	newB = makeButton("general/New16", NEW, NEW, NEW, true);
    		add(newB);
            delB = makeButton("general/Cut16", CUT, CUT, CUT, true);
            add(delB);
        }

        public void actionPerformed(ActionEvent ae) {
            int trk = trackTable.getSelectedRow();
            TrackTableModel trackTableModel = (TrackTableModel)trackTable.getModel();
            MidiSequence sequence = trackTableModel.getSequence();
            String cmd = ae.getActionCommand();
            if ( NEW.equals(cmd) ) {
                sequence.createTrack(); // !!! no history but trivial to reverse
            } else if ( CUT.equals(cmd) ) {
	            if ( trk < 0 ) return;
	            sequence.edit(new Cut(sequence.getMidiTrack(trk)));
   	            trackTableModel.fireTableDataChanged();
            }
        }
    }
}
