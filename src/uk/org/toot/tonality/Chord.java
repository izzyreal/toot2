// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.tonality;

/**
 * An immutable Chord. No mutators.
 */
public class Chord
{
    private String symbol;		// e.g. maj7
    private String spelling;	// e.g. 1 3 5 7
    private String name;		// e.g. major seventh
    private int[]  intervals; 	// derived from spelling, includes UNISON at [0]

    public Chord(String aSymbol, String aSpelling, String aName) {
        symbol = aSymbol;
        spelling = aSpelling;
        name = aName;
        decodeIntervals(spelling);
    }

    void decodeIntervals(String aSpelling) {
		String[] intervalStrings = aSpelling.split("\\s");
        intervals = new int[intervalStrings.length];
	    for (int i = 0; i < intervals.length; i++) {
        	intervals[i] = Interval.spelt(intervalStrings[i]);
        }
    }

    /**
     * Get the symbol.
     * @return the symbol e.g. "maj7"
     */
    public String getSymbol() { return symbol; }

    /**
     * Get the spelling.
     * @return the spelling e.g. "1 3 5 7"
     */
    public String getSpelling() { return spelling; }

    /**
     * Get the name.
     * @return the name e.g. "major seventh"
     */
    public String getName() { return name; }

    /**
     * Get the intervals
     * @return the array of ints representing the intervals
     */
    public int[] getIntervals() { return intervals; }

    /**
     * Get the polyphony
     * @return the number of intervals (including UNISON)
     */
    public int getPoly() { return intervals.length; }

    /**
     * Return true if someIntervals exactly matches our intervals.
     * @param someIntervals the intervals to match to our intervals
     * @return boolean true if exact match, false otherwise
     */   
    public boolean matchesIntervals(int[] someIntervals) {
        if ( intervals.length != someIntervals.length ) return false;
        for ( int i = 0; i < intervals.length; i++ ) {
            if ( intervals[i] != someIntervals[i] ) {
            	return false; // fast false return
            }
        }
        return true; // interval arrays are identical
    }
    
    /**
     * Returns an array of missing interval indices if someIntervals
     * matches our intervals with exactly missing intervals missing.
     * missing should equal 0, 1 or 2.
     * returns null if not that exact number of missing intervals
     * @return int[] the array of missing interval indices or null if no match
     */
    public int[] missingIntervals(int[] someIntervals, int missing) {
    	// optimise for matching a single note
    	if ( someIntervals.length < 2 ) return null;
    	// optimise if no missed intervals allowed 
    	if ( missing == 0 ) {
    		return matchesIntervals(someIntervals) ? new int[0] : null;
    	}
    	// optimise for required number of misses not possible
    	if ( intervals.length != someIntervals.length + missing ) {
    		return null;
    	}
        int misses = 0; // actual missing count
        int[] missingIndices = new int[missing];
        int j = 0; // index into someIntervals
        for ( int i = 0; i < intervals.length; i++ ) {
            if ( j == someIntervals.length || intervals[i] != someIntervals[j] ) {
            	if ( i == 0 ) {
            		return null; // missed root 
            	}            	
            	if ( misses == missingIndices.length ) {
            		return null; // too many misses
            	}
            	missingIndices[misses++] = i;
            } else {
            	j += 1; // interval matched
            }
        }
        if ( j != someIntervals.length ) {
        	return null; // not enough intervals
        }
        
        if ( misses != missing ) {
        	return null; // wrong number of misses (shouldn't occur?)
        }
/*        System.out.print(getSymbol()+", "+Interval.spell(intervals)+" = "+
        	Interval.spell(someIntervals)+" missing "+misses+"/"+missing+": ");
        for ( int i = 0; i < missingIndices.length; i++ ) {
        	int mi = missingIndices[i];
        	System.out.print(Interval.spell(intervals[mi])+"["+mi+"], ");
        }
        System.out.println(); */
        return missingIndices;
    }
    
    /**
     * Return whether every interval of this chord is contained within
     * the chordMode
     * @param chordMode the chord mode to match
     * @return true if every interval of this Chord matches the chord mode
     */
    public boolean matchesChordMode(int[] chordMode) {
    	for ( int i = 0; i < intervals.length; i++ ) {
    		if ( !ChordMode.hasInterval(chordMode, intervals[i])) {
    			return false; // fast failure
    		}
    	}
    	return true;
    }
    
    public String toString() {
    	return getSymbol();
    }
   
    /**
     * A Chord.Voicing is an aggregation of a theoretical root position 
     * (i.e uninverted) Chord with all voicing information such as missing 
     * intervals and (one day) octave transpositions of present intervals.
     * It isn't rooted to a pitch though, it's still just in terms of intervals.
     * @author st
     *
     */
    public static class Voicing
    {
    	private Chord chord;
    	private int[] missingIndices = null;

    	/**
    	 * Construct a new Voicing with no missing intervals.
    	 * @param chord the Chord for this Voicing
    	 */
    	public Voicing(Chord chord) {
    		this.chord = chord;
    	}
    	
    	/**
    	 * Construct a new Voicing with missing intervals.
    	 * @param chord the Chord for this Voicing
    	 * @param missingIndices the array of the indices of the missing intervals
    	 */
    	public Voicing(Chord chord, int[] missingIndices) {
    		this.chord = chord;
    		this.missingIndices = missingIndices;
    	}
    	
    	/**
    	 * Get the Chord that this Voicing uses.
    	 * @return this Chord
    	 */
    	public Chord getChord() {
    		return chord;
    	}
    	
    	/**
    	 * Return an array of the indices of the intervals which are missing.
    	 * @return int[] the array of missing interval indices
    	 */
    	public int[] getMissingIndices() {
    		return missingIndices;
    	}
    	
    	/**
    	 * Return the array of intervals that exist after missing intervals have been
    	 * removed.
    	 * @return the array of intervals excluding missing intervals
    	 */
    	public int[] getIntervals() {
    		int[] allIntervals = getChord().getIntervals();
    		if ( missingIndices == null || missingIndices.length == 0 ) {
    			return allIntervals;
    		}
    		int[] intervals = new int[allIntervals.length - missingIndices.length];
    		for ( int i = 0, j = 0, k = 0; i < allIntervals.length; i++ ) {
    			if ( missingIndices[k] == i ) {
    				k += 1;
    				continue;
    			}
    			intervals[j++] = allIntervals[i];
    		}
    		return intervals;
    	}
    	
    	/**
    	 * Get the string representation of the missing intervals.
    	 * @return the missing notation, e.g. " no 5"
    	 */
    	public String getMissingString() {
    		String missingString = "";
    		if ( missingIndices != null ) {
    			int[] intervals = getChord().getIntervals();
    			for ( int i = 0; i < missingIndices.length; i++) {
    				missingString += " no "+Interval.spell(intervals[missingIndices[i]]);
    			}
    		}
    		return missingString;
    	}
    	
    	public String toString() {
    		return getChord().getSymbol()+getMissingString(); 
    	}    	
    }
    
    /**
     * A PitchedVoicing is an aggregation of a Voicing and a root pitch.
     * @author st
     *
     */
    public static class PitchedVoicing 
    {
    	private Voicing voicing;
    	private int root;
    	private int slashBass = -1;
    	
    	/**
    	 * Construct a new PitchedVoicing of the specified Voicing with the
    	 * specified root pitch.
    	 * @param voicing the Voicing
    	 * @param root the root pitch
    	 */
    	public PitchedVoicing(Voicing voicing, int root) {
    		this.voicing = voicing;
    		this.root = root;
    	}
    	
    	/**
    	 * Construct a new PitchedVoicing of the specified Voicing with the
    	 * specified root pitch and the specified bass pitch as applicable to
    	 * slash chord notation.
    	 * @param voicing the Voicing
    	 * @param root the root pitch
    	 * @param slashBass
    	 */
    	public PitchedVoicing(Voicing voicing, int root, int slashBass) {
    		this(voicing, root);
    		this.slashBass = slashBass;
    	}
    	
    	/**
    	 * Get the Voicing
    	 * @return the Voicing
    	 */
    	public Voicing getVoicing() {
    		return voicing;
    	}
    	
    	/**
    	 * Get the root pitch for the chord voicing
    	 * @return the root pitch
    	 */
    	public int getRoot() {
    		return root;
    	}
    	
    	/**
    	 * Get the Chord for the chord voicing.
    	 * @return the Chord
    	 */
    	public Chord getChord() {
    		return getVoicing().getChord();
    	}
    	
    	/**
    	 * Get the individual pitches for the pitched chord voicing.
    	 * @return an array of ints for the note pitches.
    	 */
    	public int[] getPitches() {
    		int[] intervals = getVoicing().getIntervals(); 
    		int[] notes = new int[intervals.length];
    		for ( int i = 0; i < notes.length; i++ ) {
    			notes[i] = root + intervals[i];
    		}
    		return notes;
    		
    	}
    	
    	/**
    	 * Get the string representation of the slash chord notation
    	 * @return the slash notation, e.g. " / C"
    	 */
    	public String getSlashString() {
    		if ( slashBass < 0 || slashBass == root ) return "";
    		return " / "+Pitch.className(slashBass);
    	}
    	
    	public String toString() {
    		return Pitch.className(root)+getVoicing().toString()+getSlashString();
    	}    	
    }    
}
