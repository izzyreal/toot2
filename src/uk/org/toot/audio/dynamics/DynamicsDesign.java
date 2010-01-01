/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import java.util.Observer;
import java.util.Observable;
import uk.org.toot.audio.core.KVolumeUtils;
import uk.org.toot.control.Control; // !!! !!! poor coupling
import uk.org.toot.misc.IObservable;
import org.tritonus.share.sampled.TVolumeUtils;

import static uk.org.toot.audio.dynamics.DynamicsControlIds.*;

public class DynamicsDesign implements DynamicsProcess.ProcessVariables
{
    protected DesignVariables designVars;
    private float threshold, knee, attack, release, gain, depth;
    private int hold;
    private float sampleRate;

    public DynamicsDesign(DesignVariables vars) {
        designVars = vars;
        // observe vars to re-derive variables, removeObserver how?
        designVars.addObserver(
            new Observer() {
            	public void update(Observable obs, Object obj) {
                	Control c = (Control)obj;
                    if ( c.isIndicator() ) return;
                	switch ( c.getId() % 10 ) { // %10 hack for multi-band
		                case THRESHOLD: deriveThreshold(); break;
		                case KNEE: deriveKnee(); break;
		                case ATTACK: deriveAttack(); break;
		                case HOLD: deriveHold(); break;
		                case RELEASE: deriveRelease(); break;
		                case GAIN: deriveGain(); break;
		                case DEPTH: deriveDepth(); break;
                	}
            	}
        	}
        );
        deriveSampleRateIndependentVariables();
	}

    protected void deriveSampleRateIndependentVariables() {
        deriveThreshold();
        deriveKnee();
        deriveGain();
        deriveDepth();
    }

	public void update(float sampleRate) {
        if ( sampleRate != this.sampleRate ) {
            this.sampleRate = sampleRate;
            // derive sample rate dependent variables
            deriveAttack();
            deriveHold();
            deriveRelease();
        }
    }

    protected void deriveThreshold() {
		threshold = (float)KVolumeUtils.log2lin(designVars.getThresholddB());
//        System.out.println("threshold="+threshold);
    }

    protected void deriveKnee() {
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
        attack = deriveTimeFactor(designVars.getAttackMilliseconds());
//        System.out.println("attack="+attack);
    }

    protected void deriveHold() {
        hold = (int)(designVars.getHoldMilliseconds()*sampleRate*0.001f);
//        System.out.println("hold="+hold);

    }

    protected void deriveRelease() {
        release = deriveTimeFactor(designVars.getReleaseMilliseconds());
//        System.out.println("release="+release);
    }

    protected void deriveGain() {
        gain = (float)TVolumeUtils.log2lin(designVars.getGaindB());
//        System.out.println("gain="+gain);
    }

    protected void deriveDepth() {
        depth = (float)TVolumeUtils.log2lin(designVars.getDepthdB());
    }

    public boolean isBypassed() {
        return designVars.isBypassed();
    }

    public float getThreshold() {
        return threshold;
    }

    public float getThresholddB() {
        return designVars.getThresholddB();
    }

    public float getRatio() {
        return designVars.getRatio();
    }

    public float getKnee() {
        return knee;
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

    public void setDynamicGain(float dynamicGain) {
        // ideally we'd offload the log to another thread !!!
        designVars.setGainReduction((float)(20*Math.log(dynamicGain)));
    }

    public static interface DesignVariables extends IObservable
    {
        boolean isBypassed();
        float getThresholddB();
        float getRatio();
        float getKneedB();
        float getAttackMilliseconds();
        float getHoldMilliseconds(); // gate
        float getReleaseMilliseconds();
        float getGaindB(); // compressor
        float getDepthdB(); // gate
    //        float getMix(); // dry/wet mix
    	void setGainReduction(float dB);
    }
}
