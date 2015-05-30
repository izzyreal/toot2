// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.fader;

import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.LawControl;

/**
 * A FaderControl extends LawControl and differs from FloatControl in that
 * it generally uses a log law that needs to go to -infinity.
 * This class also is a hint for UI generation
 * @see uk.org.toot.audio.mixer.MixControls.GainControl
 */
public abstract class FaderControl extends LawControl {
    /**
     * The gain used by the dsp thread.
     * Initialise in the constructor of subclasses.
     * Override setValue() in subclasses to derive gain when value is changed.
     */
    protected float gain;

    /**
     * Constructs a new fader control object with the given parameters
     * Override to initialise gain
     * @param id the control id for automation purposed
     * @param law the ControlLaw to be used
     * @param initialValue the value that the control starts with when constructed
     */
    public FaderControl(int id, ControlLaw law, float initialValue) {
        super(id, "Level", law, 0.1f, initialValue);
    }

    /**
     * Called by the dsp thread so efficient and final so you can't make it less efficent
     * @return the current gain
     */
    public final float getGain() {
        return gain;
    }
    
} // class FaderControl
