// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.tool;

import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.spi.TootAudioServiceProvider;

import static uk.org.toot.localisation.Localisation.*;

public class ToolServiceProvider extends TootAudioServiceProvider
{
    public ToolServiceProvider() {
        super(getString("Tools"), "0.1");
		String family = description;
        addControls(DenormalControls.class, ToolIds.DENORMAL_ID, "Denormaliser", family, "0.1");

        add(DenormalProcess.class, "Denormaliser", family, "0.1");

    }

    public AudioProcess createProcessor(AudioControls c) {
        if ( c instanceof DenormalControls ) {
            return new DenormalProcess((DenormalControls)c);
        }
        return null; // caller then tries another provider
    }
}
