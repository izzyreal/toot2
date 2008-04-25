package uk.org.toot.synth.example1;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.spi.TootSynthServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.EXAMPLE_1_SYNTH_ID;

public class ExampleSynthServiceProvider extends TootSynthServiceProvider
{

	public ExampleSynthServiceProvider() {
		super("Example Synth 1", "0.1");
		addControls(ExampleSynthControls.class, EXAMPLE_1_SYNTH_ID, "Ex1Synth", "", "0.1");
		add(ExampleSynthChannel.class, "Ex1Synth", "Example Synth 1", "0.1");
	}

	public SynthChannel createSynthChannel(SynthControls c) {
		if ( c instanceof ExampleSynthControls ) {
			return new ExampleSynthChannel((ExampleSynthControls)c);
		}
		return null;
	}

}
