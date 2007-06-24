// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.pitchui;

import java.awt.Dimension;
import javax.swing.JComboBox;
import java.util.Observer;
import java.util.Observable;
import uk.org.toot.pitch.Key;
import uk.org.toot.pitch.PitchClass;

public class RootCombo extends JComboBox implements Observer
{
    private static String[] roots = { "G#", "C#", "F#", "B", "E", "A", "D", "G", "C", "F", "Bb", "Eb" };
	private Key key;

    public RootCombo(Key aKey) {
        super(roots);
        key = aKey;
        if ( key != null ) {
        	key.addObserver(this);
        }
        setSelectedItem("C");
        setPrototypeDisplayValue("C##");
		setMaximumSize(new Dimension(64, 100));
    }

    public void update(Observable o, Object arg) {
        setSelectedItem(PitchClass.name(key.getRoot()));
    }
}
