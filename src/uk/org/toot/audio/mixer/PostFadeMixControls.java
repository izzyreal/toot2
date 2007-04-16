// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.mixer;

public class PostFadeMixControls extends MixControls
{
    /**
     * @link aggregation
     * @supplierCardinality 1 
     */
    private MainMixControls mainMixControls;

    public PostFadeMixControls(MixerControls mixerControls,
        					int stripId,
                            BusControls busControls,
                            MainMixControls mainMixControls) {
        super(mixerControls, stripId, busControls,false);
        this.mainMixControls = mainMixControls;
    }

    // post fade sends are disabled when main is disabled.
    public boolean isEnabled() {
        return super.isEnabled() && mainMixControls.isEnabled();
    }

    // post-fade
    public float getGain() {
        return super.getGain() * mainMixControls.getGain();
    }
}
