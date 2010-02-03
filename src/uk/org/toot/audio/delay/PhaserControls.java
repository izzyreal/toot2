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

public class PhaserControls extends AudioControls implements PhaserProcess.Variables
{
	protected final static ControlLaw rateLaw = new LogLaw(0.1f, 2f, "Hz");
    protected final static int RATE_ID = 1;
    protected final static int DEPTH_ID = 2;
    protected final static int FEEDBACK_ID = 3;

	private FloatControl rateControl;
	private FloatControl depthControl;
	private FloatControl feedbackControl;
	
    public PhaserControls() {
		super(DelayIds.PHASER_ID, getString("Phaser"));
		ControlColumn cc = new ControlColumn();
		cc.add(rateControl = createRateControl());
		cc.add(depthControl = createDepthControl());
		cc.add(feedbackControl = createFeedbackControl());
		add(cc);
	}

    @Override
	public boolean canBypass() { return true; }

    protected FloatControl createRateControl() {
 		return new FloatControl(RATE_ID, getString("Rate"), rateLaw, 0.01f, 0.5f);
 	}

    protected FloatControl createDepthControl() {
 		FloatControl c = new FloatControl(DEPTH_ID, getString("Depth"), LinearLaw.UNITY, 0.01f, 1f);
        c.setInsertColor(Color.LIGHT_GRAY);
        return c;
 	}

    protected FloatControl createFeedbackControl() {
 		return new FloatControl(FEEDBACK_ID, getString("Resonance"), LinearLaw.UNITY, 0.01f, 0f);
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
