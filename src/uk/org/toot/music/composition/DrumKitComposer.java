// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

import static uk.org.toot.midi.misc.GM.*;

/**
 * A Drummer has four limbs, two hands and two feet.
 * But one foot only controls the kick drum.
 * And the the other foot only controls whether the hihat is open or closed
 * so it can be ignored because these are represented as two sounds.
 * So we only need to model:
 * One foot which can only control the kick drum.
 * Two hands which can control any drum or cymbal except the kick drum.
 * So we need three timings.
 * @author st
 *
 */
public class DrumKitComposer extends CompoundComposer 
{
    /**
     * @link aggregationByValue 
     * @supplierCardinality 1
     * @label kick
     */
	private RhythmicComposer kickComposer;

    /**
     * @link aggregationByValue 
     * @supplierCardinality 1
     * @label left hand
     */
	private RhythmicComposer leftHandComposer;

    /**
     * @link aggregationByValue 
     * @supplierCardinality 1
     * @label right hand
     */
	private RhythmicComposer rightHandComposer;
	
	public DrumKitComposer(String name, int program, int channel) {
		super(name, program, channel);
		kickComposer = new RhythmicComposer("Kick", program, channel, ACOUSTIC_BASS_DRUM);
		addComposer(kickComposer);
		leftHandComposer = new RhythmicComposer("Left", program, channel, ACOUSTIC_SNARE);
		addComposer(leftHandComposer);
		rightHandComposer = new RhythmicComposer("Right", program, channel, CLOSED_HI_HAT);
		addComposer(rightHandComposer);
	}

	/**
	 * @return the kickComposer
	 */
	public RhythmicComposer getKickComposer() {
		return kickComposer;
	}

	/**
	 * @return the leftHandComposer
	 */
	public RhythmicComposer getLeftHandComposer() {
		return leftHandComposer;
	}

	/**
	 * @return the rightHandComposer
	 */
	public RhythmicComposer getRightHandComposer() {
		return rightHandComposer;
	}

}
