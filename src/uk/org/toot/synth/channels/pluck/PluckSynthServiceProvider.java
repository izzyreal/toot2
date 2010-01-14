package uk.org.toot.synth.channels.pluck;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.SynthChannelControls;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;

import static uk.org.toot.synth.id.TootSynthControlsId.PLUCK_CHANNEL_ID;

public class PluckSynthServiceProvider extends TootSynthChannelServiceProvider
{
	public PluckSynthServiceProvider() {
		super("Pluck", "0.1");
		String name = PluckSynthControls.NAME;
		addControls(PluckSynthControls.class, PLUCK_CHANNEL_ID, name, "", "0.1");
		add(PluckSynthChannel.class, name, "Pluck", "0.1");
	}

	public SynthChannel createSynthChannel(SynthChannelControls c) {
		if ( c instanceof PluckSynthControls ) {
			return new PluckSynthChannel((PluckSynthControls)c);
		}
		return null;
	}
}
