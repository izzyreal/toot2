// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import java.awt.Color;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.control.LogLaw;

import static uk.org.toot.misc.Localisation.*;

public class PhaserControls extends AudioControls implements PhaserVariables
{
    protected static final ControlLaw UNITY_LIN_LAW = new LinearLaw(0f, 1f, "");
    private final static int RATE_ID = 1;
    private final static int DEPTH_ID = 2;
    private final static int FEEDBACK_ID = 3;

	private FloatControl rateControl;
	private FloatControl depthControl;
	private FloatControl feedbackControl;
	
    public PhaserControls() {
		super(DelayIds.PHASER_ID, getString("Phaser"));
		ControlColumn cc = new ControlColumn();
		cc.add(createRateControl());
		cc.add(createDepthControl());
		cc.add(createFeedbackControl());
		add(cc);
	}

    @Override
	public boolean canBypass() { return true; }

    protected FloatControl createRateControl() {
    	ControlLaw rateLaw = new LogLaw(0.1f, 2f, "Hz");
 		rateControl = new FloatControl(RATE_ID, getString("Rate"), rateLaw, 0.01f, 0.5f);
        rateControl.setInsertColor(Color.MAGENTA.darker());
        return rateControl;
 	}

    protected FloatControl createDepthControl() {
 		depthControl = new FloatControl(DEPTH_ID, getString("Depth"), UNITY_LIN_LAW, 0.01f, 1f);
        depthControl.setInsertColor(Color.WHITE);
        return depthControl;
 	}

    protected FloatControl createFeedbackControl() {
 		feedbackControl = new FloatControl(FEEDBACK_ID, getString("Resonance"), UNITY_LIN_LAW, 0.01f, 0f);
        feedbackControl.setInsertColor(Color.ORANGE);
        return feedbackControl;
 	}

	public float getDepth() {
		return depthControl.getValue();
	}

	public float getFeedback() {
		return feedbackControl.getValue();
	}
	
	public float getRate() {
		return rateControl.getValue();
	}
	
}
