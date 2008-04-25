// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.spi.TootAudioServiceProvider;

import static uk.org.toot.localisation.Localisation.*;

public class DelayServiceProvider extends TootAudioServiceProvider
{
    public DelayServiceProvider() {
        super(getString("Delay"), "0.1");
		String family = description;
        addControls(
            ModulatedDelayControls.class,
            DelayIds.MODULATED_DELAY_ID,
            getString("Modulated.Delay"),
            family,
            "0.1");
        addControls(
            StereoModulatedDelayControls.class,
            DelayIds.STEREO_MODULATED_DELAY_ID,
            getString("Stereo.Modulated.Delay"),
            family,
            "0.1",
            ChannelFormat.STEREO);
        addControls(
            MultiTapDelayStereoControls.class,
            DelayIds.MULTI_TAP_DELAY_ID,
            getString("Stereo.Multi.Tap.Delay"),
            family,
            "0.1",
            ChannelFormat.STEREO);
//        addControls(RoomSimulatorControls.class, DelayIds.ROOM_SIMULATOR, "Room Simulator", family, "0.1");

        add(ModulatedDelayProcess.class, getString("Modulated.Delay"), family, "0.1");
        add(StereoModulatedDelayProcess.class, getString("Stereo.Modulated.Delay"), family, "0.1");
        add(MultiTapDelayProcess.class, getString("Multi.Tap.Delay"), family, "0.1");
    }

    public AudioProcess createProcessor(AudioControls c) {
        if ( c instanceof StereoModulatedDelayVariables ) {
            return new StereoModulatedDelayProcess((StereoModulatedDelayVariables)c);
        } else if ( c instanceof ModulatedDelayVariables ) {
            return new ModulatedDelayProcess((ModulatedDelayVariables)c);
        } else if ( c instanceof MultiTapDelayVariables ) {
            return new MultiTapDelayProcess((MultiTapDelayVariables)c);
        } else if ( c instanceof RoomSimulatorControls ) {
            // ultimately should be composed of multiple designs
            // but this should get the Room Simulator working
            // albeit hardcoded to a single ImageSourceDesign
            return new MultiTapDelayProcess(new ImageSourceDesign((RoomSimulatorControls)c));
        }
        return null; // caller then tries another provider
    }
}
