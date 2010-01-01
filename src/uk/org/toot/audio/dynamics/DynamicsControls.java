/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.control.*;
import java.awt.Color;

import static uk.org.toot.audio.dynamics.DynamicsControlIds.*;
import static uk.org.toot.misc.Localisation.*;

abstract public class DynamicsControls extends AudioControls
    implements DynamicsDesign.DesignVariables
{
    private GainReductionIndicator gainReductionIndicator;
    private FloatControl thresholdControl;
    private FloatControl ratioControl;
    private FloatControl kneeControl;
    private FloatControl attackControl;
    private FloatControl holdControl;
    private FloatControl releaseControl;
    private FloatControl gainControl;
    private FloatControl depthControl;

    private float kneedB = 0f;
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
            depthControl = createDepthControl(-80);
            g1.add(depthControl);
        }
        if ( hasRatio() ) {
	        if ( kneedB > 0 ) {
    	        kneeControl = createKneeControl(kneedB);
        		g1.add(kneeControl);
        	}
	        ratioControl = createRatioControl();
    	    g1.add(ratioControl);
        }
        thresholdControl = createThresholdControl(getMinimumThreshold());
        g1.add(thresholdControl);
        add(g1);

        ControlColumn g2 = new ControlColumn();
        attackControl = createAttackControl(getMinimumAttack(), 100f, 20f);
        g2.add(attackControl);
        if ( hasHold() ) {
	        holdControl = createHoldControl(0, 1000, 10);
    	    g2.add(holdControl);
        }
        releaseControl = createReleaseControl(getMinimumRelease(), 2000, 200);
        g2.add(releaseControl);
		add(g2);

        ControlColumn g3 = new ControlColumn();
        boolean useg3 = false;
        if ( hasGain() ) {
            gainControl = createGainControl(-12, 12);
            g3.add(gainControl);
            useg3 = true;
        }
        if ( useg3 ) {
        	add(g3);
        }
    }

    public boolean canBypass() { return true; }

	protected boolean hasGainReductionIndicator() { return false; }

    protected float getMinimumThreshold() {
        return -40f; // -60 in Expander, Gate !!!
    }

    protected float getMinimumAttack() {
    	return 0.1f;
    }
    
    protected float getMinimumRelease() {
    	return 2f;
    }
    
    protected FloatControl createThresholdControl(float min) {
        LinearLaw law = new LinearLaw(min, 20f, "dB");
        FloatControl threshold = new FloatControl(THRESHOLD+idOffset, getString("Threshold"), law, 0.1f, 0f);
        threshold.setInsertColor(Color.white);
        return threshold;
    }

    protected boolean hasRatio() { return false; }

    protected FloatControl createRatioControl() {
        ControlLaw law = new LogLaw(1.5f, 10f, "");
        FloatControl ratio = new FloatControl(RATIO+idOffset, getString("Ratio"), law, 0.1f, 2f);
        ratio.setInsertColor(java.awt.Color.magenta.darker());
        return ratio;
    }

    protected FloatControl createKneeControl(float kneedB) {
        ControlLaw law = new LinearLaw(0f, kneedB, "dB");
        FloatControl knee = new FloatControl(KNEE+idOffset, getString("Knee"), law, 0.1f, kneedB / 2);
        return knee;
    }

    protected FloatControl createAttackControl(float min, float max, float init) {
        ControlLaw law = new LogLaw(min, max, "ms");
        FloatControl attack = new FloatControl(ATTACK+idOffset, getString("Attack"), law, 0.1f, init);
        attack.setInsertColor(Color.red.darker());
        return attack;
    }

    protected boolean hasHold() { return false; }

    protected FloatControl createHoldControl(float min, float max, float init) {
        ControlLaw law = new LinearLaw(min, max, "ms");
        FloatControl hold = new FloatControl(HOLD+idOffset, getString("Hold"), law, 1f, init);
        hold.setInsertColor(Color.red.darker());
        return hold;
    }

    protected FloatControl createReleaseControl(float min, float max, float init) {
        ControlLaw law = new LogLaw(min, max, "ms");
        FloatControl release = new FloatControl(RELEASE+idOffset, getString("Release"), law, 1f, init);
        release.setInsertColor(Color.red.darker());
        return release;
    }

    protected boolean hasGain() { return false; }

    protected FloatControl createGainControl(float min, float max) {
        ControlLaw law = new LinearLaw(min, max, "dB");
        FloatControl gain = new FloatControl(GAIN+idOffset, getString("Gain"), law, 1f, 0);
        gain.setInsertColor(Color.white);
        return gain;
    }

    protected boolean hasDepth() { return false; }

    protected FloatControl createDepthControl(float depth) {
        ControlLaw law = new LinearLaw(depth, 0, "dB");
        FloatControl depthC = new FloatControl(DEPTH+idOffset, getString("Depth"), law, 1f, -40);
        depthC.setInsertColor(Color.lightGray);
        return depthC;
    }

    static public class GainReductionIndicator extends FloatControl
    {
        public GainReductionIndicator() {
//            super(0, "Gain Reduction", new LinearLaw(-20f, 0, "dB"), 3f, 0f);
            super(0, "Gain Reduction", new LinearLaw(-20f, 0, "dB"), 3f, 0f);
            indicator = true;
            setHidden(true); // prevent normal layout
        }
    }

//	implement DynamicsDesign.DesignVariables

    public float getThresholddB() {
        return thresholdControl.getValue();
    }

    public float getRatio() {
        if ( ratioControl == null ) return 1f;
        return ratioControl.getValue();
    }

    public float getKneedB() {
        if ( kneeControl == null ) return 0f; // 0dB span, totally hard knee
        return kneeControl.getValue(); // !!! !!! return converted
    }

    public float getAttackMilliseconds() {
        return attackControl.getValue();
    }

    public float getHoldMilliseconds() {
        if ( holdControl == null ) return 0f;
        return holdControl.getValue();
    }

    public float getReleaseMilliseconds() {
        if ( releaseControl == null ) return 0f;
        return releaseControl.getValue();
    }

    public float getGaindB() {
        if ( gainControl == null ) return 0f;
        return gainControl.getValue();
    }

    public float getDepthdB() {
        if ( depthControl == null ) return 40f;
        return depthControl.getValue();
    }

    public void setGainReduction(float dB) {
        if ( gainReductionIndicator == null ) return;
        gainReductionIndicator.setValue(dB);
    }
}
