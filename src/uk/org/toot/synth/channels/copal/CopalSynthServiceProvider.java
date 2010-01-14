package uk.org.toot.synth.channels.copal;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.SynthChannelControls;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.COPAL_CHANNEL_ID;

public class CopalSynthServiceProvider extends TootSynthChannelServiceProvider
{
	public CopalSynthServiceProvider() {
		super("Cepal", "0.2");
		String name = CopalSynthControls.NAME;
		addControls(CopalSynthControls.class, COPAL_CHANNEL_ID, name, "", "0.2");
		add(CopalSynthChannel.class, name, "Cepal", "0.2");
	}

	public SynthChannel createSynthChannel(SynthChannelControls c) {
		if ( c instanceof CopalSynthControls ) {
			return new CopalSynthChannel((CopalSynthControls)c);
		}
		return null;
	}
}
