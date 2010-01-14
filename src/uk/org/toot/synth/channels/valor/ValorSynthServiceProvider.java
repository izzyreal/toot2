package uk.org.toot.synth.channels.valor;

import uk.org.toot.synth.PolyphonicSynthChannel;
import uk.org.toot.synth.SynthChannelControls;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;
import static uk.org.toot.synth.id.TootSynthControlsId.VALOR_CHANNEL_ID;

public class ValorSynthServiceProvider extends TootSynthChannelServiceProvider
{
	public ValorSynthServiceProvider() {
		super("Valor", "0.2");
		String name = ValorSynthControls.NAME;
		addControls(ValorSynthControls.class, VALOR_CHANNEL_ID, name, "", "0.2");
		add(ValorSynthChannel.class, name, "Valor", "0.2");
	}

	public PolyphonicSynthChannel createSynthChannel(SynthChannelControls c) {
		if ( c instanceof ValorSynthControls ) {
			return new ValorSynthChannel((ValorSynthControls)c);
		}
		return null;
	}
}
