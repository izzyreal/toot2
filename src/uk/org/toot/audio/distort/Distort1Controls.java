// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.distort;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.core.KVolumeUtils;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;

import static uk.org.toot.misc.Localisation.getString;

/**
 * @author st
 */
public class Distort1Controls extends AudioControls implements Distort1Variables
{
	private static final int GAIN = 0;
	
	private final static LinearLaw GAIN_LAW = new LinearLaw(0, 30, "dB");
	
	private float gain = 1f;
	
	public Distort1Controls() {
		super(DistortionIds.DISTORT1, "OD");
		add(createGainControl());
	}
	
	protected FloatControl createGainControl() {
		FloatControl control = new FloatControl(GAIN, getString("Gain"), GAIN_LAW, 0.1f, 0f) {
			public void setValue(float value) {
				super.setValue(value);
				gain = deriveGain(value);
			}
		};
		return control;
	}
	
	protected float deriveGain(float dB) {
		return (float)KVolumeUtils.log2lin(dB);		
	}

	public float getGain() {
		return gain;
	}
}
