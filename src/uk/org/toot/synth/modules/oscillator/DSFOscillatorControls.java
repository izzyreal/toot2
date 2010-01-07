// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.modules.oscillator;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.IntegerControl;
import uk.org.toot.control.IntegerLaw;
import uk.org.toot.control.LinearLaw;

import static uk.org.toot.synth.modules.oscillator.OscillatorIds.DSF_OSCILLATOR_ID;
import static uk.org.toot.misc.Localisation.*;

/**
 * @author st
 */
public class DSFOscillatorControls extends CompoundControl implements DSFOscillatorVariables
{
	public final static int RATIO_N = 0;
	public final static int RATIO_D = 1;
	public final static int PARTIALS = 2;
	public final static int ROLLOFF = 3;
	
	private final static IntegerLaw RATIO_LAW = new IntegerLaw(1, 9, "");
	private final static IntegerLaw PARTIAL_LAW = new IntegerLaw(1, 200, "");
	
	private IntegerControl ratioNumeratorControl;
	private IntegerControl ratioDenominatorControl;
	private IntegerControl partialsControl;
	private FloatControl   rolloffControl;
	
	private int idOffset;
	private int ratioNumerator;
	private int ratioDenominator;
	private int partialCount;
	private float rolloffFactor;
	
	public DSFOscillatorControls(int instanceIndex, String name, final int idOffset) {
		super(DSF_OSCILLATOR_ID, instanceIndex, name);
		this.idOffset = idOffset;
		createControls();
		deriveSampleRateIndependentVariables();
		//deriveSampleRateDependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
				switch (c.getId()-idOffset) {
				case RATIO_N: ratioNumerator = deriveRatioNumerator(); break;
				case RATIO_D: ratioDenominator = deriveRatioDenominator(); break;
				case PARTIALS: partialCount = derivePartialCount(); break;
				case ROLLOFF: rolloffFactor = deriveRolloffFactor(); break;
				}
			}
		});
	}
	
	private void createControls() {
		add(ratioNumeratorControl = createRatioControl(RATIO_N, "N"));
		add(ratioDenominatorControl = createRatioControl(RATIO_D, "D"));
		add(partialsControl = createPartialsControl(PARTIALS));
		add(rolloffControl = createRolloffControl(ROLLOFF));
	}

	protected IntegerControl createRatioControl(int id, String name) {
		IntegerControl control = new IntegerControl(id+idOffset, name, RATIO_LAW, 1f, 1);
		control.setInsertColor(Color.GREEN);
		return control;
	}
	
	protected IntegerControl createPartialsControl(int id) {
		IntegerControl control = new IntegerControl(id+idOffset, getString("Purity"), PARTIAL_LAW, 1f, 10);
		control.setInsertColor(Color.BLUE);
		return control;		
	}
	
	protected FloatControl createRolloffControl(int id) {
		FloatControl control = new FloatControl(id+idOffset, getString("Brightness"), LinearLaw.UNITY, 1f, 0.5f);
		control.setInsertColor(Color.WHITE);
		return control;
	}

	private void deriveSampleRateIndependentVariables() {
		ratioDenominator = deriveRatioDenominator();
		ratioNumerator = deriveRatioNumerator();
		partialCount = derivePartialCount();
		rolloffFactor = deriveRolloffFactor();
	}
	
	protected int deriveRatioDenominator() {
		return ratioDenominatorControl.getUserValue();
	}
	
	protected int deriveRatioNumerator() {
		return ratioNumeratorControl.getUserValue();
	}
	
	protected int derivePartialCount() {
		return partialsControl.getUserValue();
	}
	
	protected float deriveRolloffFactor() {
		return rolloffControl.getValue() * 0.99f;
	}
	
	public int getPartialCount() {
		return partialCount;
	}

	public float getPartialRolloffFactor() {
		return rolloffFactor;
	}

	public int getRatioDenominator() {
		return ratioDenominator;
	}

	public int getRatioNumerator() {
		return ratioNumerator;
	}
}
