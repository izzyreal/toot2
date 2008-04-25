// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.eq;

import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.spi.TootAudioServiceProvider;

import static uk.org.toot.localisation.Localisation.*;

/**
 * The ServiceProvider for Toot EQ.
 */
public class EQServiceProvider extends TootAudioServiceProvider
{
    public EQServiceProvider() {
        super(getString("EQ"), "0.3");
		String family = description;
        addControls(ParametricEQ.Controls.class, EQIds.PARAMETRIC_EQ_ID, getString("Parametric.EQ"), family, "0.2");
        addControls(GraphicEQ.Controls.class, EQIds.GRAPHIC_EQ_ID, getString("Graphic.EQ"), family, "0.2");
        addControls(CutEQ.Controls.class, EQIds.CUT_EQ_ID, getString("Cut.EQ"), family, "0.1");

        add(ParametricEQ.class, getString("Parametric.EQ"), family, "0.2");
        add(GraphicEQ.class, getString("Graphic.EQ"), family, "0.2");
        add(CutEQ.class, getString("Cut.EQ"), family, "0.1");
    }

    public AudioProcess createProcessor(AudioControls c) {
        if ( c instanceof ParametricEQ.Controls ) {
            return new ParametricEQ((ParametricEQ.Controls)c);
        } else if ( c instanceof GraphicEQ.Controls ) {
            return new GraphicEQ((GraphicEQ.Controls)c);
        } else if ( c instanceof CutEQ.Controls ) {
            return new CutEQ((CutEQ.Controls)c);
        }
        return null; // caller then tries another provider
    }
}
