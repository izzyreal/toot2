package uk.org.toot.synth.channels.total;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.SynthChannelControls;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.TOTAL_CHANNEL_ID;

public class TotalSynthServiceProvider extends TootSynthChannelServiceProvider
{
	public TotalSynthServiceProvider() {
		super("Total", "0.1");
		String name = TotalSynthControls.NAME;
		addControls(TotalSynthControls.class, TOTAL_CHANNEL_ID, name, "", "0.1");
		add(TotalSynthChannel.class, name, "Total", "0.1");
	}

	public SynthChannel createSynthChannel(SynthChannelControls c) {
		if ( c instanceof TotalSynthControls ) {
			return new TotalSynthChannel((TotalSynthControls)c);
		}
		return null;
	}
}
