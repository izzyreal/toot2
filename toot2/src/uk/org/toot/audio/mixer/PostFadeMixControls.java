/* Copyright Steve Taylor 2006 */

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
