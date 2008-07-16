/* Generated by TooT */

package uk.org.toot.swingui.midiui.sequenceui;

import uk.org.toot.midi.sequence.MidiTrack;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import uk.org.toot.midi.misc.GM;
import uk.org.toot.swingui.midiui.sequenceui.SequenceTimeView;
import uk.org.toot.swingui.midiui.sequenceui.SequenceView;

abstract public class AbstractRidmView extends SequenceTimeView {
    public AbstractRidmView(Viewer sequence) {
        super(sequence);
    }

    protected JPopupMenu createPopupMenu() {
        return new RidmPopup();
    }

    protected boolean isValid(MidiTrack track) {
        return track.isDrumTrack();
	}

    public String noteName(int note) {
        return GM.drumName(note);
    }

    public class RidmPopup extends SequenceView.TimePopup {
        private JMenu ridmMenu;

        // constructor
        public RidmPopup() {
            super();
            ridmMenu = createRidmMenu();
            add(ridmMenu);
        }

        protected JMenu createRidmMenu() {
            JMenu menu = new JMenu("Drum");
            menu.add(new JMenuItem("Zoom In"));
            menu.add(new JMenuItem("Zoom Out"));
            return menu;
        }
    }
}