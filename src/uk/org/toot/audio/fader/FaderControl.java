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
 */
public abstract class FaderControl extends LawControl {
    /**
     * Constructs a new fader control object with the given parameters
     * @param initialValue the value that the control starts with when constructed
     */
    public FaderControl(int id, ControlLaw law, float initialValue) {
        super(id, "Level", law, 0.1f, initialValue);
    }

    /**
     * Implement this method to return the dsp value, i.e. the gain factor to multiply by
     * @return the gain factor
     */
    public abstract float getGain();
    
} // class FaderControl
