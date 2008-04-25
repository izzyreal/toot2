/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.swingui.midiui.sequenceui;

import javax.swing.JComboBox;
import uk.org.toot.midi.sequence.MidiSequence;
import uk.org.toot.midi.sequence.NameEvent;

public class MarkerCombo extends JComboBox
{
    private MidiSequence sequence;

    public MarkerCombo(MidiSequence sequence) {
        this.sequence = sequence;
    }

    public void showPopup() {
      	NameEvent[] markers = sequence.getMarkers();
        removeAllItems();
        for ( int i = 0 ; i < markers.length ; i++ )
            addItem(markers[i].getName());
		super.showPopup();
    }
}
