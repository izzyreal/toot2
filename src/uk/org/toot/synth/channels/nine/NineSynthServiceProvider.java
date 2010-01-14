package uk.org.toot.synth.channels.nine;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.SynthChannelControls;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.NINE_CHANNEL_ID;

public class NineSynthServiceProvider extends TootSynthChannelServiceProvider
{
	public NineSynthServiceProvider() {
		super("Nine", "0.1");
		String name = NineSynthControls.NAME;
		addControls(NineSynthControls.class, NINE_CHANNEL_ID, name, "", "0.1");
		add(NineSynthChannel.class, name, "Nine", "0.1");
	}

	public SynthChannel createSynthChannel(SynthChannelControls c) {
		if ( c instanceof NineSynthControls ) {
			return new NineSynthChannel((NineSynthControls)c);
		}
		return null;
	}
}
