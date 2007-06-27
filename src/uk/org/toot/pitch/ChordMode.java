package uk.org.toot.pitch;

/**
 * A Chord Mode is the Mode derived from a particular degree of a Scale.
 * So called because it 'contains' any diatonic chord from
 * that degree of a Scale.
 * Because it is just an array of ints this class cannot be instantiated.
 * Static methods are provided to operate on the int arrays of the Chord Mode.
 * 
 * Constants SECUNDAL, TERTIAN and QUARTAL are provided for typical
 * harmonic construction.
 * @author st
 *
 */
public class ChordMode 
{
	public static final int SECUNDAL = 2;
	public static final int TERTIAN = 3;
	public static final int QUARTAL = 4;

	private ChordMode() {
		// prevent instantation
	}
	
	/**
	 * Return the interval of the degree of the chord mode
	 * @param chordMode the intevals of the chord mode
	 * @param degree
	 * @return
	 */
    public static int interval(int[] chordMode, int degree) { 
    	return chordMode[degree % chordMode.length] ; 
    }

    /**
     * Return the interval from degree1 to degree2 of the chord mode
     * @param chordMode the intevals of the chord mode
     * @param degree1
     * @param degree2
     * @return
     */
    public static int interval(int[] chordMode, int degree1, int degree2) {
    	return Interval.from(interval(chordMode, degree1), interval(chordMode, degree2));
    }

    /**
     * 
     * @param chordMode the intervals of the chord mode
     * @param poly TERTIAN 1..7
     * @param interval SECUNDAL, TERTIAN or QUARTAL
     *  tertian may be two octaves of intervals
     *  so secundal will be less than 2 octaves of intervals
     *  and quartal may be more than 2 octaves of intervals
     * @return
     */
    public static int[] getIntervals(int[] chordMode, int poly, int interval) {
        int[] intervals = new int[poly-1];
        int accum = 0;
        for ( int i = 0; i < poly-1; i++ ) {
            intervals[i] = accum + interval(chordMode, i, i+interval-1);
            accum = intervals[i];
        }
        return intervals;
    }
    
    public static boolean hasInterval(int[] chordMode, int interval) {
    	interval %= 12;
    	for ( int i = 0; i < chordMode.length; i++) {
    		if ( chordMode[i] == interval ) {
    			return true; // fast match
    		}
    	}
    	return false;
    }
}
