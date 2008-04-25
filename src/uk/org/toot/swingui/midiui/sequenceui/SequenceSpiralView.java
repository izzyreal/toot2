/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.swingui.midiui.sequenceui;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import uk.org.toot.midi.sequence.MidiTrack;
import uk.org.toot.midi.sequence.SequencePosition;
import uk.org.toot.midi.sequence.MidiNote;
import uk.org.toot.swingui.midiui.MidiColor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import java.awt.Point;

import static uk.org.toot.midi.message.MetaMsg.*;
import static uk.org.toot.midi.message.NoteMsg.*;

public class SequenceSpiralView extends SequenceView
{
    private final static int MAXBEATS = 32;
    private int QDIV = 16; 		// division per quarter note (beat)
    private int nBeats; 		// derived from exponential decay
    private int xoffset;
    private int yoffset;
    private int resolution;
	private long startTick = 0;
    private double[] rBeat = new double[MAXBEATS];
    private double[] sinTheta = new double[QDIV];
    private double[] cosTheta = new double[QDIV];
    private double beatDecay;
    private double qdivDecay;	// QDIVth root of beatDecay
    private BasicStroke stroke = new BasicStroke(3);

    public SequenceSpiralView(Viewer viewer) {
        super(viewer, false);
        resolution = getSequence().getResolution();
        initTrig();
    }

    private void initTrig() {
        for ( int i = 0; i < QDIV; i++ ) {
			double theta = 2*Math.PI*i/QDIV;
            sinTheta[i] = Math.sin(theta);
            cosTheta[i] = Math.cos(theta);
        }
	}

    public int getTolerance() { return 1; /*(int)(resolution/64);*/ }

    protected boolean isValid(MidiTrack track) {
        return true; // we can display any track
    }

    private void translate(Graphics g) {
        xoffset = getWidth()/2;
        yoffset = getHeight()/2;
	    g.translate(xoffset, yoffset);
    }

    protected long tick(int x, int y) {
        x -= xoffset;
        y -= yoffset;
        // low bits from angle
        double theta = Math.atan2(y, x) - Math.PI/2;
        double low = theta * resolution / (2*Math.PI);
        // high bits from radius
        double f = 1.0 - ((1.0 - (rBeat[1] / rBeat[0])) * (low / resolution)); // !!!
        double r = Math.sqrt(x*x + y*y);
        if ( r > rBeat[0] ) return -1L;
        int b;
        for ( b = nBeats; b >= 0; b-- ) {
            if ( r < (rBeat[b] * f) ) {
//                System.out.println(r+" > "+rBeat[b]+" :"+b);
                break;
            }
        }
        return (long)((b * resolution) + low + startTick);
    }

    /**
     * derived from editor context, NOT dependent on x or y.
     */
    protected int note(int x, int y) {
        return -1; // !!! DERIVE FROM EditBar !!!
        // return getEditor().getNoteValue(); NO, multi bars per editor !!!
    }

    public String noteName(int note) {
        return "---";
    }

    protected void scrollToVisible(long tick) {
        setStartTick(tick);
    }

    public void setStartTick(long tick) {
        startTick = tick;
        repaint();
    }

    public long getStartTick() {
        return startTick;
    }

    protected void paintTrack(Graphics g, MidiTrack track) {
        // find the region of ticks
        // find the start of the region
        if ( startTick < 0 ) startTick = 0; // clamp
        long endTick = startTick + resolution * nBeats;

        int satDiv =  getSelection().getTrack(track).size() > 0 ? 3 : 1;
        Float fhue = (Float)track.getClientProperty("Hue");
        // for each event in this region
        for ( int i = 0 ; i < track.size() ; i++ )
        {
            MidiEvent event = track.get(i) ;
            if ( event.getTick() < startTick ) continue ; // before tick region
            if ( event.getTick() > endTick ) break ;      // after tick region

           	long onTick = event.getTick() ;
            MidiMessage m = event.getMessage() ;

            if ( isMeta(m) ) {
                switch ( getType(m) ) {
                case MARKER: // marker
                case CUE_POINT: // cue point
                case TIME_SIGNATURE: // time signature
                	paintMeta(g, getString(m), onTick) ;
                }
            } else if ( isNote(m) ) {
    	        if ( isOn(m) ) {
        		    int note = getPitch(m) ;
        			int velocity = getVelocity(m);
    	    		// draw the note
                    if ( fhue != null ) {
				        float hue = fhue.floatValue();
    	                Color color = MidiColor.asHSB(hue, saturation(velocity)/satDiv, 1.0f);
				        g.setColor(color);
    				} else {
        				g.setColor(Color.white);
    				}
	    	        paintNote(g, note, velocity, onTick, -1, false);
    			}
            }
        }
    }

    protected void paintGrid(Graphics g) {
        // paint the axial lines
        double r0 = radius(-1);
        double r = r0;
        int x, y;
        double theta;
        Color color;
        String label;
        // triplet based axial lines
        g.setColor(Color.lightGray);
        for ( int i = 0; i < 12; i++ ) {
			theta = 2*Math.PI*i/12;
            x = (int)(-r*Math.sin(theta));
            y = (int)(r*Math.cos(theta));
            g.drawLine(0, 0, x, y);
        }
        // 1/4 note /16, i.e 1/64 notes
        for ( int i = 0; i < QDIV; i++ ) {
            x = (int)(-r*sinTheta[i]);
            y = (int)(r*cosTheta[i]);
            if ( i == 0 ) {					// 1/4
                color = MidiColor.BEAT.darker();
                label = "4";
            } else if ( (i % (QDIV/2)) == 0 ) {	// 1/8ths
                color = MidiColor.SIXTEENTH;
                label = "8";
            } else if ( (i % (QDIV/4)) == 0 ) {	// 1/16ths
                color = MidiColor.SIXTEENTH.darker();
                label = "16";
            } else if ( (i % (QDIV/8)) == 0 ) {	// 1/32nds
                color = MidiColor.SIXTYFOURTH.darker();
                label = "32";
            } else {
                color = MidiColor.SIXTYFOURTH;
                label = "64";
            }

            g.setColor(color);
            g.drawLine(0, 0, x, y);
            g.setColor(Color.black);
            g.drawString(label, x-10, y+10);
        }
		// the exponential decay factor of 1 beat
        beatDecay = radius(resolution) / radius(0);
        // the QDIVth root of the exponential decay factor of 1 beat
        qdivDecay = Math.pow(beatDecay, 1.0/QDIV);
        // paint the spiral lines
		SequencePosition p ;
        long tick;
        int beat = 0;
        Iterator posIter = positionIterator(startTick);
        // we only get beat positions, interposlate if necessary
//        System.out.println(r0+" "+qdivDecay);
   	    while ( posIter.hasNext() && beat < MAXBEATS ) {
       	    p = (SequencePosition)posIter.next();
	        tick = getSequence().getTick(p) - startTick;
            r = radius(tick);
            rBeat[beat] = r;
            if ( r < 50 ) break;
//            System.out.println(beat+": "+r);
            if ( r > r0 ) continue;
			color = (p.beat == 0) ? MidiColor.BAR : MidiColor.BEAT;
            drawHelix(g, color, r);
            beat++;
        }
   		nBeats = --beat;
    }

    protected void drawHelix(Graphics g, Color color, double radius0) {
//        System.out.print("Helix ");
        double r = radius0;
        int x, y;
        int xprev = 0, yprev = 0;
        g.setColor(color);
        for ( int i = 0; i <= QDIV; i++ ) {
            r *= qdivDecay;
            if ( r > (rBeat[0] + 1) ) continue;
            x = (int)(-r*sinTheta[i%QDIV]);
   	        y = (int)(r*cosTheta[i%QDIV]);
            if ( i > 0 && r > 50 ) {
//                System.out.print(i+", ");
   	        	g.drawLine(xprev, yprev, x, y);
            }
            xprev = x;
            yprev = y;
        }
//        System.out.println();
    }

    protected void paintComponent(Graphics g) {
        translate(g);
        super.paintComponent(g);
    }

    protected double theta(long tick) {
		tick %= resolution;
        return (2*Math.PI*tick)/resolution;
    }

    protected double radius(long tick) {
        int r0 = (int)(0.9 * getHeight() / 2);
        if ( tick == -1 ) {
			return r0;
        }
        // perspective should be 1 at reference tick
        // falling to 0 at infinity tick
        double decay = Math.exp(-tick * 0.1 / resolution);
//        System.out.println(tick+": "+decay);
        return r0 * decay;
    }

    protected void paintNote(Graphics g, int note, int velocity, long onTick, long offTick, boolean bOutline) {
        onTick -= startTick;
        if ( onTick < 0 ) return; // too early
        Graphics2D g2 = (Graphics2D)g;
        double theta = theta(onTick);
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double r = radius(onTick) - 1;
        int x = (int)(-r*sinTheta);
		int y = (int)(r*cosTheta);
        double r2 = 4 + radius(onTick+resolution); // towards the next beat
        if ( r2 > (r - 1) ) return; // too late (too small)
        int x2 = (int)(-r2*sinTheta);
		int y2 = (int)(r2*cosTheta);
        g2.setStroke(stroke);
        g2.drawLine(x, y, x2, y2);
//        System.out.println(x+", "+y+"   "+x2+", "+y2);
    }

    protected void paintMeta(Graphics g, String str, long tick) {
        tick -= startTick;
        double theta = theta(tick);
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double r = radius(tick);
        int x = (int)(-r*sinTheta);
		int y = (int)(r*cosTheta);
        g.setColor(metaColor) ;
        g.drawString(str, x+3, y) ;
    }

    private void drawSelectionLines(Graphics g, int x1, int y1, int x2, int y2) {
        // SHOULD avoid quantised ticks to get points 3 and 4 !!!
        // because 1 and 2 are NOT quantised
        // can it be done geometrically with just points 1 and 2 ??? YES
        // from 1 drawLine to 3
        // from 3 drawHelix to 2 - DOH BACKWARDS
        // from 2 drawLine to 4
        // from 4 drawHelix to 1
		// NO - need to draw helix to find 3 and 4
        // OR - quantise all MAINTAINING thetas (but not like rect!!!)
        // !!! should we arrange the points to simplify things? like rect?
        // logically we can drag from any logical point to diagonal point
        // meaning points may be arbitrarily arranged
        // we could insist on dragging order but why burden the user
        // how are the 4 logical points named
        // visually outer/inner distinction is easy
        // YIKES, we can only draw the helix forward in time currently
        // which makes the cycle impossible to complete. feck.
        // yep, quantising all is starting to look easier :(
        uk.org.toot.midi.sequence.MidiSequence seq = getSequence();

		// 2 x, y -> tick
        long tick1 = tick(x1, y1);
        long tick2 = tick(x2, y2);
        // 2 tick -> x, y	(QUANTISE x, y)
        double radius1 = radius(tick1);
        double theta1 = theta(tick1);
        x1 = (int)(-radius1*Math.sin(theta1)) + xoffset;
		y1 = (int)(radius1*Math.cos(theta1)) + yoffset;
        double radius2 = radius(tick2);
        double theta2 = theta(tick2); // ensure radial
        x2 = (int)(-radius2*Math.sin(theta2)) + xoffset;
		y2 = (int)(radius2*Math.cos(theta2)) + yoffset;
        // 2 tick -> position
        SequencePosition pos1 = seq.getPosition(tick1);
        SequencePosition pos2 = seq.getPosition(tick2);
        // twiddle positions (swap bar.beat)
        int tmp = pos1.bar; pos1.bar = pos2.bar; pos2.bar = tmp;
        tmp = pos1.beat; pos1.beat = pos2.beat; pos2.beat = tmp;
        // 2 position -> tick
        long tick3 = seq.getTick(pos1) - startTick;
        long tick4 = seq.getTick(pos2) - startTick;
        // 2 tick -> x, y
        double radius3 = radius(tick3);
        double theta3 = theta1; // ensure radial
        int x3 = (int)(-radius3*Math.sin(theta3)) + xoffset;
		int y3 = (int)(radius3*Math.cos(theta3)) + yoffset;
        double radius4 = radius(tick4);
        double theta4 = theta2; // ensure radial
        int x4 = (int)(-radius4*Math.sin(theta4)) + xoffset;
		int y4 = (int)(radius4*Math.cos(theta4)) + yoffset;
        // now have 4 points
        // draw radial lines
        g.drawLine(x1, y1, x3, y3);
        g.drawLine(x2, y2, x4, y4);
        // draw helical lines (inner for now)
	}

    /**
     * overridden for geometric purposes
     */
    public Point drawSelectionArea(int x1, int y1, int x2, int y2, Point drag) {
        Graphics g = getGraphics();
        g.setColor(Color.white);
        g.setXORMode(Color.black);
        if ( tick(x2, y2) > startTick ) {
	        // first erase old
	        drawSelectionLines(g, x1, y1, drag.x, drag.y);
    	    // then draw new
        	drag.x = x2;
	        drag.y = y2;
    	    drawSelectionLines(g, x1, y1, drag.x, drag.y);
    	}
        // remember to dispose
        g.dispose();
        return drag;
    }

/**
     * Overridden for consideration of On tick only, Off tick ignored.
     * if value is -1, match nearest note at the specified tick
     */
    public MidiNote match(MidiTrack t, long tick, int value, boolean bvelocity) {
        long tik;
        for (int i = 0; i < t.size(); i++) {
            MidiEvent ev = t.get(i);
			tik = ev.getTick();
            if (tik < (tick - getTolerance())) continue; // too early
            if (tik > (tick + getTolerance())) break; // too late
            MidiMessage msg = ev.getMessage();
            if (isNote(msg)) {
                if (isOn(msg)) {
                    int note = getPitch(msg);
                    int velocity = getVelocity(msg);
                    if (value != -1 && value != (bvelocity ? velocity : note)) {
                        continue; // not matched
                    }
                    // find the matching off note
                    for (int j = 1 + i; j < t.size(); j++) {
                        MidiEvent event = t.get(j);
                        MidiMessage m = event.getMessage();
                        if (!(isNote(m))) continue;
//                        if ( !isOn(m) && !isOff(m)) continue; this csn't hsppen when isNote
                        if (getPitch(m) != note) continue;
                        // now we have a matching note off
//                        if (event.getTick() < (tick - getTolerance())) break; // reject note on too
                        //	                offTick = event.getTick() ;
                        return new MidiNote(ev, event); // match
                    }
                }
            }
        }
        return null; // no match
    }

    public List<MidiNote> getMatches(int trk, int x1, int y1, int x2, int y2) {
        List<MidiNote> matches = new ArrayList<MidiNote>();
        // !!! UNIFY code with selectionMoved()
        // iterate over beats in range
        	// getMatches from track between ticks for each beat
			//matches.addAll(super.getMatches( , 0, , 127));
        return matches;
    }
}
