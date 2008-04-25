/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.swingui.midiui.sequenceui;

import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import uk.org.toot.midi.sequence.MidiTrack;
import uk.org.toot.midi.misc.GM;
import uk.org.toot.music.tonality.Pitch;
import uk.org.toot.swingui.miscui.ClickAdapter;
import uk.org.toot.swingui.midiui.GMDrumPopup;

public class EditBar extends ViewBar
{
    public EditBar(SequenceView view) {
        super(view);
    }

    protected void fillLeft() {
        super.fillLeft();
        fillLeftEdit();
    }

    protected void fillLeftEdit() {
        add(new JLabel("  Snap   "));
        add(new SnapCombo(view));
    	addSeparator();
    }

    protected class DrawNoteLabel extends JLabel
    {
        protected int note = GM.CLOSED_HI_HAT;
        private ClickAdapter clickAdapter;
        private DrumPopup drumPopup;

        public DrawNoteLabel() {
            super();
            refresh(note);
            setPreferredSize(new Dimension(128, 24));
            // ClickAdapter Popup needs resetting on topTrack change
            drumPopup = new DrumPopup();
            clickAdapter = new ClickAdapter(drumPopup, true);
            addMouseListener(clickAdapter);
            view.addPropertyChangeListener("topTrack",
                new PropertyChangeListener() {
                	public void propertyChange(PropertyChangeEvent ev) {
                    	refresh(DrawNoteLabel.this.note);
                        MidiTrack track = view.getTopTrack();
                        JPopupMenu popup = track.isDrumTrack() ? drumPopup : null;
                        clickAdapter.setPopup(popup);
                	}
            	}
            );
        }

        public void refresh(int drum) {
            note = drum; // !!
            MidiTrack track = view.getTopTrack();
            setText(track.isDrumTrack() ? GM.drumName(note)
                						: Pitch.name(note));
        }

        public int getNoteValue() { return note; }

	    private class DrumPopup extends GMDrumPopup
    	{
        	protected void setDrum(int drum) {
            	DrawNoteLabel.this.refresh(drum);
	        }
    	}

    }
}
