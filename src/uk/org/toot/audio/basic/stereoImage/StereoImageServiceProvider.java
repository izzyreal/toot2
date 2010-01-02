// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.basic.stereoImage;

import uk.org.toot.audio.spi.TootAudioServiceProvider;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.core.ChannelFormat;

import static uk.org.toot.misc.Localisation.*;

/**
 * Exposes stereo image manipulation as a plugin service
 * @author st
 *
 */
public class StereoImageServiceProvider extends TootAudioServiceProvider
{
    public StereoImageServiceProvider() {
        super(getString("Stereo.Image"), "0.1");
        String family = getString("Basic");
        addControls(StereoImageControls.class, StereoImageControls.STEREO_IMAGE, 
        		getString("Stereo.Image"), family, "0.1", ChannelFormat.STEREO, null);
        add(StereoImageProcess.class, getString("Stereo.Image"), family, "0.1");
    }

    public AudioProcess createProcessor(AudioControls c) {
        if ( c instanceof StereoImageProcessVariables ) {
            return new StereoImageProcess((StereoImageProcessVariables)c);
        } 
        return null; // caller then tries another provider
    }
}
