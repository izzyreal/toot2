// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

/**
 * A Keyboard player has two hands.
 * So we need two timings.
 * @author st
 *
 */
public class KeyboardComposer extends CompoundComposer 
{
    /**
     * @link aggregationByValue 
     * @supplierCardinality 1
     * @label left hand
     */
	private TonalComposer leftHandComposer;

    /**
     * @link aggregationByValue 
     * @supplierCardinality 1
     * @label right hand
     */
	private TonalComposer rightHandComposer;
	
	public KeyboardComposer(String name, int program, int channel) {
		super(name, program, channel);
		// !!! need anonymous instances to ensure hands don't collide
		leftHandComposer = new TonalComposer("Left", program, channel);
		addComposer(leftHandComposer);
		rightHandComposer = new TonalComposer("Right", program, channel);
		addComposer(rightHandComposer);
	}

	/**
	 * @return the leftHandComposer
	 */
	public TonalComposer getLeftHandComposer() {
		return leftHandComposer;
	}

	/**
	 * @return the rightHandComposer
	 */
	public TonalComposer getRightHandComposer() {
		return rightHandComposer;
	}

}
