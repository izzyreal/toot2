/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.core.Taps.TapControl;
import uk.org.toot.control.*;
import java.awt.Color;

import static uk.org.toot.audio.dynamics.DynamicsControlIds.*;
import static uk.org.toot.misc.Localisation.*;

abstract public class DynamicsControls extends AudioControls
    implements DynamicsDesign.DesignVariables
{
	private final static ControlLaw THRESH_LAW = new LinearLaw(-40f, 20f, "dB");
    private final static ControlLaw RATIO_LAW = new LogLaw(1.5f, 10f, "");
    private final static ControlLaw ATTACK_LAW = new LogLaw(20f, 100f, "ms");
    private final static ControlLaw HOLD_LAW = new LogLaw(1f, 1000f, "ms");
    private final static ControlLaw RELEASE_LAW = new LogLaw(200f, 2000f, "ms");
    private final static ControlLaw GAIN_LAW = new LinearLaw(-12f, 12f, "dB");
    private final static ControlLaw DEPTH_LAW = new LinearLaw(-80f, 0f, "dB");

    private GainReductionIndicator gainReductionIndicator;
    private FloatControl thresholdControl;
    private FloatControl ratioControl;
    private FloatControl attackControl;
    private FloatControl holdControl;
    private FloatControl releaseControl;
    private FloatControl gainControl;
    private FloatControl depthControl;
    private TapControl keyControl;
    
    private float threshold, ratio = 1f, attack, hold, release, gain, depth = 40f;
    private AudioBuffer key;

    private int idOffset = 0;

    public DynamicsControls(int id, String name) {
        this(id, name, 0);
    }

    public DynamicsControls(int id, String name, int idOffset) {
        super(id, name, 126-idOffset); // cheap sparse bypass id
        this.idOffset = idOffset;
        if ( hasGainReductionIndicator() ) {
            gainReductionIndicator = new GainReductionIndicator();
        	add(gainReductionIndicator);
        }
        ControlColumn g1 = new ControlColumn();
        if ( hasDepth() ) {
            depthControl = createDepthControl();
            g1.add(depthControl);
            derive(depthControl);
        }
        if ( hasRatio() ) {
	        ratioControl = createRatioControl();
    	    g1.add(ratioControl);
    	    derive(ratioControl);
        }
        thresholdControl = createThresholdControl();
        g1.add(thresholdControl);
        derive(thresholdControl);
        add(g1);

        ControlColumn g2 = new ControlColumn();
        attackControl = createAttackControl();
        g2.add(attackControl);
        derive(attackControl);
        if ( hasHold() ) {
	        holdControl = createHoldControl();
    	    g2.add(holdControl);
    	    derive(holdControl);
        }
        releaseControl = createReleaseControl();
        g2.add(releaseControl);
		add(g2);
		derive(releaseControl);

        ControlColumn g3 = new ControlColumn();
        boolean useg3 = false;
        if ( hasKey() ) {
        	keyControl = createKeyControl();
        	g3.add(keyControl);
        	useg3 = true;
        	derive(keyControl);
        }
        if ( hasGain() ) {
            gainControl = createGainControl();
            g3.add(gainControl);
        	useg3 = true;
        	derive(gainControl);
        }
        if ( useg3 ) {
        	add(g3);
        }
    }

    @Override
    protected void derive(Control c) {
    	switch ( c.getId() - idOffset ) {
    	case THRESHOLD: threshold = thresholdControl.getValue(); break;
    	case RATIO: ratio = ratioControl.getValue(); break;
    	case ATTACK: attack = attackControl.getValue(); break;
    	case HOLD: hold = holdControl.getValue(); break;
    	case RELEASE: release = releaseControl.getValue(); break;
    	case GAIN: gain = gainControl.getValue(); break;
    	case DEPTH: depth = depthControl.getValue(); break;
    	case KEY: key = keyControl.getBuffer(); break; 
    	}
    }
    
	protected boolean hasGainReductionIndicator() { return false; }

	protected ControlLaw getThresholdLaw() {
		return THRESH_LAW;
	}

	protected FloatControl createThresholdControl() {
        return new FloatControl(THRESHOLD+idOffset, getString("Threshold"), getThresholdLaw(), 0.1f, 0f);
    }

    protected boolean hasRatio() { return false; }

    protected FloatControl createRatioControl() {
        FloatControl ratio = new FloatControl(RATIO+idOffset, getString("Ratio"), RATIO_LAW, 0.1f, 2f);
        ratio.setInsertColor(java.awt.Color.BLUE);
        return ratio;
    }

    protected ControlLaw getAttackLaw() {
    	return ATTACK_LAW;
    }
    
    protected FloatControl createAttackControl() {
        ControlLaw law = getAttackLaw();
        return new FloatControl(ATTACK+idOffset, getString("Attack"), law, 0.1f, law.getMinimum());
    }

    protected boolean hasHold() { return false; }

    protected ControlLaw getHoldLaw() {
    	return HOLD_LAW;
    }
    
    protected FloatControl createHoldControl() {
        return new FloatControl(HOLD+idOffset, getString("Hold"), getHoldLaw(), 1f, 10f);
    }

    protected ControlLaw getReleaseLaw() {
    	return RELEASE_LAW;
    }
    
    protected FloatControl createReleaseControl() {
        ControlLaw law = getReleaseLaw();
        return new FloatControl(RELEASE+idOffset, getString("Release"), law, 1f, law.getMinimum());
    }

    protected boolean hasGain() { return false; }

    protected ControlLaw getGainLaw() {
    	return GAIN_LAW;
    }
    
    protected FloatControl createGainControl() {
        return new FloatControl(GAIN+idOffset, getString("Gain"), getGainLaw(), 1f, 0);
    }

    protected boolean hasDepth() { return false; }

    protected ControlLaw getDepthLaw() {
    	return DEPTH_LAW;
    }
    
    protected FloatControl createDepthControl() {
        FloatControl depthC = new FloatControl(DEPTH+idOffset, getString("Depth"), getDepthLaw(), 1f, -40);
        depthC.setInsertColor(Color.lightGray);
        return depthC;
    }

    protected boolean hasKey() { return false; }
    
    protected TapControl createKeyControl() {
    	return new TapControl(KEY+idOffset, "Key");
    }
    
    static public class GainReductionIndicator extends FloatControl
    {
        public GainReductionIndicator() {
            super(-1, "Gain Reduction", new LinearLaw(-20f, 0, "dB"), 3f, 0f);
            indicator = true;
            setHidden(true); // prevent normal layout
        }
    }

//	implement DynamicsDesign.DesignVariables

    public float getThresholddB() {
        return threshold;
    }

    public float getRatio() {
        return ratio;
    }

    public float getAttackMilliseconds() {
        return attack;
    }

    public float getHoldMilliseconds() {
        return hold;
    }

    public float getReleaseMilliseconds() {
        return release;
    }

    public float getGaindB() {
        return gain;
    }

    public float getDepthdB() {
        return depth;
    }

    public void setGainReduction(float dB) {
        if ( gainReductionIndicator == null ) return;
        gainReductionIndicator.setValue(dB);
    }
    
    public AudioBuffer getKeyBuffer() {
    	return key;
    }
}
