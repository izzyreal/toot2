package uk.org.toot.synth.channels.total;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.synth.PolyphonicSynthChannel;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.TOTAL_CHANNEL_ID;

public class TotalSynthServiceProvider extends TootSynthChannelServiceProvider
{
	public TotalSynthServiceProvider() {
		super("Whirl", "0.1");
		String name = TotalSynthControls.NAME;
		addControls(TotalSynthControls.class, TOTAL_CHANNEL_ID, name, "", "0.1");
		add(TotalSynthChannel.class, name, "Whirl", "0.2");
	}

	public PolyphonicSynthChannel createSynthChannel(CompoundControl c) {
		if ( c instanceof TotalSynthControls ) {
			return new TotalSynthChannel((TotalSynthControls)c);
		}
		return null;
	}
}
