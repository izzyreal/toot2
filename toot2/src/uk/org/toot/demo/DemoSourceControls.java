/* Copyright Steve Taylor 2006 */

package uk.org.toot.demo;

import uk.org.toot.control.*;
import uk.org.toot.audio.core.*;
import uk.org.toot.audio.mixer.*;
import uk.org.toot.audio.core.AudioBuffer;

// Doesn't really need to extend CompoundControl
// unless automatic UI generation is required.
// Defaults to 'm/s demo' in folder 'demo' mapped to the specifed mixer input
// strip routed to the specified mixer group strip
public class DemoSourceControls extends CompoundControl
{
    private MixerControls mixerControls;
    private MainMixControls mainMixControls;
    BooleanControl muteControl;
    BooleanControl soloControl;
    EnumControl routeControl;
    private AudioBuffer.MetaInfo info;
    private String name;

    public DemoSourceControls(MixerControls mixerControls, String stripName, String groupName) {
        super(-1, ""); // only needed because we extend CompoundControl!
        this.mixerControls = mixerControls;
        // we're interested in the Main bus of the mixer
        // and mix control modules are named after the bus they mix to
        mainMixControls =
            (MainMixControls)mixerControls.getStripControls(stripName)
            .find(mixerControls.getMainBusControls().getName());
        // we 'borrow' the mute and solo controls of the strip we're connected to
        muteControl = mainMixControls.getMuteControl();
        soloControl = mainMixControls.getSoloControl();
        // we also perform some trivial fader automation
        // mainly to demonstrate how easy it is
        mainMixControls.getGainControl().setValue(-10); // set fader dB
        // add them to us if we extend CompoundControl!
        add(muteControl);
        add(soloControl);
        // we also 'borrow' the route control which we need
        // to sync Frinika folders to mixer group strips
        routeControl = mainMixControls.getRouteControl();
        // set the default Frinika lane and folder names
        setName("m/s demo");
        setGroup(groupName, "demo");
    }

    public void setName(String aName) {
        name = aName;
        // update the MetaInfo that will be used by the DemoSourceProcess
        info = new AudioBuffer.MetaInfo(name);
    }

    public void setGroup(String groupName, String folderName) {
        // route our mixer strip to the specified group strip
        routeControl.setValue(groupName);
        // set the group source name to the Frinika folder lane name
        // this should really be done when Frinika creates a folder
        mixerControls.getStripControls(groupName).setSourceLabel(folderName);
    }

    // --- belows is the API the model process requires ----------------------

    public boolean isEnabled() {
        // delegate to the bus mix controls of the mixer strip we're connected to
        return mainMixControls.isEnabled();
    }

    public AudioBuffer.MetaInfo getMetaInfo() {
        return info;
    }
}
