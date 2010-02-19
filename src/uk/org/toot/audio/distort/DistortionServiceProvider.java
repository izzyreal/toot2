// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.distort;

import uk.org.toot.audio.spi.TootAudioServiceProvider;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioControls;

import static uk.org.toot.misc.Localisation.*;

/**
 * Exposes distortion as a plugin service
 * @author st
 */
public class DistortionServiceProvider extends TootAudioServiceProvider
{
    public DistortionServiceProvider() {
        super(getString("Distortion"), "0.1");
        String family = description;
        addControls(Distort1Controls.class, DistortionIds.DISTORT1, 
        	"OD", family, "0.1");
        addControls(BitCrusherControls.class, DistortionIds.BIT_CRUSH, getString("BitCrush"), 
        		family, "0.1");

        add(Distort1Process.class, "OD", family, "0.1");
        add(BitCrusherProcess.class, getString("BitCrush"), family, "0.1");
    }

    public AudioProcess createProcessor(AudioControls c) {
        if ( c instanceof Distort1Variables ) {
            return new Distort1Process((Distort1Variables)c);
        } else if ( c instanceof BitCrusherControls ) {
        	return new BitCrusherProcess((BitCrusherControls)c);
        }
        return null; // caller then tries another provider
    }
}
