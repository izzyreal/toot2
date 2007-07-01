/* Generated by Together */

package uk.org.toot.pitch;

/**
 * An immutable Chord. No mutators.
 */
public class Chord
{
    private String symbol;		// e.g. maj7
    private String spelling;	// e.g. 1 3 5 7
    private String name;		// e.g. major seventh
    private int[]  intervals; 	// derived from spelling

    public Chord(String aSymbol, String aSpelling, String aName) {
        symbol = aSymbol;
        spelling = aSpelling;
        name = aName;
        decodeIntervals(spelling);
    }

    void decodeIntervals(String aSpelling) {
		String[] degrees = aSpelling.split("\\s");
        intervals = new int[degrees.length];
	    for (int i = 0; i < intervals.length; i++) {
        	intervals[i] = Interval.spelt(degrees[i]);
        }
    }

    public String getSymbol() { return symbol; }

    public String getSpelling() { return spelling; }

    public String getName() { return name; }

    public int[] getIntervals() { return intervals; }

    public int getPoly() { return intervals.length; }

    /**
     * Return true if someIntervals exactly matches our intervals.
     * @param someIntervals
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
    	// optimise if no missed intervals allowed 
    	if ( missing == 0 ) {
    		return matchesIntervals(someIntervals) ? new int[0] : null;
    	}
        int misses = 0;
        int[] missingIndices = new int[missing];
        int j = 0;
        for ( int i = 0; i < intervals.length /*&& j < someIntervals.length*/ ; i++ ) {
        	if ( j == someIntervals.length ) return null;
            if ( intervals[i] != someIntervals[j] ) {
            	// can't miss unison or highest interval
            	if ( i == 0 || i == intervals.length-1 ) {
            		return null; // fast false return
            	}
            	if ( misses == missingIndices.length ) {
            		return null;
            	}
            	missingIndices[misses++] = i;
            } else {
            	j += 1; // interval matched
            }
        }
        if ( j != someIntervals.length ) return null;
        if ( misses != missing ) return null;
/*        System.out.print(getSymbol()+", "+Interval.spell(intervals)+" = "+
        	Interval.spell(someIntervals)+" missing "+misses+"/"+missing+": ");
        for ( int i = 0; i < missingIndices.length; i++ ) {
        	System.out.print(Interval.spell(intervals[missingIndices[i]])+"["+missingIndices[i]+"], ");
        }
        System.out.println(); */
        return missingIndices;
    }
    
    /**
     * Return true if every interval of this chord is contained within
     * the chordMode
     * @param chordMode
     * @return
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
     * A Chord.Voicing is an aggregation of a theoretical root position Chord with
     * all voicing information such as missing intervals and (one day) octave 
     * transpositions of present intervals.
     * It isn't rooted a to a pitch though, it's still just in terms of intervals.
     * @author st
     *
     */
    public static class Voicing
    {
    	private Chord chord;
    	private int[] missingIndices = null;

    	public Voicing(Chord chord) {
    		this.chord = chord;
    	}
    	
    	public Voicing(Chord chord, int[] missingIndices) {
    		this.chord = chord;
    		this.missingIndices = missingIndices;
    	}
    	
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
    	 * @return
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
    	
    	public String toString() {
    		String missingString = "";
    		if ( missingIndices != null ) {
    			int[] intervals = getChord().getIntervals();
    			for ( int i = 0; i < missingIndices.length; i++) {
    				missingString += " no "+Interval.spell(intervals[missingIndices[i]]);
    			}
    		}
    		return getChord().getSymbol()+missingString; // !!! (no 5) etc.
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
    	
    	public PitchedVoicing(Voicing voicing, int root) {
    		this.voicing = voicing;
    		this.root = root;
    	}
    	
    	public Voicing getVoicing() {
    		return voicing;
    	}
    	
    	public int getRoot() {
    		return root;
    	}
    	
    	public Chord getChord() {
    		return getVoicing().getChord();
    	}
    	
    	public int[] getNotes() {
    		int[] intervals = getVoicing().getIntervals(); 
    		int[] notes = new int[intervals.length];
    		for ( int i = 0; i < notes.length; i++ ) {
    			notes[i] = root + intervals[i];
    		}
    		return notes;
    		
    	}
    	
    	public String toString() {
    		return PitchClass.name(root)+getVoicing().toString(); // !!! !!! TODO
    	}    	
    }    
}
