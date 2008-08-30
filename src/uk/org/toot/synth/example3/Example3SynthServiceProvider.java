package uk.org.toot.synth.example3;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.spi.TootSynthServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.EXAMPLE_3_SYNTH_ID;

public class Example3SynthServiceProvider extends TootSynthServiceProvider
{

	public Example3SynthServiceProvider() {
		super("Example Synth 3", "0.1");
		String name = Example3SynthControls.NAME;
		addControls(Example3SynthControls.class, EXAMPLE_3_SYNTH_ID, name, "", "0.1");
		add(Example3SynthChannel.class, name, "Example Synth 3", "0.1");
	}

	public SynthChannel createSynthChannel(SynthControls c) {
		if ( c instanceof Example3SynthControls ) {
			return new Example3SynthChannel((Example3SynthControls)c);
		}
		return null;
	}

}
