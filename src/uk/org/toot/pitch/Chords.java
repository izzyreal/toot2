package uk.org.toot.pitch;

import java.util.Arrays;
import java.util.List;

import uk.org.toot.pitch.Chord;
import static uk.org.toot.pitch.Interval.*;

public class Chords 
{
    private static List<Chord> chords = new java.util.ArrayList<Chord>();
    
    private static Identifier chordIdentifier =
    	new DefaultIdentifier();

    public static void setChordIdentifer(Identifier identifier) {
    	chordIdentifier = identifier;
    }
    
    public static void add(Chord aChord) {
        chords.add(aChord);
    }

    public static void addChord(String aSymbol, String aSpelling, String aName) {
        add(new Chord(aSymbol, aSpelling, aName));
    }

    public static Chord withSymbol(String aSymbol) {
        for ( Chord chord : chords ) {
            if ( chord.getSymbol().equals(aSymbol) ) return chord;
        }
        return null;
    }

    public static Chord withSpelling(String aSpelling) {
        for ( Chord chord : chords ) {
            if ( chord.getSpelling().equals(aSpelling) ) return chord;
        }
        return null;
    }

    public static Chord withName(String aName) {
        for ( Chord chord : chords ) {
            if ( chord.getName().equals(aName) ) return chord;
        }
        return null;
    }

    public static Chord withIntervals(int[] someIntervals) {
        for ( Chord chord : chords ) {
            if ( chord.matchesIntervals(someIntervals) ) return chord;
        }
        return null;
    }

    public static List<Chord.Voicing> withIntervals(int[] someIntervals, int missing) {
    	int[] missingIndices;
    	List<Chord.Voicing> voicings = new java.util.ArrayList<Chord.Voicing>();
        for ( Chord chord : chords ) {
        	missingIndices = chord.missingIntervals(someIntervals, missing); 
            if ( missingIndices != null ) {
            	voicings.add(new Chord.Voicing(chord, missingIndices));
            }
        }
        return voicings;
    }

    public static List<Chord.PitchedVoicing> withNotes(int[] notes) {
    	return chordIdentifier.withNotes(notes);
    }
    
    public static List<Chord> fromChordMode(int[] chordMode) {
    	List<Chord> chordList = new java.util.ArrayList<Chord>();
        for ( Chord chord : chords ) {
        	if ( chord.matchesChordMode(chordMode) ) {
        		chordList.add(chord);
        	}
        }	
    	return chordList;
    }
    
    public static void checkIdentifiability() {
    	List<Chord.PitchedVoicing> matches;
    	Chord.PitchedVoicing pitchedVoicing;
    	int[] notes;
    	boolean found;
        for ( Chord chord : chords ) {
        	pitchedVoicing = new Chord.PitchedVoicing(new Chord.Voicing(chord), PitchClass.value("C"));
        	notes = pitchedVoicing.getNotes();
        	matches = withNotes(notes);
        	found = false;
        	for ( Chord.PitchedVoicing cpv : matches ) {
        		if ( cpv.getChord() == chord ) {
//        			System.out.println(chord.getSymbol()+" identified");
        			found = true;
        			break;
        		}
        	}
        	if ( !found ) {
        		System.out.println("C"+chord.getSymbol()+" not identified");
            	for ( Chord.PitchedVoicing car : matches ) {
            		System.out.println("matched "+car);
            	}
        	}
        }
    }
    
    static { addChords(); }

    private static void addChords() {
		// Major: major third, major seventh ---------------------------------
        // three note
		addChord("maj", 		"1 3 5",			"major");
        // four note
		addChord("maj7", 		"1 3 5 7",			"major seventh");
		addChord("maj7-5",		"1 3 b5 7",			"major seventh flat 5");
		addChord("maj7+5",		"1 3 #5 7",	 		"major seventh sharp 5");
		addChord("maj7sus4",	"1 4 5 7",			"major seventh suspended fourth");
		addChord("add+11",		"1 3 5 #11",		"added augmented eleventh");
		addChord("6",			"1 3 5 6",			"six");
        // five note
		addChord("maj9", 		"1 3 5 7 9",		"major ninth");
		addChord("maj9+5", 		"1 3 #5 7 9",		"major ninth augmented fifth");
		addChord("maj9-5", 		"1 3 b5 7 9",		"major ninth diminished fifth");
		addChord("maj7+11",		"1 3 5 7 #11",		"major seventh augmented eleventh");
		addChord("6/7",			"1 3 5 6 7",		"six seven");
		addChord("6/9",			"1 3 5 6 9",		"major sixth added ninth");
		addChord("6/7sus4",		"1 4 5 6 7",		"six seven suspended fourth");
        // six note
		addChord("maj11", 		"1 3 5 7 9 11",		"major eleventh");
		addChord("maj11+5",		"1 3 #5 7 9 11",	"major eleventh augmented fifth");
		addChord("maj11-5",		"1 3 b5 7 9 11",	"major eleventh augmented fifth");
		addChord("maj9+11",		"1 3 5 7 9 #11",	"major ninth augmented eleventh");
        // seven note
		addChord("maj13", 		"1 3 5 7 9 11 13",	"major thirteenth");
		addChord("maj13+11",	"1 3 5 7 9 #11 13",	"major thirteenth augmented eleventh");
		// Minor/major: minor third, major seventh ---------------------------
		addChord("min/maj7",	"1 b3 5 7",			"minor/major seventh");
		addChord("min/maj9",	"1 b3 5 7 9",		"minor/major ninth");
		addChord("min/maj11",	"1 b3 5 7 9 11",	"minor/major eleventh");
		addChord("min/maj13",	"1 b3 5 7 9 11 13",	"minor/major thirteenth");
		// Minor: minor third, minor seventh ----------------------------------
        // three note
		addChord("m", 			"1 b3 5",			"minor");
        // four note
		addChord("m7", 			"1 b3 5 b7",		"minor seventh");
		addChord("m7-5", 		"1 b3 b5 b7",		"half diminished");
		addChord("m6",			"1 b3 5 6",			"minor sixth");
        // five note
		addChord("m9", 			"1 b3 5 b7 9",		"minor ninth");
		addChord("m9-5",		"1 b3 b5 b7 9",		"minor ninth diminished fifth");
		addChord("m7-9",		"1 b3 5 b7 b9",		"minor seventh flat nine");
		addChord("m7-9-5",		"1 b3 b5 b7 b9",	"minor seventh flat nine diminished fifth");
		addChord("m7/11",		"1 b3 5 b7 11",		"minor seven eleven");
		addChord("m6/7",		"1 b3 5 6 b7",		"minor six seven");
		addChord("m6/9",		"1 b3 5 6 9",		"minor six nine");
        // six note
		addChord("m11", 		"1 b3 5 b7 9 11",	"minor eleventh");
		addChord("m11-9",		"1 b3 5 b7 b9 11",	"minor eleventh flat nine");
		addChord("m11-9-5",		"1 b3 b5 b7 b9 11",	"minor eleventh flat nine diminished fifth");
		addChord("m11-5",		"1 b3 b5 b7 9 11",	"minor eleventh diminished fifth");
		addChord("m6/7/11",		"1 b3 5 6 b7 11",	"minor six seven eleven");
		addChord("m13/11",		"1 b3 5 9 11 13",	"minor thirteen eleven");
		// seven note
		addChord("m13", 		"1 b3 5 b7 9 11 13","minor thirteenth");
		addChord("m13-9",		"1 b3 5 b7 b9 11 13","minor thirteenth flat nine");
		addChord("m13-5",		"1 b3 b5 b7 9 11 13","minor thriteenth diminished fifth");
		// Diminished: minor third, diminished fifth
		addChord("dim", 		"1 b3 b5",			"diminished");
		addChord("dim7", 		"1 b3 b5 bb7",		"diminished seventh");
		// Augmented
		addChord("aug", 		"1 3 #5",			"augmented");
		// Dominant: major third, minor seventh -------------------------------
        // four note
		addChord("7",			"1 3 5 b7",			"seventh");
		addChord("7-5",			"1 3 b5 b7",		"seventh flat 5");
		addChord("7+5",			"1 3 #5 b7",		"seventh sharp 5");
        // five note
		addChord("9",			"1 3 5 b7 9",		"ninth");
		addChord("9-5",			"1 3 b5 b7 9",		"ninth diminished fifth");
		addChord("9+5",			"1 3 #5 b7 9",		"ninth augmented fifth");
		addChord("7-9",			"1 3 5 b7 b9",		"seventh flat 9");
		addChord("7-9-5",		"1 3 b5 b7 b9",		"seventh flat 9 dimished fifth");
		addChord("7-9+5",		"1 3 #5 b7 b9",		"seventh flat 9 augmented fifth");
		addChord("7+9",			"1 3 5 b7 #9",		"seventh augmented ninth");
		addChord("7+9-5",		"1 3 b5 b7 #9",		"seventh augmented ninth diminished fifth");
		addChord("7+9+5",		"1 3 #5 b7 #9",		"seventh augmented ninth augmented fifth");
		addChord("7/11",		"1 3 5 b7 11",		"seven eleven");
        // six note
		addChord("11",			"1 3 5 b7 9 11",	"eleventh");
        addChord("11+9",		"1 3 5 b7 #9 11",	"eleventh augmented ninth");
        addChord("11+9+5",		"1 3 #5 b7 #9 11",	"eleventh augmented ninth augmented fifth");
        addChord("11+9-5",		"1 3 b5 b7 #9 11",	"eleventh augmented ninth diminished fifth");
		addChord("11-9",		"1 3 5 b7 b9 11",	"eleventh diminished ninth");
        addChord("11-9+5",		"1 3 #5 b7 b9 11",	"eleventh diminished ninth augmented fifth");
        addChord("11-9-5",		"1 3 b5 b7 b9 11",	"eleventh diminished ninth diminished fifth");
		addChord("7+11",		"1 3 5 b7 9 #11",	"seventh augmented eleventh"); //
        addChord("7+11+9",		"1 3 5 b7 #9 #11",	"seventh augmented eleventh augmented ninth");
        addChord("7+11+9+5",	"1 3 #5 b7 #9 #11",	"seventh augmented eleventh augmented ninth augmented fifth");
        addChord("7+11-9",		"1 3 5 b7 b9 #11",	"seventh augmented eleventh diminished ninth");
        addChord("7+11-9+5",	"1 3 #5 b7 b9 #11",	"seventh augmented eleventh diminished ninth augmented fifth");
        // seven note
		addChord("13",			"1 3 5 b7 9 11 13",	"thirteenth");
		addChord("13+9",		"1 3 5 b7 #9 11 13","thirteenth augmented ninth");
		addChord("13+9+5",		"1 3 #5 b7 #9 11 13","thirteenth augmented ninth augmented fifth");
		addChord("13+9-5",		"1 3 b5 b7 #9 11 13","thirteenth augmented ninth diminished fifth");
		addChord("13-9",		"1 3 5 b7 b9 11 13","thirteenth diminished ninth");
		addChord("13-9+5",		"1 3 #5 b7 b9 11 13","thirteenth diminished ninth");
		addChord("13-9-5",		"1 3 b5 b7 b9 11 13","thirteenth diminished ninth");
		addChord("13+11",		"1 3 5 b7 9 #11 13","thirteenth augmented eleventh");
		addChord("13+11+9",		"1 3 5 b7 #9 #11 13","thirteenth augmented eleventh augmented ninth");
		addChord("13+11+9+5",	"1 3 #5 b7 #9 #11 13","thirteenth augmented eleventh augmented ninth augmented fifth");
		addChord("13+11-9",		"1 3 5 b7 b9 #11 13","thirteenth augmented eleventh diminished ninth");
		addChord("13+11-9+5",	"1 3 #5 b7 b9 #11 13","thirteenth augmented eleventh diminished ninth augmented fifth");
		addChord("13sus4",		"1 4 5 b7 9 13",	"thirteenth suspended fourth");

		// oddities :)
		addChord("sus2",		"1 2 5",			"suspended second");
		addChord("sus4",		"1 4 5",			"suspended fourth");
		addChord("add2",		"1 2 3 5",			"added second");
		addChord("add4",		"1 3 4 5",			"added fourth");
		addChord("5",			"1 5",				"power");
	}

    public interface Identifier 
    {
    	public List<Chord.PitchedVoicing> withNotes(int[] notes);
    }

    public static class DefaultIdentifier implements Identifier
    {
    	public List<Chord.PitchedVoicing> withNotes(int[] notes) {
    		List<Chord.PitchedVoicing> matches = new java.util.ArrayList<Chord.PitchedVoicing>();
    		
    		// first remove duplicate notes (identical pitch classes)
    		notes = PitchClass.distinct(notes);
    		int[] intervals = new int[notes.length];
    		
    		int rootStart = 0;
    		// may be a standard chord in root position
    		root(0, notes, intervals);
    		Chord chord = Chords.withIntervals(intervals);
    		if ( chord != null ) { 
    			matches.add(new Chord.PitchedVoicing(new Chord.Voicing(chord), notes[0]));
    			rootStart += 1;
    		}

    		boolean sixth;
    		List<Chord.Voicing> voicings;
    		for ( int missing = 0; missing < 3; missing++ ) {
//    			if ( missing > 0 ) System.out.println("trying "+missing+" missing");
        		for ( int root = rootStart; root < notes.length; root++) {
        			int nmatch = 0;
            		root(root, notes, intervals);
            		compress(intervals);
            		sixth = expand(intervals); // 6 -> 13 if present            		
            		voicings = Chords.withIntervals(intervals, missing);      		
           			for ( int i = 0; i < voicings.size(); i++ ) {
           				matches.add(new Chord.PitchedVoicing(voicings.get(i), notes[root], notes[0]));
           				nmatch += 1;
           			}
            		if ( sixth ) {
            			toggle(intervals, MAJOR_SIXTH); // 13 -> 6        			
            			voicings = Chords.withIntervals(intervals, missing);
           				for ( int i = 0; i < voicings.size(); i++ ) {
           					matches.add(new Chord.PitchedVoicing(voicings.get(i), notes[root], notes[0]));
               				nmatch += 1;
           				}
            		}
            		// try slash chords that don't include the 'bass note'
            		if ( nmatch == 0 ) {
            			if ( root == notes.length-1 ) continue;
            			int[] slashNotes = new int[notes.length -1];
                		int[] slashIntervals = new int[slashNotes.length];
            			int bassNote = notes[root];
            			for ( int i = 0, j = 0; i < notes.length; i++ ) {
            				int note = notes[i];
            				if ( note == bassNote ) continue;
            				slashNotes[j++] = note;
            			}
        				root(root, slashNotes, slashIntervals);
                		compress(slashIntervals);
                		sixth = expand(slashIntervals); // 6 -> 13 if present            		
                		voicings = Chords.withIntervals(slashIntervals, missing);
               			for ( int i = 0; i < voicings.size(); i++ ) {
               				matches.add(new Chord.PitchedVoicing(voicings.get(i), slashNotes[root], bassNote));
               				nmatch += 1;
               			}
                		if ( sixth ) {
                			toggle(intervals, MAJOR_SIXTH); // 13 -> 6        			
                			voicings = Chords.withIntervals(slashIntervals, missing);
               				for ( int i = 0; i < voicings.size(); i++ ) {
               					matches.add(new Chord.PitchedVoicing(voicings.get(i), slashNotes[root], bassNote));
                   				nmatch += 1;
               				}
                		}
            		}
        		}
        		// don't try more missing notes if already have matches
        		if ( matches.size() > 0 ) break;
    		}
    		return matches;
    	}

    	// side-effects
    	protected void root(int root, final int[] notes, int[] intervals) {
    		int tmp;
    		for ( int i = 0; i < intervals.length; i++) {
    			tmp = notes[i] - notes[root];
        		// ensure within range 0..23?
    			while ( tmp < 0 ) tmp += 12;
    			while ( tmp > 23 ) tmp -=12;
    			intervals[i] = tmp;
    		}    		
    		// ensure in order
     		Arrays.sort(intervals);
    	}
    	
    	// side-effects, requires sorted input
    	// compresses to one octave
    	protected void compress(int[] intervals) {
    		for ( int i = 0; i < intervals.length; i++) {
    			intervals[i] = intervals[i] % 12;
    		}
    		// ensure in order
     		Arrays.sort(intervals);    		
    	}
    	
    	// side-effects, requires sorted input
    	// expands to nearly two octaves to match Chord intervals
    	// returns true if sixth was promoted to thirteenth
    	protected boolean expand(int[] intervals) {
    		int tmp;
    		int tmp2;
    		int len = intervals.length;
    		boolean sixth = false;
    		for ( int i = 0; i < len; i++) {
    			tmp = intervals[i];
    			switch ( tmp ) {
    	        case UNISON: // "1"
    	        case MAJOR_THIRD: // "3"
    	        case PERFECT_FIFTH: // "5"
    	        case AUGMENTED_FIFTH: // "#5"
    	        case MINOR_SEVENTH: // "b7"
    	        case MAJOR_SEVENTH: // "7"
	        		continue; // these always stay in the first octave
	        		
    	        case MINOR_SECOND: // "b2", always b9, b2 never occurs
    	        	tmp += 12;
    	        	break;
    	        	
    	        case MAJOR_SECOND: // "2"
    	        	if ( i + 1 >= len ) break;
    	        	tmp2 = intervals[i+1];
    	        	if ( tmp2 == MINOR_THIRD || 
    	        		 tmp2 == MAJOR_THIRD ||
    	        		 tmp2 == PERFECT_FOURTH ) // for 13sus4
    	        		tmp += 12;
    	        	break;
    	        	
    	        case MINOR_THIRD: // "b3"
    	        	// #9 if 3 present
    	        	if ( i + 1 >= len ) break;
    	        	tmp2 = intervals[i+1];
    	        	if ( tmp2 == MAJOR_THIRD ) 
    	        		tmp += 12;
    	        	break;
    	        	
    	        case PERFECT_FOURTH: // "4"
    	        	// 11 if b3 or 3 present
    	        	if ( i - 1 < 0 ) break;
    	        	tmp2 = intervals[i-1];
    	        	if ( tmp2 == MINOR_THIRD || 
    	        		 tmp2 == MAJOR_THIRD )
    	        		tmp += 12;
    	        	break;

    	        case DIMINISHED_FIFTH: // "b5"
    	        	// #11 if 5 or #5 present
    	        	if ( i + 1 >= len ) break;
    	        	tmp2 = intervals[i+1];
	        		if ( tmp2 == PERFECT_FIFTH ||
	        			 tmp2 == AUGMENTED_FIFTH )
	        			tmp += 12;
    	        	break;
    	        	
    	        case MAJOR_SIXTH: // "6"
    	        	// try 13, no way of deciding with potentially missing notes
   	        		tmp += 12;
   	        		sixth = true;
    	        	break;
    			}
    			intervals[i] = tmp;
    		}
    		// ensure in order
     		Arrays.sort(intervals);
     		return sixth;
    	}

    	protected void toggle(int[] intervals, int value) {
    		for ( int i = 0; i < intervals.length; i++) {
    			if ( intervals[i] % 12 == value ) {
    				// toggle between value and value + 12
    				intervals[i] = value + value + 12 - intervals[i];
    				break;
    			}
    		}
    		// ensure in order
     		Arrays.sort(intervals);
        }
    }

}
