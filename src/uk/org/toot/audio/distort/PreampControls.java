// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.distort;

import static uk.org.toot.audio.distort.DistortionIds.PREAMP;
import static uk.org.toot.misc.Localisation.getString;

import java.awt.Color;

import org.tritonus.share.sampled.TVolumeUtils;

import uk.org.toot.control.Control;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;

/**
 * @author st
 *
 */
public class PreampControls extends Distort1Controls implements PreampVariables
{
	//private static final int GAIN2 = 2;
	private static final int BIAS2 = 3;
	private static final int MASTER = 4;
	
	protected final static LinearLaw MASTER_LAW = new LinearLaw(-20, 20, "dB");

	private FloatControl masterControl, bias2Control;
	
	private float bias2 = 0f;
	private float master = 1f;
	
	public PreampControls() {
		super(PREAMP, "Preamp");
		ControlColumn col = new ControlColumn();
		col.add(bias2Control = createBias2Control());
		col.add(masterControl = createMasterControl());
		add(col);
	}

	protected FloatControl createBias2Control() {
		FloatControl control = new FloatControl(BIAS2, getString("Bias"), BIAS_LAW, 0.01f, bias2);
		control.setInsertColor(Color.DARK_GRAY);
		return control;
	}
	
	protected FloatControl createMasterControl() {
		FloatControl control = new FloatControl(MASTER, getString("Master"), MASTER_LAW, 0.1f, 0);
		return control;
	}
	
	@Override
	protected void derive(Control c) {
        switch ( c.getId() ) {
        case MASTER: master = deriveMaster(); break;
        case BIAS2: bias2 = deriveBias2(); break;
        default: super.derive(c); break;
        }
	}
	
	protected float deriveMaster() {
		return (float)TVolumeUtils.log2lin(masterControl.getValue());
	}
	
	protected float deriveBias2() {
		return bias2Control.getValue();
	}
	
	public float getBias2() {
		return bias2;
	}

	public float getGain2() {
		return 4f;	// 12dB fixed
	}

	public float getMaster() {
		return master;
	}
}
