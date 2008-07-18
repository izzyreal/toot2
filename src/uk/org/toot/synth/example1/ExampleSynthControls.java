package uk.org.toot.synth.example1;

import static uk.org.toot.localisation.Localisation.getString;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.envelope.*;
import uk.org.toot.synth.filter.*;
import uk.org.toot.synth.oscillator.*;
import static uk.org.toot.synth.id.TootSynthControlsId.EXAMPLE_1_SYNTH_ID;

public class ExampleSynthControls extends SynthControls
{
	private WaveOscillatorControls[] oscillatorControls;
	private FilterControls[] filterControls;
	private EnvelopeControls[] envelopeControls;
	
	public ExampleSynthControls(String name) {
		super(EXAMPLE_1_SYNTH_ID, name);
		
		oscillatorControls = new WaveOscillatorControls[4];
		filterControls = new FilterControls[1];
		envelopeControls = new EnvelopeControls[3];
		
		ControlRow row1 = new ControlRow();
		envelopeControls[2] = 
			new EnvelopeControls(2, getString("Sync")+" "+getString("Envelope"), 0x50, 5) {
				protected boolean hasDelay() { return false; }
			}
		; 
		row1.add(envelopeControls[2]);
		oscillatorControls[0] = new WaveOscillatorControls(0, "Oscillator 1", 0x00, true);
		oscillatorControls[1] = new WaveOscillatorControls(1, "Oscillator 2", 0x08, false);
		row1.add(oscillatorControls[0]);
		row1.add(oscillatorControls[1]);
		add(row1);
		
		ControlRow row2 = new ControlRow();		
		envelopeControls[1] = 
			new EnvelopeControls(1, getString("Filter")+" "+getString("Envelope"), 0x48) {
				protected boolean hasDelay() { return false; }
			};
		row2.add(envelopeControls[1]);
		filterControls[0] = new FilterControls(0, "Low Pass Filter", 0x20);
		row2.add(filterControls[0]);
		add(row2);

		ControlRow row3 = new ControlRow();
		envelopeControls[0] = 
			new EnvelopeControls(0, getString("Amplitude")+" "+getString("Envelope"), 0x40) {
				protected boolean hasDelay() { return false; }
			}
		; 
		row3.add(envelopeControls[0]);
		add(row3);
		
	}

	public WaveOscillatorVariables getOscillatorVariables(int instance) {
		return oscillatorControls[instance];
	}
	
	public FilterVariables getFilterVariables(int instance) {
		return filterControls[instance];
	}
	
	public EnvelopeVariables getEnvelopeVariables(int instance) {
		return envelopeControls[instance];
	}
}
