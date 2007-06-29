package uk.org.toot.pitch;

import static uk.org.toot.pitch.Interval.AUGMENTED_FIFTH;
import static uk.org.toot.pitch.Interval.AUGMENTED_FOURTH;
import static uk.org.toot.pitch.Interval.DIMINISHED_FIFTH;
import static uk.org.toot.pitch.Interval.MAJOR_SECOND;
import static uk.org.toot.pitch.Interval.MAJOR_SEVENTH;
import static uk.org.toot.pitch.Interval.MAJOR_SIXTH;
import static uk.org.toot.pitch.Interval.MAJOR_THIRD;
import static uk.org.toot.pitch.Interval.MINOR_SECOND;
import static uk.org.toot.pitch.Interval.MINOR_SEVENTH;
import static uk.org.toot.pitch.Interval.MINOR_SIXTH;
import static uk.org.toot.pitch.Interval.MINOR_THIRD;
import static uk.org.toot.pitch.Interval.PERFECT_FIFTH;
import static uk.org.toot.pitch.Interval.PERFECT_FOURTH;
import static uk.org.toot.pitch.Interval.UNISON;

import java.util.Iterator;
import java.util.List;

/**
 * The ordered list of available Scales
 * @author st
 */
public class Scales 
{
    /**
     * @supplierCardinality 1..*
     * @label scales 
     * @associates <{uk.org.toot.pitch.Scale}>
     */
    private static List<Scale> scales = new java.util.ArrayList<Scale>();

    private static void checkScales() {
    	if ( scales.isEmpty() ) {
    		Conventional.init();
    	}
    }
    
    static public List<Scale> getScales() {
    	checkScales();
        return scales;
    }

    static public void add(Scale scale) {
    	scales.add(scale);
    }
    
    static public List<String> getScaleNames() {
    	checkScales();
        List<String> scaleNames = new java.util.ArrayList<String>();
        Iterator iterator = scales.iterator();
        while ( iterator.hasNext() ) {
            Scale scale = (Scale)iterator.next();
			scaleNames.add(scale.getName());
        }
        return scaleNames;
    }

    static public Scale getInitialScale() {
    	checkScales();
    	return scales.get(0);
    }
    
    static public Scale getScale(String scaleName) {
    	checkScales();
        Iterator iterator = scales.iterator();
        while ( iterator.hasNext() ) {
            Scale scale = (Scale)iterator.next();
            if ( scale.getName().indexOf(scaleName) >= 0 ) {
                return scale;
            }
        }
        return getInitialScale(); // if in doubt, use the first scale
    }

    public static class Conventional
    {
        public static void init()
        {
            int[] major = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, PERFECT_FOURTH,
                	PERFECT_FIFTH, MAJOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Major", major)) ;

            int[] minorNatural = {
                UNISON, MAJOR_SECOND, MINOR_THIRD, PERFECT_FOURTH,
                	PERFECT_FIFTH, MINOR_SIXTH, MINOR_SEVENTH } ;
            Scales.add(new Scale("Natural Minor", minorNatural));

            int[] minorHarmonic = {
                UNISON, MAJOR_SECOND, MINOR_THIRD, PERFECT_FOURTH,
                	PERFECT_FIFTH, MINOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Harmonic Minor", minorHarmonic)) ;

            // Diminished		R	 	2	b3	 	4	b5	 	b6	6	 	7
            int[] diminished = {
                UNISON, MAJOR_SECOND, MINOR_THIRD, PERFECT_FOURTH,
                	DIMINISHED_FIFTH, MINOR_SIXTH, MAJOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Diminished", diminished)) ;

            // Augmented		R	 	 	b3	3	 	 	5	b6	 	 	7
            int[] augmented = {
                UNISON, MINOR_THIRD, MAJOR_THIRD, PERFECT_FIFTH,
                    MINOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Augmented", augmented)) ;

    		// Whole Tone		R	 	2	 	3	 	b5	 	b6	 	b7
            int[] wholeTone = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, DIMINISHED_FIFTH,
                    MINOR_SIXTH, MINOR_SEVENTH } ;
    		Scales.add(new Scale("Whole Tone", wholeTone)) ;

    		// Lydian Dominant		R	 	2	 	3	 	b5	5	 	6	b7
            int[] lydianDominant = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, DIMINISHED_FIFTH,
                    PERFECT_FIFTH, MAJOR_SIXTH, MINOR_SEVENTH } ;
            Scales.add(new Scale("Lydian Dominant", lydianDominant)) ;

    		// Pentatonic Major	R	 	2	 	3	 	 	5	 	6
            int[] pentatonicMajor = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, PERFECT_FIFTH, MAJOR_SIXTH } ;
            Scales.add(new Scale("Pentatonic Major", pentatonicMajor)) ;

    		// Pentatonic Minor	R	 	 	b3	 	4	 	5	 	 	b7
            int[] pentatonicMinor = {
                UNISON, MINOR_THIRD, DIMINISHED_FIFTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Pentatonic Minor", pentatonicMinor)) ;

    		// 3 Semitone		R	 	 	b3	 	 	b5	 	 	6
            @SuppressWarnings("unused")
    		int[] threeSemitone = {
                UNISON, MINOR_THIRD, DIMINISHED_FIFTH, MAJOR_SIXTH } ;
//            Scales.add(new Scale("3 Semitone", threeSemitone)) ;

    		// 4 Semitone		R	 	 	 	3	 	 	 	b6
            @SuppressWarnings("unused")
    		int[] fourSemitone= {
                UNISON, MAJOR_THIRD, MINOR_SIXTH } ;
//            Scales.add(new Scale("4 Semitone", fourSemitone)) ;

    		// Blues			R	 	 	b3	 	4	b5	5	 	 	b7
            int[] blues = {
                UNISON, MINOR_THIRD, PERFECT_FOURTH,
                    DIMINISHED_FIFTH, PERFECT_FIFTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Blues", blues)) ;

    		// Bebop			R	 	2	 	3	4	 	5	 	6	b7	7
            int[] bebop = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, PERFECT_FOURTH,
                    PERFECT_FIFTH, MAJOR_SIXTH, MINOR_SEVENTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Bebop", bebop)) ;

    		// Enigmatic		R	b2	 	 	3	 	b5	 	b6	 	b7	7
            int[] enigmatic = {
                UNISON, MINOR_SECOND, MAJOR_THIRD, DIMINISHED_FIFTH,
                    MINOR_SIXTH, MINOR_SEVENTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Enigmatic", enigmatic)) ;
        }

    }

    /**
     * George Russell
     * Lydian Chromatic Concept of Tonal Organisation
     * Chapter 2
     * @author st
     */
    public static class LydianChromaticConcept 
    {
        public static void init()
        {
        	// the seven Principal Scales of the Lydian Chromatic Scale

        	// 7 tone order, ingoing Tonal Gravity level

            int[] lydian = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, AUGMENTED_FOURTH,
                	PERFECT_FIFTH, MAJOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Lydian", lydian)) ;

            // 9 tone order, semi-ingoing Tonal Gravity level

            int[] lydianAugmented = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, AUGMENTED_FOURTH,
                	AUGMENTED_FIFTH, MAJOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Lydian Augmented", lydianAugmented)) ;

            int[] lydianDiminished = {
                UNISON, MAJOR_SECOND, MINOR_THIRD, AUGMENTED_FOURTH,
                	PERFECT_FIFTH, MAJOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Lydian Diminished", lydianDiminished)) ;

            // scales above are the Consonant Nucleus
            
            // 10 tone order, semi-outgoing Tonal Gravity level
            
            int[] lydianFlatSeventh = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, AUGMENTED_FOURTH,
                	PERFECT_FIFTH, MAJOR_SIXTH, MINOR_SEVENTH } ;
            Scales.add(new Scale("Lydian Flat Seventh", lydianFlatSeventh)) ;

            int[] auxAugmented = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, AUGMENTED_FOURTH,
                	AUGMENTED_FIFTH, MINOR_SEVENTH } ;
            Scales.add(new Scale("Auxiliary Augmented", auxAugmented)) ;

            // 11 tone order, semi-outgoing Tonal Gravity level
            
            int[] auxDiminished = {
                UNISON, MAJOR_SECOND, MINOR_THIRD, PERFECT_FOURTH,
                	AUGMENTED_FOURTH, AUGMENTED_FIFTH, MAJOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Auxiliary Diminished", auxDiminished)) ;

            // 12 tone order, outgoing Tonal Gravity level

            int[] auxDiminishedBlues = {
                UNISON, MINOR_SECOND, MINOR_THIRD, MAJOR_THIRD, AUGMENTED_FOURTH, 
                	PERFECT_FIFTH, MAJOR_SIXTH, MINOR_SEVENTH } ;
            Scales.add(new Scale("Auxiliary Diminished Blues", auxDiminishedBlues)) ;

            // the four Horizontal Scales of the Lydian Chromatic Scale
            // they contain the perfect fourth degree which causes them to
            // exist in a state of resolving (cadencing) to the I Major
            // or VI Minor cadential centre

            int[] major = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, PERFECT_FOURTH,
                	PERFECT_FIFTH, MAJOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Major", major)) ;

            int[] majorFlatSeventh = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, PERFECT_FOURTH,
                	PERFECT_FIFTH, MAJOR_SIXTH, MINOR_SEVENTH } ;
            Scales.add(new Scale("Major Flat Seventh", majorFlatSeventh)) ;

            int[] majorAugmentedFifth = {
                UNISON, MAJOR_SECOND, MAJOR_THIRD, PERFECT_FOURTH,
                	AUGMENTED_FIFTH, MAJOR_SIXTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("Major Augmented", majorAugmentedFifth)) ;

            int[] africanAmericanBlues = {
                UNISON, MAJOR_SECOND, MINOR_THIRD, MAJOR_THIRD, 
                	PERFECT_FOURTH,	AUGMENTED_FOURTH, PERFECT_FIFTH, 
                	MAJOR_SIXTH, MINOR_SEVENTH, MAJOR_SEVENTH } ;
            Scales.add(new Scale("African-American Blues", africanAmericanBlues)) ;
        }

    }
}
