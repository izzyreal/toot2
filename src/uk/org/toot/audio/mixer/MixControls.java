// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.mixer;

import java.awt.Color;
import uk.org.toot.control.*;
import uk.org.toot.audio.core.*;
import static uk.org.toot.audio.mixer.MixerControlsIds.*;
import uk.org.toot.audio.fader.*;

import static uk.org.toot.audio.mixer.MixControlIds.*;
import static uk.org.toot.misc.Localisation.*;

/**
 * MixControls are the composite Controls for a MixProcess.
 */
public class MixControls extends AudioControls
    implements MixVariables
{
    private static final float HALF_ROOT_TWO = (float)(Math.sqrt(2) / 2);
    private BooleanControl soloControl = null;

    private BooleanControl muteControl;

    private GainControl gainControl;

    private LCRControl lcrControl;

    private FrontRearControl frontRearControl;
    /**
     * @supplierCardinality 1
     * @link aggregationByValue 
     */
    private BusControls busControls;

    /**
     * @link aggregation
     * @supplierCardinality 1 
     */
    protected MixerControls mixerControls;

    private boolean isMaster;
    private int channelCount;
    // create the bus controls for the 'crossbar' of strip/bus
    public MixControls(MixerControls mixerControls,
        					int stripId,
                            BusControls busControls,
                            boolean isMaster) {
		super(busControls.getId(), busControls.getName());
        this.mixerControls = mixerControls;
        this.busControls = busControls;
        this.isMaster = isMaster;
        int busId = busControls.getId();

        ChannelFormat format = getChannelFormat();
        channelCount = format.getCount();
        // LFE
        if ( format.getLFE() >= 0 ) {
        }
        // front/rear
        if ( channelCount >= 4 ) {
            frontRearControl = new FrontRearControl();
            add(frontRearControl);
        }
        // divergence (Center <-> Left/Right) ?
        if ( format.getCenter() >= 0 && channelCount > 1 ) {
        }
        // pan/balance
        if ( channelCount > 1 ) {
			if ( stripId == CHANNEL_STRIP ) {
    	        PanControl pc =  new PanControl();
   				add(pc); // pan/bal STEREO ONLY !!!
            	lcrControl = pc;
        	} else {
	            BalanceControl bc = new BalanceControl();
    	        add(bc);
        	    lcrControl = bc;
        	}
        }

		ControlRow enables = new ControlRow();
        // master mixes show the bus solo indicator, not a control
		if ( isMaster ) {
   		    enables.add(busControls.getSoloIndicator());
	    } else {
        	enables.add(soloControl = createSoloControl());
        	soloControl.addObserver(busControls); // !!! deleteObserver? !!! !!!
        }
        // all busses have a mute control
        enables.add(muteControl = createMuteControl());
        add(enables);
        // the main bus has an internal route unless it's the main strip or an aux strip
        // not really our concern, the MainMixControls subclass provides it
        // called here so before fader
        if ( busId == MAIN_BUS ) {
            EnumControl routeControl = createRouteControl(stripId);
            if ( routeControl != null ) {
       			add(routeControl);
            }
        }
        // all busses have a fader
        float initialdB = ( (busId == AUX_BUS ||
        	 				 busId == FX_BUS) && !isMaster ) ?
                            -FaderLaw.ATTENUATION_CUTOFF : 0f;
        gainControl = new GainControl(initialdB);
        gainControl.setInsertColor(isMaster ? Color.BLUE.darker() : Color.black);
        add(gainControl);
    }

    public boolean isMaster() { return isMaster; }

    public ChannelFormat getChannelFormat() {
        return busControls.getChannelFormat();
    }

    public boolean isAlwaysVertical() { return true; }

    public boolean canBeDeleted() { return false; }

    public boolean hasPresets() { return false; }

    public boolean isSolo() {
        return soloControl == null ? hasSolo() : soloControl.getValue();
    }

    public boolean isMute() {
        return muteControl.getValue();
    }

    public boolean isEnabled() {
		return !(isMute() || isSolo() != hasSolo());
    }

    public boolean hasSolo() {
        return busControls.hasSolo();
    }

    public float getGain() {
        return gainControl.getGain();
    }

    public void getChannelGains(float[] dest) {
        float g = getGain();
        switch ( channelCount ) {
        case 6: // FIVE_1
//        	dest[5] = g * getLFE();
//        	dest[4] = g * getCenter();
			// intentional fall-through
        case 4: // QUAD
        	// rear
            float r = g * frontRearControl.getRear();
        	dest[3] = r * lcrControl.getRight();
        	dest[2] = r * lcrControl.getLeft();
            // front
            float f = g * frontRearControl.getFront();
    	    dest[1] = f * lcrControl.getRight();
	        dest[0] = f * lcrControl.getLeft();
            break;
        case 2: // STEREO
    	    dest[1] = g * lcrControl.getRight();
	        dest[0] = g * lcrControl.getLeft();
            break;
        case 1: // MONO
        	dest[0] = 1f;
        	break;
        }
    }

    protected EnumControl createRouteControl(int stripId) {
        return null;
    }

    protected BooleanControl createMuteControl() {
        BooleanControl c = new BooleanControl(MUTE, getString("Mute"), false);
        c.setAnnotation(c.getName().substring(0, 1));
        c.setStateColor(true, Color.orange);
        return c;
    }

    protected BooleanControl createSoloControl() {
        BooleanControl c = new BooleanControl(SOLO, getString("Solo"), false);
        c.setAnnotation(c.getName().substring(0, 1));
        c.setStateColor(true, Color.green);
        return c;
    }

    public BooleanControl getMuteControl() {
        return muteControl;
    }

    public BooleanControl getSoloControl() {
        return soloControl;
    }

    public GainControl getGainControl() {
        return gainControl;
    }

    /**
     * An abstract implementation of a Left/Center/Right control such as
     * a pan or balance control.
     */
    public abstract static class LCRControl extends FloatControl
    {
        // this isn't a pan/balance law, just a linear 0..1 control law
        protected static final LinearLaw linLaw = new LinearLaw(0f, 1f, "");

        private static final String[] presetNames = {
            getString("Center"), getString("Left"), getString("Right")
        };

        public LCRControl(String name, ControlLaw law, float precision, float initialValue) {
            super(LCR, name, law, precision, initialValue);
            setInsertColor(java.awt.Color.pink);
        }

        public abstract float getLeft();
        public abstract float getRight();

        public String[] getPresetNames() {
            return presetNames;
        }

        public void applyPreset(String presetName) {
            if ( presetName.equals(getString("Center")) ) {
                setValue(0.5f);
            } else if ( presetName.equals(getString("Left")) ) {
                setValue(0f);
            } else if ( presetName.equals(getString("Right")) ) {
                setValue(1f);
            }
        }
    }


    /**
     * A PanControl implements stereo pan.
     */
    public static class PanControl extends LCRControl
    {
        private float left = HALF_ROOT_TWO;		// -3dB centre
        private float right = HALF_ROOT_TWO; 	// -3dB centre

        public PanControl() {
            super(getString("Pan"), linLaw,
                0.01f,   	// precision
                0.5f		// initially center
                );
        }

        public float getLeft() { return left; }

        public float getRight() { return right; }

        public void setValue(float value) {
            super.setValue(value);
            // as AMEI / MMA RP-036
            left = (float)Math.cos(Math.PI / 2 * value);
            right = (float)Math.sin(Math.PI / 2 * value);
        }

        public float getPan() { return getValue(); }
    }



    /**
     * A BalanceControl implements stereo balance.
     */
    public static class BalanceControl extends LCRControl
    {
        private float left = 1;
        private float right = 1;

        public BalanceControl() {
            super(getString("Balance"), linLaw,
                0.01f,   	// precision
                0.5f		// initially center
                );
        }

        public float getLeft() { return left; }

        public float getRight() { return right; }

        public void setValue(float value) {
            super.setValue(value);
            left = value < 0.5f ? 1f : 2 * (1 -value);
            right = value > 0.5f ? 1f : 2 * value;
    //        System.out.println(getControlPath()+": "+value+" "+left+", "+right);
        }

        public float getBalance() { return getValue(); }
    }

    /**
     * A FrontRearControl.
     */
    public static class FrontRearControl extends FloatControl
    {
        private float front = HALF_ROOT_TWO; // -3dB center
        private float rear = HALF_ROOT_TWO;

        // this isn't a pan/balance law, just a linear 0..1 control law
        protected static final LinearLaw linLaw = new LinearLaw(0f, 1f, "");

        private static final String[] presetNames = {
            getString("Front"), getString("Middle"), getString("Rear")
        };

        public FrontRearControl() {
            super(FRONT_SURROUND, getString("F.S"), linLaw,
                0.01f,   	// precision
                0.5f		// initially middle
                );
            setInsertColor(Color.GREEN.darker());
        }

        public float getFront() { return front; }

        public float getRear() { return rear; }

        public void setValue(float value) {
            super.setValue(value);
            // as AMEI / MMA RP-036
            front = (float)Math.cos(Math.PI / 2 * value);
            rear = (float)Math.sin(Math.PI / 2 * value);
        }

        public String[] getPresetNames() {
            return presetNames;
        }

        public void applyPreset(String presetName) {
            if ( presetName.equals(getString("Middle")) ) {
                setValue(0.5f);
            } else if ( presetName.equals(getString("Front")) ) {
                setValue(0f);
            } else if ( presetName.equals(getString("Rear")) ) {
                setValue(1f);
            }
        }
    }

    /**
     * A GainControl is a FaderControl which implements GainVariables.
     */
    public static class GainControl extends FaderControl {
        private float gain;

        public GainControl(float initialdB) {
            super(GAIN, FaderLaw.BROADCAST, initialdB);
    	    gain = (float)Math.pow(10.0, initialdB/20.0);
            if ( initialdB <= -FaderLaw.ATTENUATION_CUTOFF ) {
                gain = 0f;
    //                System.out.println("Zero gain for "+getControlPath());
            }
    	}

        public void setValue(float value) {
            super.setValue(value);
            if ( value <= -FaderLaw.ATTENUATION_CUTOFF ) {
                gain = 0f;
    //                System.out.println("Zero gain for "+getControlPath());
            } else {
        	    gain = (float)Math.pow(10.0, value/20.0);
            }
        }

        public float getGain() {
            return gain;
        }
    }
}


