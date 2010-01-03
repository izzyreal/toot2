// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.modules.amplifier;

import static uk.org.toot.misc.Localisation.getString;
import static uk.org.toot.synth.modules.amplifier.AmplifierControlIds.*;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.control.LogLaw;

public class AmplifierControls extends CompoundControl 
	implements AmplifierVariables
{
	private final static ControlLaw LEVEL_LAW = new LogLaw(0.01f, 1f, "");
	
	private FloatControl velocityTrackControl;
	private FloatControl levelControl;
	
	private float velocityTrack;
	private float level;
	
	private int idOffset = 0;
	
	private int sampleRate = 44100;
	
	public AmplifierControls(int instanceIndex, String name, int idOffset) {
		this(AmplifierIds.AMPLIFIER_ID , instanceIndex, name, idOffset);
	}
	
	public AmplifierControls(int id, int instanceIndex, String name, final int idOffset) {
		super(id, instanceIndex, name);
		this.idOffset = idOffset;
		createControls();
		deriveSampleRateIndependentVariables();
		deriveSampleRateDependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
//				if (c.isIndicator()) return;
				switch (c.getId()-idOffset) {
				case VEL_TRACK: velocityTrack = deriveVelocityTrack() ; break;
				case LEVEL: level = deriveLevel(); break;
				}
			}
		});
	}

	protected void createControls() {
		add(velocityTrackControl = createVelocityTrackControl());
		add(levelControl = createLevelControl());
	}

	protected void deriveSampleRateIndependentVariables() {
		velocityTrack = deriveVelocityTrack();
		level = deriveLevel();
	}

	protected float deriveVelocityTrack() {
		return velocityTrackControl.getValue();
	}

	protected float deriveLevel() {
		return levelControl.getValue();
	}
	
	protected void deriveSampleRateDependentVariables() {
	}

	protected FloatControl createVelocityTrackControl() {
        FloatControl control = new FloatControl(VEL_TRACK+idOffset, getString("Velocity"), LinearLaw.UNITY, 0.01f, 0.5f);
        control.setInsertColor(Color.BLUE);
        return control;				
	}

	protected FloatControl createLevelControl() {
        return new FloatControl(LEVEL+idOffset, getString("Level"), LEVEL_LAW, 0.01f, 0.1f);
	}
	
	public float getVelocityTrack() {
		return velocityTrack;
	}
	
	public float getLevel() {
		return level;
	}
	
	public void setSampleRate(int rate) {
		if ( sampleRate != rate ) {
			sampleRate = rate;
			deriveSampleRateDependentVariables();
		}
	}

}
