/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.swingui.midiui.sequenceui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import uk.org.toot.midi.core.MidiSystem;
import uk.org.toot.swingui.midiui.sequenceui.OpenSequenceUI;
import uk.org.toot.swingui.miscui.TootBar;
import uk.org.toot.midi.sequence.MidiSequence;

/**
 * A Workspace contains a Toolbar, a TabbedPane and a SidePane
 * which relate to a single OpenSequenceUI instance and a MidiSystem instance.
 */
public class WorkSpace extends JPanel
{
    private OpenSequenceUI sequenceUI;
    private JToolBar toolBar ;
    private JTabbedPane tabbedPane;
    private SidePanel sidePane;
	private SplitView splitPane;

    public WorkSpace(OpenSequenceUI sequenceUI, MidiSystem rack) {
        super(new BorderLayout());
        this.sequenceUI = sequenceUI;

        toolBar = new ToolBar();
        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);

        // create the side pane
        sidePane = new SidePanel(sequenceUI, rack);
        sidePane.setMinimumSize(new Dimension(320, 480));

        // create the main/side split pane
	    splitPane = new SplitView(sidePane, tabbedPane);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.0);

        add(toolBar, BorderLayout.SOUTH);
        add(splitPane, BorderLayout.CENTER);
    }

    public JTabbedPane getTabbedPane() { return tabbedPane; }

    public MidiSequence getSequence() { return sequenceUI.getSequence(); }

    public JToolBar getPositionView() { return sequenceUI.getPositionView(); }

    public Dimension getPreferredSize() {
    	return getParent().getSize();
/*        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension((int)(screen.width*0.9), (int)(screen.height*0.5)); */
    }

    /**
     * ToolBar inner class
     */
    private class ToolBar extends TootBar
    {
//        private final String ABOUT = "About...";
        private final String SIDE = "Side";
        private JToggleButton sideTB;

        // the transport toolbar
        public ToolBar() {
            super(getSequence().getName());
            add(sideTB = makeToggleButton("general/Side16", SIDE, SIDE, SIDE, true));
            addSeparator();
            add(getPositionView());
    //	doesn't quite work right because sub bars are necessarily floatable
//            add(Box.createHorizontalGlue());
    //    	    add(makeButton("general/About16", ABOUT, ABOUT, ABOUT, true));
            sideTB.addActionListener(
                new ActionListener() {
                	public void actionPerformed(ActionEvent ae) {
	            		boolean visible = !sidePane.isVisible();
						sidePane.setVisible(visible);
						splitPane.adjustDivider();
		                sideTB.setSelected(visible);
                	}
            	}
            );
        }
    }


}
