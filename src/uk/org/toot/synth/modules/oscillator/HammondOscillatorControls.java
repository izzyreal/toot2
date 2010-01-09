// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.modules.oscillator;

import static uk.org.toot.synth.modules.oscillator.OscillatorIds.DSF_OSCILLATOR_ID;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import org.tritonus.share.sampled.TVolumeUtils;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;

/**
 * @author st
 *
 */
public class HammondOscillatorControls extends CompoundControl implements HammondOscillatorVariables
{
	private int idOffset;

	private float[] levels;
	private static float gain1 = gain(1);
	
	private FloatControl[] levelControls;
	
	private final static String[] names = 
		{ "16'", "5 1/3", "8'", "4'", "2 2/3'", "2'", "1 3/5'", "1 1/3'", "1'"};
	
	private final static LinearLaw LEVEL_LAW = new LinearLaw(0, 8, "");
	private final static Color BROWN = new Color(160, 82, 45);
	
	public HammondOscillatorControls(int instanceIndex, String name, final int idOffset) {
		super(DSF_OSCILLATOR_ID, instanceIndex, name);
		this.idOffset = idOffset;
		levels = new float[names.length];
		levelControls = new FloatControl[names.length];
		createControls();
		deriveSampleRateIndependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
				int n = c.getId()-idOffset;
				levels[n] = deriveLevel(n);
//				switch ( c.getId()-idOffset ) {
//				}
			}
		});
	}
	
	private void createControls() {
		Color color;
		ControlRow row = new ControlRow();
		for ( int i = 0; i < names.length; i++ ) {
			if ( i < 2 ) color = BROWN;
			else if ( names[i].length() > 2 ) color = Color.DARK_GRAY;
			else color = Color.WHITE;
			row.add(levelControls[i] = createLevelControl(i+idOffset, names[i], color));
		}
		add(row);
	}
	
	protected FloatControl createLevelControl(int id, String name, Color color) {
        FloatControl control = new FloatControl(id, name, LEVEL_LAW, 0.1f, 0) {
            public boolean isRotary() { return false; }
        };
        control.setInsertColor(color);
		return control;
	}
	
	private void deriveSampleRateIndependentVariables() {
		for ( int i = 0; i < names.length; i++ ) {
			levels[i] = deriveLevel(i);
		}
	}

	/*
	 * Levels are marked 1..8.
	 * 8 is 0dB, each lower integer is 3dB lower, 0 is -infinity dB i.e. zero level
	 * 8=0, 7=-3, 6=-6, 5=-9, 4=-12, 3=-15, 2=-18, 1=-21, tapers to 0=-infinity
	 */
	protected float deriveLevel(int i) {
		float value = levelControls[i].getValue();
		if ( value < 1f ) {
			return value * gain1; // linear taper to zero
		} 
		return gain(value);		
	}
	
	protected static float gain(float value) {
		return (float)TVolumeUtils.log2lin(3f * (value - 8f));
	}
	
	public float[] getLevels() {
		return levels;
	}
}
