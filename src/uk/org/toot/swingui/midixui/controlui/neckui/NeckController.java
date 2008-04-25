// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.swingui.midixui.controlui.neckui;

import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.awt.Color;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.JLabel;

import uk.org.toot.midix.control.neck.ChordFamily;
import uk.org.toot.midix.control.neck.ChordShape;
import uk.org.toot.midix.control.neck.Player;
import uk.org.toot.midix.control.neck.StringTuning;
import uk.org.toot.midix.control.neck.StrungNeck;
import uk.org.toot.music.tonality.*;

import java.awt.Graphics;

public class NeckController extends NeckView implements MouseWheelListener, Observer //,KeyProvide
{
//    private int velocity = 100; // !!! this should be obtained from Player
    private int rootFret = -1;
    private int mouseString = -1; // the string the mouse pointer is nearest
    private JLabel chordLabel;
    private PlayingStyle style;
    private List<PlayingStyle> styles = new java.util.ArrayList<PlayingStyle>();
    private Player player;

    public NeckController(StrungNeck aNeck, Player aPlayer) {
        super(aNeck);
        player = aPlayer;
		add(new ChordStyle());
        add(new ModeStyle());
        style = styles.get(0);
        addMouseWheelListener(this); // for strumming
        addMouseListener(new NeckMouseInputListener()); // for buttons
        addMouseMotionListener(new NeckMouseMotionListener()); // for position
//        Log.debug("Mouse has "+MouseInfo.getNumberOfButtons()+" buttons");
		addKeyListener(new NeckKeyListener()); // for keys
        setFocusable(true);
		chordLabel = new JLabel();
        chordLabel.setFont(chordLabel.getFont().deriveFont(16f));
        getKey().addObserver(this); // observe key/scale changes
        updateDiatonics();
    }

    public void update(Observable o, Object arg) {
        if ( getKey().equals(o) ) {
            updateDiatonics();
        }
    }

    public void setTuning(StringTuning tuning) {
        getNeck().setTuning(tuning);
        updateDiatonics();
    }

    public void updateDiatonics() {
        for ( StrungNeck.TunedString string : getNeck().getStrings() ) {
            int mask = 0;
            int open = string.getOpenTuning();
            for (int f = 0; f <= getFrets(); f++ ) {
				if ( getKey().contains(open+f) ) {
                    mask |= 1<<f;
                }
            }
            string.diatonics(mask);
        }
        refinger();
        repaint();
    }

    /**
     * Something has happened which may make the current fingering invalid
     * so refinger at the last known fretboard position.
     */
    protected boolean refinger() {
        int fret = rootFret;
        int string = mouseString;
        rootFret = -1;
        return refinger(fret, string);
    }

    protected boolean refinger(MouseEvent e) {
		return refinger(fret(e.getX()), string(e.getY()));
    }

    protected boolean refinger(int fret, int string) {
        if ( fret == rootFret && string == mouseString ) return false; // fast return if no change
        boolean ret = style.reRoot(fret, string);
        if ( ret ) {
	        rootFret = fret;
    	    mouseString = string;
        	repaint();
        }
		return ret;
    }

    public void useStyle(String name) {
        for ( PlayingStyle s : getStyles() ) {
            if ( s.getName().equals(name) ) {
                style = s;
                refinger();
                repaint();
                return;
            }
        }
    }

    protected void add(PlayingStyle s) {
        styles.add(s);
    }

    protected List<PlayingStyle> getStyles() { return styles; }

    protected PlayingStyle getStyle() { return style; }

    public Key getKey() { return player.getKey(); }

    public int getVelocity() { return player.getVelocity(); }

    /**
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        style.mouseWheelMoved(e);
    }

    public JLabel getChordLabel() {
        return chordLabel;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        style.paintFingering(g);
    }

    protected void paintFingerPosition(Graphics g, int fret, int string, int label) {
        g.fillOval(xfret(fret) - 24, ystring(string)-12, 24, 24);
	   	g.setColor(Color.black);
        if ( fret > 0  && label >= 0 ) {
	        g.drawString(String.valueOf(label), xfret(fret)-16, ystring(string)+4);
        }
    }

    protected void paintMute(Graphics g, int fret, int string) {
        g.setColor(Color.red);
        int x = xfret(fret);
        int y = ystring(string);
        int SZ = 10;
        g.drawLine(x-SZ, y-SZ, x+SZ, y+SZ);
        g.drawLine(x-SZ, y+SZ, x+SZ, y-SZ);
    }

    protected void paintFrets(Graphics g) {
//        Rectangle drawRect = g.getClipBounds();
		// paint the non-diatonic notes before paiting the frets and inlays
        for ( int s = 0; s < getStringCount(); s++ ) {
            StrungNeck.TunedString string = getString(s);
	        for ( int f = 1 ; f <= getFrets(); f++ ) {
        		if ( !string.diatonic(f) ) {
		            // darken the string fret-gap if it's not diatonic
    	            g.setColor(s == style.getRootString() ? Color.darkGray.brighter().brighter() : Color.lightGray);
           	    	g.fillRect(xfret(f-1)+1, ystring(s)-(getPixelsPerString()/2), xfret(f)-xfret(f-1)-1, getPixelsPerString());
/*       			} else {
			        g.setColor(Color.darkGray.brighter());
        			g.drawString(String.valueOf(getKey().degree(root)+1), xfret(f-1)+10, ystring(s)-1); */
       			}
           	}
        }
        super.paintFrets(g);
    }

    public class PlayingStyle
    {
        private String name;
        protected boolean releasing = false; // we'll pick but not mute on release
        public PlayingStyle(String aName) { name = aName; }
        public String getName() { return name; }
		public boolean reRoot(int fret, int string) { return false; }
    	public void keyPressed(KeyEvent e) { }
    	public void keyReleased(KeyEvent e) { }
        public void mouseWheelMoved(MouseWheelEvent e) { }
        public void paintFingering(Graphics g) { }
        public int getRootString() { return -1; }
    }

    private class ChordStyle extends PlayingStyle
    {
        private ChordFamily chordFamily;
        private ChordShape currentShape;
        private int prevStr = -1;
    	private long prevWhen = 0L;
		private boolean useSus4 = false;
        private boolean use6 = false;
        private boolean add9 = false;

        public ChordStyle() {
            super("Chords");
            setChordFamily(ChordFamily.named("E"));
            currentShape = chordFamily.get("maj"); //
        }

        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if ( code >= KeyEvent.VK_1 && code < KeyEvent.VK_1+getStringCount() ) {
                pickString(code - KeyEvent.VK_1, false, getVelocity()); // !!
                return;
            }
            // QWERTY to provide individual string bending
            switch ( code ) {
            case KeyEvent.VK_Q: setBending(0, true); break;
            case KeyEvent.VK_W: setBending(1, true); break;
            case KeyEvent.VK_E: setBending(2, true); break;
            case KeyEvent.VK_R: setBending(3, true); break;
            case KeyEvent.VK_T: setBending(4, true); break;
            case KeyEvent.VK_Y: setBending(5, true); break;
            case KeyEvent.VK_U: setBending(6, true); break;
            case KeyEvent.VK_I: setBending(7, true); break;
            case KeyEvent.VK_O: setBending(8, true); break;
            case KeyEvent.VK_P: setBending(9, true); break;
            case KeyEvent.VK_CONTROL: useSus4 = true; reRoot(rootFret, getRootString()); break;
            case KeyEvent.VK_WINDOWS: add9 = true; reRoot(rootFret, getRootString()); break;
            case KeyEvent.VK_ALT: use6 = true; reRoot(rootFret, getRootString()); break;
            // space for F5 through F8 other chord families with different root strings
            case KeyEvent.VK_F9: setChordFamily(ChordFamily.named("C")); break;
            case KeyEvent.VK_F10: setChordFamily(ChordFamily.named("A")); break;
            case KeyEvent.VK_F11: setChordFamily(ChordFamily.named("G")); break;
            case KeyEvent.VK_F12: setChordFamily(ChordFamily.named("E")); break;
            default: System.out.println("NeckController: Pressed "+e.paramString()); return;
            }
            e.consume();
        }

        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();
            if ( code >= KeyEvent.VK_1 && code < KeyEvent.VK_1+getStringCount() && releasing ) {
                muteString(code - KeyEvent.VK_1);
                return;
            }
            switch ( code ) {
            case KeyEvent.VK_Q: setBending(0, false); break;
            case KeyEvent.VK_W: setBending(1, false); break;
            case KeyEvent.VK_E: setBending(2, false); break;
            case KeyEvent.VK_R: setBending(3, false); break;
            case KeyEvent.VK_T: setBending(4, false); break;
            case KeyEvent.VK_Y: setBending(5, false); break;
            case KeyEvent.VK_U: setBending(6, false); break;
            case KeyEvent.VK_I: setBending(7, false); break;
            case KeyEvent.VK_O: setBending(8, false); break;
            case KeyEvent.VK_P: setBending(9, false); break;
            case KeyEvent.VK_CONTROL: useSus4 = false; reRoot(rootFret, getRootString()); break;
            case KeyEvent.VK_WINDOWS: add9 = false; reRoot(rootFret, getRootString()); break;
            case KeyEvent.VK_ALT: use6 = false; reRoot(rootFret, getRootString()); break;
            default: return;
            }
            e.consume();
        }

        public int getFirstShapeString() { return getNeck().getTuning().isLow() ? 1 : 0; }

	    public int getRootString() { return chordFamily.getRootString()+getFirstShapeString(); }

        protected void setChordFamily(ChordFamily family) {
            chordFamily = family;
            reRoot(rootFret, getRootString());
            repaint();
        }

    	// triad on two lowest strings, then 7ths, 9ths, 11ths, 13ths
    	private int decodePoly(int string) {
        	return string < 2 ? 3 : string+1;
    	}

    	private int[] addInterval(int[] in, int addInt) {
            int[] out = new int[in.length+1];
			int i = 0, j = 0;
			while ( i < in.length ) {
                if ( i == j && in[i] > addInt ) {
                    out[j++] = addInt;
                }
				out[j++] = in[i++];
            }
            if ( i == j ) {
                out[out.length-1] = addInt;
            }
            return out; // !!!
    	}

    	/**
         * If sus4, 6 or add9 modifiers are requested, modify the intervals.
         */
    	private int[] modifyIntervals(int[] in, int degree) {
            Scale scale = getKey().getScale();
            int[] ret;
           	// !! can only do sus4 if 4 is in the chord scale at this degree
            // and 5, to avoid dimsus4 etc.
        	if ( useSus4
                 && scale.hasInterval(degree, Interval.PERFECT_FOURTH)
                 && scale.hasInterval(degree, Interval.PERFECT_FIFTH) ) {
	        	// sus4, delete the third, replace with 4
    	        for ( int i = 0; i < in.length; i++ ) {
        	        if ( in[i] == Interval.MAJOR_THIRD ||
            	         in[i] == Interval.MINOR_THIRD ) {
                	    in[i] = Interval.PERFECT_FOURTH;
	                }
    	        }
        	}
   			ret = in;
            if ( use6 &&
    			scale.hasInterval(degree, Interval.MAJOR_SIXTH) &&
                scale.hasInterval(degree, Interval.PERFECT_FIFTH) ) {
                ret = addInterval(ret, Interval.MAJOR_SIXTH);
            }
            if ( add9 &&
    			scale.hasInterval(degree, Interval.MAJOR_SECOND) &&
                scale.hasInterval(degree, Interval.PERFECT_FIFTH) ) {
                ret = addInterval(ret, Interval.MAJOR_SECOND);
            }
            return ret;
    	}

		/**
    	 * @return true if root was changed, false otherwise
	     */
		public boolean reRoot(int fret, int string) {
        	// now we have the fret of the new chord root
	        // we (should) know which string has the root
    	    // we can determine the root note from the neck's string
        	int root = getString(getRootString()).getOpenTuning()+fret;
	        // and hence force a diatonic root (a scale degree)
    	    // probably by just not moving to non-diatonic notes
        	if ( !getKey().contains(root) ) return false;
    	    // now pick an appropriate diatonic chord for the degree !!
        	int index = getKey().index(root);
	        int poly = decodePoly(string);
	        int[] chordMode = getKey().getScale().getChordMode(index);
    	    ChordShape shape = null;
	        int[] intervals = ChordMode.getIntervals(chordMode, poly, ChordMode.TERTIAN);
//	        System.out.println(Interval.spell(chordMode)+": "+Interval.spell(intervals));
	        // if sus4, 6 or add9 modifiers are active modify the intervals here
	        intervals = modifyIntervals(intervals, index);
	        Chord chord = Chords.withIntervals(intervals);
	        if ( chord != null ) {
	        	shape = chordFamily.get(chord.getSymbol());
	        	if ( shape != null && !shape.isValidAt(fret) ) {
	        		// retry with 'other' family on same root string
	        		shape = chordFamily.other().get(chord.getSymbol());
	        		if ( shape != null && !shape.isValidAt(fret) ) {
	        			shape = null;
//	        			System.out.print("!!");
	        		}
	        	}
//	        	System.out.print("["+string+"/"+poly+"]"+chord.getSymbol()+" ");
	        }
//        	System.out.println();
			if ( shape == null || chord == null ) return false;
	        rootFret = fret;
			shape(rootFret, shape, getFirstShapeString()); // !!
    	    setChordShape(shape);
   	        getChordLabel().setText(Pitch.className(root)+" "+chord.getSymbol());
            getChordLabel().setForeground(shape == null ? Color.red : Color.blue);

	        return true;
    	}

		/**
	     * Each notch is string pluck
    	 * notch polarity gives strum direction
	     * remember previous string and move accordingly
    	 * Reset string index if 1st delta > N
	     * reset according to direction
    	 */
		public void mouseWheelMoved(MouseWheelEvent e) {
        	int notches = e.getWheelRotation();
//        int velocity = (getHeight() - e.getY()) * 127 / getHeight();
    	    long when = e.getWhen();
        	long delta = when - prevWhen;
	        boolean up = notches > 0;
    	    prevWhen = when;
        	if ( delta > 250 ) {
	            repaint();
    	        prevStr = up ? getStringCount() : getFirstShapeString()-1;
        	}
//        barre(fret(e.getX()));
			refinger(e);
        	int str;
        	boolean picked = false;
        	while ( !picked ) {
	            str = prevStr - notches;
		        if ( str >= getFirstShapeString() && str < getStringCount() ) {
					picked = pickString(str, up, getVelocity());
    	    	} else {
        	    	picked = true; // it isn't really, but avoid an indefinite loop
        		}
	        	prevStr = str;
	        }
    	}

        protected ChordShape getChordShape() { return currentShape; }

        protected void setChordShape(ChordShape chordShape) { currentShape = chordShape; }

        public void paintFingering(Graphics g) {
            if ( getChordShape() == null ) return;
            // paint the fingering on top of everything else
            int last = getStringCount()-getFirstShapeString();
            for ( int i = 0; i < last; i++ ) {
                ChordShape.Fretting f = getChordShape().getFretting(i);
                int fret ;
    	    	if ( f.finger == -1 ) {
           			fret = f.fret; // 'open'ish, fretted relative to nut
    			} else {
       		        fret = rootFret+f.fret; // fretted relative to shape
    			}
      	    	g.setColor(Color.white);
                if ( fret >= 0 ) {
                    paintFingerPosition(g, fret, i+getFirstShapeString(), f.finger);
           		} else if ( fret < 0 ) {
            		g.setColor(Color.red);
            		paintMute(g, 0, i+getFirstShapeString());
           		}
            }
        }
    }

    private class ModeStyle extends PlayingStyle
    {
        private int[] fingerFret = new int[10];
        private int lastPressCode;

        public ModeStyle() { super("Modes"); }

        public void keyPressed(KeyEvent e) {
            // hold and pick
            int code = e.getKeyCode();
            if ( code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9 ) {
                lastPressCode = code;
                int finger = code - KeyEvent.VK_1;
                mute(); // stop other strings playing
                getString(mouseString).hold(fingerFret[finger]);
                pickString(mouseString, false, getVelocity());
                return;
            }
        }

        public void keyReleased(KeyEvent e) {
            // hold and pick
            int code = e.getKeyCode();
            if ( code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9 ) {
//                int finger = code - KeyEvent.VK_1;
//                mute(); // stop other strings playing
//                getString(mouseString).hold(fingerFret[finger]);
				// avoid muting after a new note has been pressed
                // what does this do if released on a diff string to pressed? !!
				if ( code == lastPressCode && releasing ) {
                	muteString(mouseString);
                }
                return;
            }
        }

		/**
    	 * @return true if root was changed, false otherwise
	     */
		public boolean reRoot(int fret, int str) {
            StrungNeck.TunedString string = getString(str);
        	if ( !string.diatonic(fret) ) return false;
            int finger = 0;
            fingerFret[finger++] = fret;
            // work out the finger intervals from this note
            while ( ++fret <= getFrets() && finger < 9 ) { // next fret
                if ( !string.diatonic(fret) ) continue;
                fingerFret[finger++] = fret; // next finger
            }
            repaint();
            return true;
        }

        public void paintFingering(Graphics g) {
            // piant the finger intervals
            for ( int i = 0; i < 4; i++ ) {
	            g.setColor(Color.white);
                if ( fingerFret[i] > getFrets() ) break;
            	paintFingerPosition(g, fingerFret[i], mouseString, i+1);
            }
   	        getChordLabel().setText(" "); // blank any previous text
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
        }
    }

    private class NeckMouseInputListener extends MouseInputAdapter implements MouseInputListener
    {
        private boolean in = false;

		public void mouseExited(MouseEvent e) {
            if ( in ) {
                in = false;
                mute();
            }
		}

        public void mouseEntered(MouseEvent e) {
            if ( !in ) {
	        	requestFocusInWindow();
                in = true;
            }
        }

        public void mousePressed(MouseEvent e) {
            switch ( e.getButton() ) {
            case 1: break;
            case 2: break; // !! ignore wheel button
            case 3: break;//
            default: break;
            }
        }

        public void mouseReleased(MouseEvent e) {
            switch ( e.getButton() ) {
            case 1: break;
            case 2: break; // !! ignore wheel button
            case 3: break;
            }
        }
    }

    private class NeckMouseMotionListener extends MouseMotionAdapter implements MouseMotionListener
    {
        private boolean wasBending = false;
        private int bendx;

        public void mouseMoved(MouseEvent e) { moved(e); }

        public void mouseDragged(MouseEvent e) { moved(e); }

        private void moved(MouseEvent e) {
            if ( !getNeck().isBending() ) {
	            refinger(e);
                if ( wasBending ) {
                    getNeck().bend(0); // reset bend amount
//	                Log.debug("Bend cancelled for "+bendable);
                	wasBending = false;
                }
            } else if ( !wasBending ) {
                // start bend
                bendx = e.getX();
                wasBending = true;
//                Log.debug("Bend started for "+bendable);
            } else {
                // continue bend
                getNeck().bend(e.getX()-bendx);
//                System.out.print("B");
            }
        }
    }

    private class NeckKeyListener extends KeyAdapter implements KeyListener
    {
        private int lastKey = -1;

        public void keyPressed(KeyEvent e) {
            if ( e.getKeyCode() == lastKey ) return; // ignore auto-repeats (except Alt-Gr !!!)
            switch ( e.getKeyCode() ) {
            case KeyEvent.VK_F1: useStyle("Modes");break;
            case KeyEvent.VK_F2: useStyle("Chords"); break;
            case KeyEvent.VK_SHIFT: break;
            default: style.keyPressed(e); break;
            }
            lastKey = e.getKeyCode();
        }

        public void keyReleased(KeyEvent e) {
            if ( e.getKeyCode() == lastKey ) lastKey = -1;
            switch ( e.getKeyCode() ) {
            case KeyEvent.VK_F1: break;
            case KeyEvent.VK_F2: break;
            case KeyEvent.VK_SHIFT: break;
            default: style.keyReleased(e); break;
            }
        }
    }
}
