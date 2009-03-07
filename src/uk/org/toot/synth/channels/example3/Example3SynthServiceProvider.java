package uk.org.toot.synth.channels.example3;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.synth.PolyphonicSynthChannel;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.EXAMPLE_3_CHANNEL_ID;

public class Example3SynthServiceProvider extends TootSynthChannelServiceProvider
{

	public Example3SynthServiceProvider() {
		super("Example Synth 3", "0.1");
		String name = Example3SynthControls.NAME;
		addControls(Example3SynthControls.class, EXAMPLE_3_CHANNEL_ID, name, "", "0.1");
		add(Example3SynthChannel.class, name, "Example Synth 3", "0.1");
	}

	public PolyphonicSynthChannel createSynthChannel(CompoundControl c) {
		if ( c instanceof Example3SynthControls ) {
			return new Example3SynthChannel((Example3SynthControls)c);
		}
		return null;
	}

}
