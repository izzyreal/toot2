package uk.org.toot.pitch;

import static uk.org.toot.pitch.Interval.*;

/**
 * George Russell
 * Lydian Chromatic Concept of Tonal Organisation
 * Chapter 2
 * @author st
 */
public class LydianChromaticConceptScales 
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
        Scales.add(new Scale("Major Augmented", africanAmericanBlues)) ;
    }

}
