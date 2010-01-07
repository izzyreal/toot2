// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.channels.total;

import static uk.org.toot.misc.Localisation.getString;
import static uk.org.toot.synth.id.TootSynthControlsId.TOTAL_CHANNEL_ID;
import uk.org.toot.control.CompoundControl;
import uk.org.toot.synth.modules.amplifier.AmplifierControls;
import uk.org.toot.synth.modules.amplifier.AmplifierVariables;
import uk.org.toot.synth.modules.envelope.EnvelopeControls;
import uk.org.toot.synth.modules.envelope.EnvelopeVariables;
import uk.org.toot.synth.modules.oscillator.DSFOscillatorControls;
import uk.org.toot.synth.modules.oscillator.DSFOscillatorVariables;

/**
 * @author st
 */
public class TotalSynthControls extends CompoundControl
{
	public static String NAME = "Total";
	
	// OFFSETS MUST NOT BE CHANGED TO PRESERVE PERSISTENCE PORTABILITY
	// OFFSETS ARE SLIGHTLY SPARSE TO ALLOW EXTENSION OF EXISTING MODULES
	private final static int OSC1_OFFSET 	= 0x00; // 8, 5 used, 3 free
	private final static int AMP_OFFSET 	= 0x38; // 2, 2 used
	private final static int AMPENV_OFFSET 	= 0x40; // 8, 6 used, 2 free

	private DSFOscillatorControls oscillatorControls;
	private EnvelopeControls[] envelopeControls;
	private AmplifierControls amplifierControls;

	public TotalSynthControls() {
		super(TOTAL_CHANNEL_ID, NAME);
		envelopeControls = new EnvelopeControls[2];

		ControlRow oscRow = new ControlRow();
		oscillatorControls = new DSFOscillatorControls(0, getString("Oscillator"), OSC1_OFFSET);
		oscRow.add(oscillatorControls);
		add(oscRow);
		
		ControlRow envRow = new ControlRow();
		envelopeControls[0] = 
			new EnvelopeControls(0, getString("Amplifier")+" "+getString("Envelope"), AMPENV_OFFSET); 
		envRow.add(envelopeControls[0]);
		amplifierControls = new AmplifierControls(0, getString("Amplifier"), AMP_OFFSET);
		envRow.add(amplifierControls);
		add(envRow);

	}

	public DSFOscillatorVariables getOscillatorVariables() {
		return oscillatorControls;
	}
	
	public EnvelopeVariables getEnvelopeVariables(int instance) {
		return envelopeControls[instance];
	}
	public AmplifierVariables getAmplifierVariables() {
		return amplifierControls;
	}	
}
