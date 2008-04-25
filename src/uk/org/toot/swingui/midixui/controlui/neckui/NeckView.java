// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.swingui.midixui.controlui.neckui;

import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import uk.org.toot.midix.control.neck.ChordShape;
import uk.org.toot.midix.control.neck.StrungNeck;
import uk.org.toot.music.tonality.Pitch;

public class NeckView extends JComponent
{
    private StrungNeck neck;
    private float tet12 = 1.059463094359f;

    public NeckView(StrungNeck aNeck) {
        neck = aNeck;
    }

    public Dimension getPreferredSize() {
        return new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().width-10), 32*neck.getStringCount());
    }

    public int getPixelsPerString() { return getHeight() / (neck.getStringCount()+1); }

    public StrungNeck getNeck() { return neck; }

    public StrungNeck.TunedString getString(int s) { return getNeck().getString(s); }

    public int getStringCount() { return getNeck().getStringCount(); }

    public int getFrets() { return getNeck().getFrets(); }

    protected void paintComponent(Graphics g) {
//        paintFretboard(g);
        // draw frets (really only if fretted)
        paintFrets(g);
        // draw strings
        paintStrings(g);
	}

    protected void paintFrets(Graphics g) {
//        Rectangle drawRect = g.getClipBounds();
		int centre = getStringCount() / 2;
//        centre -=1 ; // correct for string number
        int o = getPixelsPerString() / 2;
        for ( int i = 0 ; i <= getFrets(); i++ ) {
            if ( i > 0 ) {
	        	g.setColor(Color.darkGray);
	            int xpos = (xfret(i)+xfret(i-1))/2;
    	        if ( i == 3 || i == 5 || i == 7 || i ==9 || i == 15 || i == 17 || i == 19 || i == 21 ) {
        	        g.fillOval(xpos-4, ystring(centre)+o, 8, 8);
            	} else if ( i == 12 || i == 24 ) {
                	g.fillOval(xpos-4, ystring(centre+1)+o, 8, 8);
	                g.fillOval(xpos-4, ystring(centre-1)+o, 8, 8);
    	        }
            }
	        g.setColor(Color.black);
            g.drawLine(xfret(i), ystring(0)+o, xfret(i), ystring(getStringCount()-1)-o);
            g.drawString(i > 0 ? String.valueOf(i) : "Nut", xfret(i)-16, ystring(0)+o+12);
        }
    }

    protected void paintStrings(Graphics g) {
//        Rectangle drawRect = g.getClipBounds();
        g.setColor(Color.black);
        for ( int i = 0 ; i < getStringCount(); i++ ) {
            g.drawLine(31, ystring(i), getWidth()-42, ystring(i));
            g.drawString(Pitch.name(getString(i).getOpenTuning()), 0, ystring(i)+4);
        }
    }

    /**
     * Fret position, separation halves every octave
     */
    protected int xfret(int fret) {
        int w = (int)(getWidth()*1.2);
        return w - (int)((w-42)/Math.pow(tet12, fret)) + 24;
    }

    protected int ystring(int string) {
        return getHeight() - (string * getPixelsPerString()) - getPixelsPerString();
    }

    /**
     * @return the 0 based index of the fret nearest the mouse x position
     * a binary search might be usefully faster
     */
    protected int fret(int x) {
        int fret = 0;
        int nfrets = getFrets();
        while ( xfret(fret) < x && fret <= nfrets) fret++;
		return fret;
    }

    /**
     * @return the 0 based index of the string nearest the mouse y position
     * simplest impl is to reverse the ystring method!
     */
    protected int string(int y) {
//        y + getPixelsPerString() - getHeight() = - (string * getPixelsPerString());
//        getHeight() - y - getPixelsPerString() = string * getPixelsPerString();
		int s = (getHeight() - y - (getPixelsPerString()/2))/getPixelsPerString();
        if ( s >= getStringCount() ) {
            s = getStringCount()-1;
		}
		return s;
    }

    protected void barre(final int fret) {
        getNeck().barre(fret);
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
	        	Graphics g = getGraphics();
    	    	g.setColor(Color.white);
        		int from = getStringCount() - getNeck().getBarreSize();
        		g.drawLine(xfret(fret), ystring(from)+8, xfret(fret), ystring(getStringCount()-1)-8);
            }
    	});
    }

    protected void shape(int f, ChordShape shape, int firstString) {
        getNeck().shape(f, shape, firstString);
        repaint();
    }

    protected void mute() {
        getNeck().mute();
        repaint();
    }

    protected void muteString(final int str) {
		final StrungNeck.TunedString string = getString(str);
        string.mute();
    }

    protected boolean pickString(final int str, boolean up, int velocity) {
		final StrungNeck.TunedString string = getString(str);
        if ( string.getFret() < 0 ) velocity = 0;
        string.pick(up, velocity);
        if ( velocity < 1 ) return false;
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		        Graphics g = getGraphics();
        		g.setColor(Color.white);
		        g.drawLine(xfret(string.getFret()), ystring(str), getWidth()-42, ystring(str));
		        g.setColor(Color.black);
        		g.drawString(Pitch.name(getString(str).getNote()), getWidth()-38, ystring(str)+4);
            }
    	});
        return true;
    }

	protected void setBending(int str, boolean b) {
        StrungNeck.TunedString string = getString(str);
        if ( string == null ) return; // invalid string
        string.setBending(b);
    }
}
