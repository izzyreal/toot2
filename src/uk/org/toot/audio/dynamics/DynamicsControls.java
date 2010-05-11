// Copyright (C) 2006, 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.dynamics;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.core.KVolumeUtils;
import uk.org.toot.audio.core.Taps.TapControl;
import uk.org.toot.control.*;

import java.awt.Color;

import org.tritonus.share.sampled.TVolumeUtils;

import static uk.org.toot.audio.dynamics.DynamicsControlIds.*;
import static uk.org.toot.misc.Localisation.*;

abstract public class DynamicsControls extends AudioControls
    implements DynamicsProcess.Variables
{
	private final static ControlLaw THRESH_LAW = new LinearLaw(-40f, 20f, "dB");
    private final static ControlLaw RATIO_LAW = new LogLaw(1.5f, 10f, "");
    private final static ControlLaw ATTACK_LAW = new LogLaw(10f, 100f, "ms");
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
    
    private float sampleRate = 44100;
    private float threshold, thresholddB, ratio = 1f, knee = 0f;
    private float attack, release, gain = 1f, depth = 40f;
    public int hold = 0;
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

    public void update(float sampleRate) {
        this.sampleRate = sampleRate;
        // derive sample rate dependent variables
        deriveAttack();
        deriveHold();
        deriveRelease();
    }

    @Override
    protected void derive(Control c) {
    	switch ( c.getId() - idOffset ) {
    	case THRESHOLD: deriveThreshold(); break;
    	case RATIO: deriveRatio(); break;
    	case ATTACK: deriveAttack(); break;
    	case HOLD: deriveHold(); break;
    	case RELEASE: deriveRelease(); break;
    	case GAIN: deriveGain(); break;
    	case DEPTH: deriveDepth(); break;
    	case KEY: deriveKey(); break; 
    	}
    }
    
    protected void deriveThreshold() {
        thresholddB = thresholdControl.getValue();
        threshold = (float)KVolumeUtils.log2lin(thresholddB);
    }

    protected void deriveRatio() {
        if ( ratioControl == null ) return;
        ratio = ratioControl.getValue();
    }
    
    private static float LOG_0_01 = (float)Math.log(0.01);
    // http://www.physics.uoguelph.ca/tutorials/exp/Q.exp.html
    // http://www.musicdsp.org/showArchiveComment.php?ArchiveID=136
    // return per sample factor for 99% in specified milliseconds
    protected float deriveTimeFactor(float milliseconds) {
        float ns = milliseconds * sampleRate / 1000;
        float k = LOG_0_01 / ns ; // k, per sample
        return (float)Math.exp(k);
    }

    protected void deriveAttack() {
        attack = deriveTimeFactor(attackControl.getValue());
    }

    protected void deriveHold() {
        if ( holdControl == null ) return;
        hold = (int)(holdControl.getValue()*sampleRate*0.001f);
    }

    protected void deriveRelease() {
        release = deriveTimeFactor(releaseControl.getValue());
    }

    protected void deriveGain() {
        if ( gainControl == null ) return;
        gain = (float)TVolumeUtils.log2lin(gainControl.getValue());
    }

    protected void deriveDepth() {
        if ( depthControl == null ) return;
        depth = (float)TVolumeUtils.log2lin(depthControl.getValue());
    }

    protected void deriveKey() {
        if ( keyControl == null ) return;
        key = keyControl.getBuffer();
    }
    
    @Override
    protected void setParent(CompoundControl parent) {
        super.setParent(parent);
        if ( parent == null && keyControl != null ) {
            keyControl.remove(); // dereferences tap
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

//	implement DynamicsProcess.Variables

    public float getThresholddB() {
        return thresholddB;
    }

    public float getThreshold() {
        return threshold;
    }

    public float getKnee() {
        return knee;
    }

    public float getRatio() {
        return ratio;
    }
    
    public float getAttack() {
        return attack;
    }

    public int getHold() {
        return hold;
    }

    public float getRelease() {
        return release;
    }

    public float getGain() {
        return gain;
    }

    public float getDepth() {
        return depth;
    }

    public AudioBuffer getKeyBuffer() {
        return key;
    }

    public void setDynamicGain(float dynamicGain) {
        if ( gainReductionIndicator == null ) return;
        // ideally we'd offload the log to another thread !!!
        gainReductionIndicator.setValue((float)(20*Math.log(dynamicGain)));
    }
}
