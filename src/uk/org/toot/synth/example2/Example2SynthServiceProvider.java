package uk.org.toot.synth.example2;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.spi.TootSynthServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.EXAMPLE_2_SYNTH_ID;

public class Example2SynthServiceProvider extends TootSynthServiceProvider
{

	public Example2SynthServiceProvider() {
		super("Example Synth 2", "0.1");
		addControls(Example2SynthControls.class, EXAMPLE_2_SYNTH_ID, "Ex2Synth", "", "0.1");
		add(Example2SynthChannel.class, "Ex2Synth", "Example Synth 2", "0.1");
	}

	public SynthChannel createSynthChannel(SynthControls c) {
		if ( c instanceof Example2SynthControls ) {
			return new Example2SynthChannel((Example2SynthControls)c);
		}
		return null;
	}

}
