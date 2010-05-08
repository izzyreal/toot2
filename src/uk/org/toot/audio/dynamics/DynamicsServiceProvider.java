/* Copyright (C) 2006 Steve Taylor (toot.org.uk) */

package uk.org.toot.audio.dynamics;

import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.spi.TootAudioServiceProvider;

import static uk.org.toot.misc.Localisation.*;

public class DynamicsServiceProvider extends TootAudioServiceProvider
{
    public DynamicsServiceProvider() {
        super(getString("Dynamics"), "0.1");
		String family = description;
        addControls(Compressor.Controls.class, DynamicsIds.COMPRESSOR_ID, getString("Compressor"), family, "0.2");
        addControls(Limiter.Controls.class, DynamicsIds.LIMITER_ID,  getString("Limiter"), family, "0.2");
//        addControls(Expander.Controls.class, DynamicsIds.EXPANDER_ID, getString("Expander"), family, "0.1");
        addControls(Gate.Controls.class, DynamicsIds.GATE_ID, getString("Gate"), family, "0.1");
        addControls(MultiBandCompressor.DualBandControls.class, DynamicsIds.MULTI_BAND_COMPRESSOR_ID, getString("Dual.Band.Compressor"), family, "0.2");
//        addControls(MultiBandCompressor.QuadBandControls.class, DynamicsIds.MULTI_BAND_COMPRESSOR_ID, getString("Quad.Band.Compressor"), family, "0.1");
        addControls(TremoloControls.class, DynamicsIds.TREMOLO_ID, getString("Tremolo"), family, "0.1");

        add(Compressor.class, getString("Compressor"), family, "0.2");
        add(Limiter.class, getString("Limiter"), family, "0.2");
//        add(Expander.class, getString("Expander"), family, "0.1");
        add(Gate.class, getString("Gate"), family, "0.1");
        add(MultiBandCompressor.class, getString("Multi.Band.Compressor"), family, "0.2");
        add(TremoloProcess.class, getString("Tremolo"), family, "0.1");
    }

    public AudioProcess createProcessor(AudioControls c) {
 		if ( c instanceof MultiBandCompressor.MultiBandControls ) {
            return new MultiBandCompressor((MultiBandCompressor.MultiBandControls)c);
        }
 		if ( c instanceof TremoloProcess.Variables ) {
 			return new TremoloProcess((TremoloProcess.Variables)c);
 		}
 		if ( !(c instanceof DynamicsControls) ) return null;
        if ( c instanceof Compressor.Controls ) {
            return new Compressor((Compressor.Controls)c);
        } else if ( c instanceof Limiter.Controls ) {
            return new Limiter((Limiter.Controls)c);
        } else if ( c instanceof Expander.Controls ) {
            return new Expander((Expander.Controls)c);
        } else if ( c instanceof Gate.Controls ) {
            return new Gate((Gate.Controls)c);
        }
        return null; // caller then tries another provider
    }
}
