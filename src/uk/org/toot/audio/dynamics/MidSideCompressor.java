// Copyright (C) 2006, 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.dynamics;

import org.tritonus.share.sampled.TVolumeUtils;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.control.Control;

import static uk.org.toot.audio.dynamics.DynamicsControlIds.*;
import static uk.org.toot.misc.Localisation.*;

public class MidSideCompressor extends MidSideDynamicsProcess
{
    public MidSideCompressor(Variables vars) {
		super(vars, false); // RMS, not peak
    }

    protected float function(int i, float value) {
        if ( value > threshold[i] ) { // -knee/2 etc. interpolate around knee !!!
        	float overdB = (float)TVolumeUtils.lin2log(value/threshold[i]);
            return (float)TVolumeUtils.log2lin(overdB * ratio2[i]);
        }
        return 1f;
    }
    
    public static class Controls extends AudioControls
        implements MidSideDynamicsProcess.Variables
    {
        private Compressor.Controls mid, side;

        private float[] threshold = new float[2];
        private float[] thresholddB = new float[2];
        private float[] knee = new float[2];
        private float[] ratio = new float[2];
        private float[] attack = new float[2];
        private int[] hold = new int[2];
        private float[] release = new float[2];
        private float[] gain = new float[2];
        
        public Controls() {
            super(DynamicsIds.MID_SIDE_COMPRESSOR_ID, getString("Mid-Side Compressor"));
            add(mid = new Compressor.Controls(getString("Mid"), 0) {
                protected boolean hasKey() { return false; }
            });
            add(side = new Compressor.Controls(getString("Side"), 0x10) {
                protected boolean hasKey() { return false; }
            });
            deriveIndependentVariables();
            deriveDependentVariables(); // at default 44100 sample rate
        }
        
        protected void deriveIndependentVariables() {
            threshold[0] = mid.getThreshold();
            threshold[1] = side.getThreshold();
            thresholddB[0] = mid.getThresholddB();
            thresholddB[1] = side.getThresholddB();
            ratio[0] = mid.getRatio();
            ratio[1] = side.getRatio();
            gain[0] = mid.getGain();
            gain[1] = side.getGain();
        }

        protected void deriveDependentVariables() {
            attack[0] = mid.getAttack();
            attack[1] = side.getAttack();
            release[0] = mid.getRelease();
            release[1] = side.getRelease();           
        }
        
        @Override
        protected void derive(Control c) {
            int id = c.getId();
            int n = 0;
            Compressor.Controls cc = mid;
            if ( id >= 0x10 ) {
                n = 1;
                cc = side;
                id -= 0x10;
            }
            switch ( id ) {
            case THRESHOLD: 
                threshold[n] = cc.getThreshold();
                thresholddB[n] = cc.getThresholddB(); break;
            case RATIO: ratio[n] = cc.getRatio(); break;
            case ATTACK: attack[n] = cc.getAttack(); break;
            //case HOLD: hold[n] = cc.getHold(); break;
            case RELEASE: release[n] = cc.getRelease(); break;
            case GAIN: gain[n] = cc.getGain(); break;
            //case DEPTH: depth[n] = cc.getDepth(); break;
            //case KEY: key[n] = cc.getKeyBuffer(); break; 
            }
        }
        
        public void update(float sampleRate) {
            mid.update(sampleRate);
            side.update(sampleRate);
            deriveDependentVariables();
        }

        public float[] getThreshold() {
            return threshold;
        }

        public float[] getThresholddB() {
            return thresholddB;
        }

        public float[] getKnee() {
            return knee;
        }

        public float[] getRatio() {
            return ratio;
        }

        public float[] getAttack() {
            return attack;
        }

        public int[] getHold() {
            return hold;
        }

        public float[] getRelease() {
            return release;
        }

        public float[] getGain() {
            return gain;
        }

        public void setDynamicGain(float gainM, float gainS) {
            mid.setDynamicGain(gainM);
            side.setDynamicGain(gainS);
        }
    }
}
