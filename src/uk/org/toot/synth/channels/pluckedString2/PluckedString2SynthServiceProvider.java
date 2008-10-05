package uk.org.toot.synth.channels.pluckedString2;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.EXAMPLE_3_CHANNEL_ID;

public class PluckedString2SynthServiceProvider extends TootSynthChannelServiceProvider
{

	public PluckedString2SynthServiceProvider() {
		super("Plucked String 2", "0.1");
		String name = PluckedString2SynthControls.NAME;
		addControls(PluckedString2SynthControls.class, EXAMPLE_3_CHANNEL_ID, name, "", "0.1");
		add(PluckedString2SynthChannel.class, name, "Plucked String 2", "0.1");
	}

	public SynthChannel createSynthChannel(CompoundControl c) {
		if ( c instanceof PluckedString2SynthControls ) {
			return new PluckedString2SynthChannel((PluckedString2SynthControls)c);
		}
		return null;
	}

}
