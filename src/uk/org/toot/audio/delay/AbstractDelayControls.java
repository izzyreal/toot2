// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.control.*;
import java.awt.Color;

import static uk.org.toot.localisation.Localisation.*;

public class AbstractDelayControls extends AudioControls
{
    // reserve some id's at the top of our range for common controls
    private static final int FEEDBACK_INVERT_ID = 121;
    private static final int FEEDBACK_ID = 122;
    private static final int MIX_INVERT_ID = 123;
    private static final int MIX_ID = 124;
    protected static final int DELAY_FACTOR_ID = 125;

    protected static final ControlLaw UNITY_LIN_LAW = new LinearLaw(0f, 1f, "");

    private BooleanControl feedbackInvertControl;
    private FloatControl feedbackControl;
    private BooleanControl mixInvertControl;
    private FloatControl mixControl;

    public AbstractDelayControls(int id, String name) {
        super(id, name);
    }

    protected void addFeedbackInvertControl() {
        feedbackInvertControl = new BooleanControl(FEEDBACK_INVERT_ID, getString("Invert"), false);
        feedbackInvertControl.setStateColor(true, Color.orange);
    }

    protected void addFeedbackControl() {
 		feedbackControl = new FloatControl(FEEDBACK_ID, getString("Resonance"), UNITY_LIN_LAW, 0.01f, 0f);
        feedbackControl.setInsertColor(Color.orange);
 	}

    protected void addMixInvertControl() {
        mixInvertControl = new BooleanControl(MIX_INVERT_ID, getString("Invert"), false);
        mixInvertControl.setStateColor(true, Color.PINK);
    }

    protected void addMixControl() {
        mixControl = new MixControl();
    }

    protected ControlColumn createCommonControlColumn(boolean withInverts) {
        ControlColumn g = new ControlColumn();
        if ( withInverts ) addFeedbackInvertControl();
        addFeedbackControl();
        if ( withInverts ) addMixInvertControl();
        addMixControl();
        if ( feedbackInvertControl != null ) g.add(feedbackInvertControl);
        if ( feedbackControl != null ) g.add(feedbackControl);
        if ( mixInvertControl != null ) g.add(mixInvertControl);
        if ( mixControl != null ) g.add(mixControl);
        return g;
    }

    // should be a private control matter, just negate feedback gain
    public boolean isFeedbackInverted() {
        if ( feedbackInvertControl == null ) return false;
        return feedbackInvertControl.getValue();
    }

    public float getFeedback() {
        if ( feedbackControl == null ) return 0f;
        if ( isFeedbackInverted() ) return -feedbackControl.getValue();
        return feedbackControl.getValue();
    }

    public boolean isWetInverted() {
        if ( mixInvertControl == null ) return false;
        return mixInvertControl.getValue();
    }

    public float getMix() {
        if ( mixControl == null ) return 1.0f; // full wet, no dry
        return mixControl.getValue();
    }

    static public class MixControl extends FloatControl
    {
        private static final String[] presetNames = {
            getString("Dry"), getString("Wet")
        };

        public MixControl() {
            super(MIX_ID, getString("Mix"), UNITY_LIN_LAW, 0.01f, 0.5f);
            setInsertColor(Color.white);
        }

        public String[] getPresetNames() {
        	return presetNames;
    	}

    	public void applyPreset(String name) {
            if ( getString("Dry").equals(name) ) {
                setValue(0f);
            } else if ( getString("Wet").equals(name) ) {
                setValue(1f);
            }
    	}
    }
}
