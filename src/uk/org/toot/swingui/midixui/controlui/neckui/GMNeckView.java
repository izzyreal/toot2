// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.midixui.controlui.neckui;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import uk.org.toot.midix.control.neck.NeckFamily;
import uk.org.toot.midix.control.neck.StrungNeck;
import uk.org.toot.midix.control.neck.Tunings;
import uk.org.toot.midi.misc.GM;

public class GMNeckView extends JPanel
{
    private JToolBar toolBar;
    private NeckController ctrl;
    private boolean init = false;

    // toolbar and neck controller/view
    public GMNeckView(NeckController controller) {
        super(new BorderLayout());
        ctrl = controller;
        toolBar = new GMNeckToolBar();
        add(toolBar, BorderLayout.NORTH);
        add(controller, BorderLayout.CENTER);
        init = true;
    }

    public StrungNeck getNeck() { return ctrl.getNeck(); }

    private ProgramCombo programCombo;

    public void sendProgram() { if ( init ) programCombo.sendProgram(); } // !!!! !!!

    private class GMNeckToolBar extends JToolBar
    {
        public GMNeckToolBar() {
			// tuning selection
/*            add(new JLabel("  Tuning  "));
            final Tunings tunings = getNeck().getFamily().getTunings();
            final TuningCombo tuningCombo = new TuningCombo(tunings);
            tuningCombo.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    String tuning = (String)tuningCombo.getSelectedItem();
                    ctrl.setTuning(tunings.createTuning(tuning));
                    sendProgram(); // update possible new strings
                }
            });
            add(tuningCombo); */

            // octave string checkbox
            final JCheckBox octaveCheckBox = new JCheckBox("x2", getNeck().hasOctaveStrings());
            octaveCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    getNeck().setOctaveStrings(octaveCheckBox.isSelected());
                    ctrl.repaint();
                }
            });
            add(octaveCheckBox);
            addSeparator();
            add(new JLabel("  Style  "));

            // barre combo
/*            add(new JLabel(" Barre Size "));
            add(new BarreCombo()); */

			// chord shape combo
//            final ChordCombo chordCombo = new ChordCombo(chordFamily);
//            add(chordCombo);

            final StyleCombo styleCombo = new StyleCombo();
            add(styleCombo);

            final JCheckBox releaseCheckBox = new JCheckBox("Release", ctrl.getStyle().releasing);
            releaseCheckBox.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
                    ctrl.getStyle().releasing = releaseCheckBox.isSelected();
                }
    		});
            add(releaseCheckBox);

            addSeparator();
            add(new JLabel("  Sound  "));
            // y control function combo
            // output device combo
            // program combox
            NeckFamily f = getNeck().getFamily();
            programCombo = new ProgramCombo(f.getGMFamily(), 8);
            add(new FamilyCombo(f.getGMFamily()));
            add(programCombo);
            addSeparator();

            add(Box.createHorizontalGlue());
			add(ctrl.getChordLabel());
            add(Box.createHorizontalStrut(16));
        }
    }

    private class StyleCombo extends JComboBox implements ActionListener
    {
        public StyleCombo() {
            for ( NeckController.PlayingStyle style : ctrl.getStyles() ) {
                addItem(style.getName());
            }
            setPrototypeDisplayValue("Chords #");
            setMaximumSize(new Dimension(120, 100));
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            ctrl.useStyle((String)getSelectedItem());
        }
    }

    private class ProgramCombo extends JComboBox implements ActionListener
    {
        private int offset;

        public ProgramCombo(int family, int num) {
            setPrototypeDisplayValue("######################");
            setMaximumSize(new Dimension(200, 100));
            addActionListener(this);
            setFamily(family);
        }

        public void setFamily(int family) {
            removeActionListener(this);
            offset = family * 8;
            removeAllItems();
            for ( int i = 0; i < 8; i++ ) {
                addItem(GM.melodicName(i+offset));
            }
            setSelectedIndex(0);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
//            System.out.println("sendProgram because "+e);
            sendProgram();
        }

        public void sendProgram() {
            if ( getSelectedIndex() >= 0 ) {
	            getNeck().setProgram(getSelectedIndex()+offset);
            }
        }
    }


    private class FamilyCombo extends JComboBox implements ActionListener
    {
        public FamilyCombo(int family) {
            addActionListener(this);
            setPrototypeDisplayValue("Chromatic Perc"); // !!
            setMaximumSize(new Dimension(200, 100));
            setFamily(family);
        }

        public void setFamily(int family) {
            this.removeAllItems();
            for ( int i = 0; i < 16; i++ ) {
                addItem(GM.melodicFamilyName(i));
            }
            setSelectedIndex(family);
        }

        public void actionPerformed(ActionEvent e) {
            if ( getSelectedIndex() >= 0 ) {
            	programCombo.setFamily(getSelectedIndex());
            }
        }
    }


    @SuppressWarnings("unused")
	private class BarreCombo extends JComboBox implements ActionListener
    {
        public BarreCombo() {
            for ( int i = 0; i <= getNeck().getStringCount(); i++ ) {
                addItem(i > 0 ? String.valueOf(i) : "Off");
            }
            setSelectedIndex(getNeck().getBarreSize());
            addActionListener(this);
            setMaximumSize(new Dimension(50, 40));
        }

        public void actionPerformed(ActionEvent e) {
            getNeck().setBarreSize(getSelectedIndex());
        }
    }


    public class TuningCombo extends JComboBox
    {
        public TuningCombo(Tunings t) {
            setPrototypeDisplayValue("C F# C F# C F# - Aug Fourths ##");
            setMaximumSize(new Dimension(200, 100));
            for ( String tuning : t.getTunings() ) {
                addItem(tuning);
            }
        }
    }
}
