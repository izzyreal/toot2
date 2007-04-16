// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.core;

import uk.org.toot.control.*;
import uk.org.toot.control.CompoundControl;
import static uk.org.toot.audio.id.ProviderId.USE_PARENT_PROVIDER_ID;

/**
 * AudioControls are composite Controls which control an AudioProcess, defined
 * in 'user' terms.
 */
public class AudioControls extends CompoundControl
{
	private CompoundControl.BypassControl bypassControl = null;

    int providerId = 0;

    public AudioControls(int id, String name) {
        this(id, name, 127);
    }

    public AudioControls(int id, String name, int bypassId) {
        super(id, name);
        if ( canBypass() ) {
            bypassControl = new BypassControl(bypassId);
            add(bypassControl);
        }
    }

    public boolean hasOrderedFrequencies() { return false; }

	public boolean canBypass() { return false; }

    public void setBypassed(boolean state) {
        if ( canBypass() && bypassControl != null ) {
        	bypassControl.setValue(state);
        }
    }

    public boolean isBypassed() {
        if ( bypassControl == null ) return false;
        return bypassControl.getValue();
    }

    public BooleanControl getBypassControl() {
        return bypassControl;
    }

    public int getProviderId() {
        if ( providerId == USE_PARENT_PROVIDER_ID ) {
            return super.getProviderId(); // CoR
        }
        return providerId;
    }

    protected void setProviderId(int id) {
        providerId = id;
    }
}
