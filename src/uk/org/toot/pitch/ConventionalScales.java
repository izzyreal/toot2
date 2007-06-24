package uk.org.toot.pitch;

import static uk.org.toot.pitch.Interval.*;

public class ConventionalScales
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
//        Scales.add(new Scale("3 Semitone", threeSemitone)) ;

		// 4 Semitone		R	 	 	 	3	 	 	 	b6
        @SuppressWarnings("unused")
		int[] fourSemitone= {
            UNISON, MAJOR_THIRD, MINOR_SIXTH } ;
//        Scales.add(new Scale("4 Semitone", fourSemitone)) ;

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
