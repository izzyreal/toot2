/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.swingui.midiui.sequenceui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JComponent;
import javax.swing.BorderFactory;
import uk.org.toot.midi.core.MidiSystem;
import uk.org.toot.midi.sequence.MidiTrack;
import uk.org.toot.midi.sequence.SequencePosition;
import uk.org.toot.swingui.miscui.TootBar;

public class SpiralEditor extends Editor
{
    private SequenceSpiralView view;
   	private SpiralEditBar editBar;

    public SpiralEditor(OpenSequenceUI openSeqUI, MidiSystem rack) {
        super(openSeqUI, rack);
    }

    protected JComponent createContent() {
        view = new SequenceSpiralView(this);
//        view.add(new SnapCombo());
		editBar = new SpiralEditBar(view);
        JPanel pane = new JPanel(new BorderLayout());
        pane.add(editBar, BorderLayout.NORTH);
        pane.add(new HelixScroller(view), BorderLayout.CENTER);
        pane.setBorder(BorderFactory.createLineBorder(Color.black));
        return pane;
    }

    public void updatePosition(long tick) {
        SequencePosition pos = getSequence().getPosition(tick);
//        System.out.print(pos);
        pos = new SequencePosition(pos.bar, pos.beat); // just bar and beat
//        System.out.println(" from "+pos);
        tick = getSequence().getTick(pos);
        if ( follow && tick != view.getStartTick() ) {
            view.setStartTick(tick);
        }
    }

    public void setVisibleTrack(MidiTrack track, boolean visible) {
        super.setVisibleTrack(track, visible);
        if ( view != null ) view.repaint();
    }

    /**
     * override for edit context, x and y are ignored
     */
    public int getDefaultNote(SequenceView view, int x, int y) {
        return editBar.getNoteValue();
    }

    protected JToolBar createToolBar() {
        return new ToolBar();
    }

    /**
     * ToolBar inner class
     */
    private class ToolBar extends TootBar
    {
        public ToolBar() {
            super(getSequence().getName());
//            add(new GridLeftBar());
//            add(new TimeZoomBar());
            add(new TrackBar());
        }
    }

    /**
     * to provide tool context widgets
     */
    private class SpiralEditBar extends EditBar
    {
        private DrawNoteLabel label;

        public SpiralEditBar(SequenceView view) {
            super(view);
        }

        protected void fillLeftEdit() {
            label = new DrawNoteLabel();
            add(label);
            this.addSeparator();
            super.fillLeftEdit();
        }

        public int getNoteValue() {
			return label.getNoteValue();
        }
    }
}
