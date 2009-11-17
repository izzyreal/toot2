package uk.org.toot.synth.channels.whirl;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.synth.MonophonicSynthChannel;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.WHIRL_CHANNEL_ID;

public class WhirlSynthServiceProvider extends TootSynthChannelServiceProvider
{
	public WhirlSynthServiceProvider() {
		super("Whirl", "0.1");
		String name = WhirlSynthControls.NAME;
		addControls(WhirlSynthControls.class, WHIRL_CHANNEL_ID, name, "", "0.2");
		add(WhirlSynthChannel.class, name, "Whirl", "0.2");
	}

	public MonophonicSynthChannel createSynthChannel(CompoundControl c) {
		if ( c instanceof WhirlSynthControls ) {
			return new WhirlSynthChannel((WhirlSynthControls)c);
		}
		return null;
	}
}
