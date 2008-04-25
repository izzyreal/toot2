// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.swingui.midixui.controlui.neckui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import uk.org.toot.midi.core.MidiSystem;
import uk.org.toot.midix.control.neck.NeckFamily;
import uk.org.toot.midix.control.neck.Player;
import uk.org.toot.midix.control.neck.StrungNeck;
import uk.org.toot.swingui.tonalityui.RootCombo;
import uk.org.toot.swingui.tonalityui.ScaleCombo;
import uk.org.toot.music.tonality.Key;
import uk.org.toot.music.tonality.Pitch;
import uk.org.toot.music.tonality.Scales;

public class NecksView extends JPanel
{
	private MidiSystem midiSystem;
    private JTabbedPane tabbedPane;
    private JToolBar toolBar;
    private Player player = new Player();

    // toolbar and tabbed pane
    public NecksView(MidiSystem midiSystem) {
        super(new BorderLayout());
        this.midiSystem = midiSystem;
        tabbedPane = new JTabbedPane();
        toolBar = new NeckToolBar();
        add(toolBar, BorderLayout.SOUTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(new VelocitySlider(), BorderLayout.EAST);
        tabbedPane.addChangeListener(
            new ChangeListener() {
            	public void stateChanged(ChangeEvent e) {
                	GMNeckView view = (GMNeckView)tabbedPane.getSelectedComponent();
                    view.sendProgram(); // update program change
            	}
        	}
        );
    }

    public void addNeck(NeckController ctrl) {
        midiSystem.addMidiDevice(ctrl.getNeck());
        GMNeckView view = new GMNeckView(ctrl);
        tabbedPane.add(ctrl.getNeck().getName()+"  ", view);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1); // autoselect
//        view.sendProgram();
    }

    protected Key getKey() { return player.getKey(); }

    protected int getVelocity() { return player.getVelocity(); }

    protected void setVelocity(int velocity) { player.setVelocity(velocity); }

    public Dimension getPreferredSize() {
        return new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().width-10), 320);
    }

    private class VelocitySlider extends JSlider
    {
        public VelocitySlider() {
            super(JSlider.VERTICAL, 0, 127, getVelocity());
            addChangeListener(new ChangeListener() {
		        public void stateChanged(ChangeEvent e) {
            		setVelocity(getValue());
            	}
            });
        }
    }

    private class NeckToolBar extends JToolBar //implements ActionListener
    {
        public NeckToolBar() {
            for ( NeckFamily family : NeckFamily.families() ) {
                JButton button = new JButton(" New "+family.getName());
                button.setActionCommand(family.getName());
                button.addActionListener(new ActionListener() {
	        		public void actionPerformed(ActionEvent e) {
    		    		String cmd = e.getActionCommand();
		    	    	addNeck(new NeckController(new StrungNeck(NeckFamily.named(cmd)), player));
    		    	}
                });
                add(button);
            }
            addSeparator();
            add(new JLabel("  Tonality  "));

            // key root combo
            final RootCombo rootCombo = new RootCombo(player.getKey());
            add(rootCombo);
            rootCombo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    getKey().setRoot(Pitch.classValue((String)rootCombo.getSelectedItem()));
                }
            });

            // key scale combo
            final ScaleCombo scaleCombo = new ScaleCombo();
            add(scaleCombo);
            scaleCombo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    getKey().setScale(Scales.getScale((String)scaleCombo.getSelectedItem()));
                }
            });

            add(Box.createHorizontalStrut(16));
            add(new JLabel("Ctrl: sus4"));
            add(Box.createHorizontalStrut(12));
            add(new JLabel("Alt: 6"));
            add(Box.createHorizontalStrut(16));
            add(new JLabel("F1: Modes"));
            add(Box.createHorizontalStrut(12));
            add(new JLabel("F2: Chords"));
            add(Box.createHorizontalStrut(16));
            add(new JLabel("F9,F10,F11,F12: C,A,G,E-shaped Chords"));
            add(Box.createHorizontalGlue());
			add(new JLabel("toot.org.uk"));
            add(Box.createHorizontalStrut(16));
        }
    }
}
