package uk.org.toot.swingui.midiui.sequenceui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import uk.org.toot.midi.sequence.edit.Transpose;
import uk.org.toot.midi.sequence.MidiSequence;
import uk.org.toot.midi.sequence.MidiTrack;
import uk.org.toot.midi.sequence.edit.Cut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrackPopupMenu extends JPopupMenu {

    private MidiTrack track;

    // constructor
    public TrackPopupMenu() {
        add(createTransposeMenu());
        add(new CutItem());
    }

    protected JMenu createTransposeMenu() {
        JMenu menu = new JMenu("Transpose");
        menu.add(new TransposeItem("Octave Up", 12));
        menu.add(new TransposeItem("Octave Down", -12));
        menu.add(new TransposeItem("Semitone Up", 1));
        menu.add(new TransposeItem("Semitone Down", -1));
        return menu;
    }

    public void setTrack(MidiTrack atrack) {
        track = atrack;
        setLabel(track.getTrackName());
    }

    private class TransposeItem extends JMenuItem implements ActionListener{
        private int semitones;

        public TransposeItem(String label, int semitones) {
            super(label);
            this.semitones = semitones;
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            MidiSequence sequence = track.getSequence();
   	        sequence.edit(new Transpose(track, semitones));
        }
    }

    private class CutItem extends JMenuItem implements ActionListener
    {
        public CutItem() {
            super("Cut");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
	        MidiSequence sequence = track.getSequence();
    	    sequence.edit(new Cut(track));
        }
    }

}


