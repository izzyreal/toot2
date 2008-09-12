package uk.org.toot.synth.channels.pluckedString2;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.spi.TootSynthServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.EXAMPLE_3_SYNTH_ID;

public class PluckedString2SynthServiceProvider extends TootSynthServiceProvider
{

	public PluckedString2SynthServiceProvider() {
		super("Plucked String 2", "0.1");
		String name = PluckedString2SynthControls.NAME;
		addControls(PluckedString2SynthControls.class, EXAMPLE_3_SYNTH_ID, name, "", "0.1");
		add(PluckedString2SynthChannel.class, name, "Plucked String 2", "0.1");
	}

	public SynthChannel createSynthChannel(SynthControls c) {
		if ( c instanceof PluckedString2SynthControls ) {
			return new PluckedString2SynthChannel((PluckedString2SynthControls)c);
		}
		return null;
	}

}
