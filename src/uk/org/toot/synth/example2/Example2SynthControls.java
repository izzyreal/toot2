package uk.org.toot.synth.example2;

import static uk.org.toot.localisation.Localisation.getString;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.amplifier.AmplifierControls;
import uk.org.toot.synth.amplifier.AmplifierVariables;
import uk.org.toot.synth.envelope.*;
import uk.org.toot.synth.filter.*;
import uk.org.toot.synth.mixer.MixerControls;
import uk.org.toot.synth.mixer.MixerVariables;
import uk.org.toot.synth.oscillator.*;
import static uk.org.toot.synth.id.TootSynthControlsId.EXAMPLE_2_SYNTH_ID;

/**
 * idOffset Map
 * 0x00 Oscillator 1
 * 0x08 Oscillator 2
 * 0x10 Oscillator 3
 * 0x18 Vibrato LFO
 * 0x20 Low Pass Filter
 * 0x28 	
 * 			0x2d Low Pass Filter Mixer
 * 0x30 State Variable Filter
 * 0x38	State Variable Filter Mixer
 * 0x3d Amplifier Mixer
 * 0x40 Amplifier Envelope
 * 0x48 Low Pass Filter Envelope
 * 0x50 Oscillator 2 Sync Envelope
 * 0x58 Oscillator 3 Sync Envelope
 * 0x60 State Variable Filter envelope ?
 * 0x68 Oscillator 1 Width LFO
 * 0x70 Oscillator 2 Width LFO
 * 0x78 Oscillator 3 Width LFO
 * @author st
 *
 */
public class Example2SynthControls extends SynthControls
{
	private MultiWaveOscillatorControls[] oscillatorControls;
	private FilterControls[] filterControls;
	private EnvelopeControls[] envelopeControls;
	private AmplifierControls amplifierControls;
	private DelayedLFOControls[] lfoControls;
	private MixerControls[] mixerControls;
	
	public Example2SynthControls(String name) {
		super(EXAMPLE_2_SYNTH_ID, name);
		
		oscillatorControls = new MultiWaveOscillatorControls[4];
		filterControls = new FilterControls[2];
		envelopeControls = new EnvelopeControls[5];
		lfoControls = new DelayedLFOControls[4];
		mixerControls = new MixerControls[3];
		
		LFOConfig widthLFOConfig = new LFOConfig();
		LFOConfig vibratoConfig = new LFOConfig();
		vibratoConfig.rateMin = 4f;
		vibratoConfig.rateMax = 7f;
		vibratoConfig.rate = 5.5f;
		vibratoConfig.deviationMax = 2f;
		vibratoConfig.deviation = 1.5f;		
		vibratoConfig.hasLevel = true;

		ControlRow osc0row = new ControlRow();
		lfoControls[0] = new DelayedLFOControls(0, "Vibrato", 0x18, vibratoConfig);
		osc0row.add(lfoControls[0]);
		oscillatorControls[0] = new MultiWaveOscillatorControls(0, "Oscillator 1", 0x00, true);
		osc0row.add(oscillatorControls[0]);
		lfoControls[1] = new DelayedLFOControls(2, "Width LFO", 0x68, widthLFOConfig);
		osc0row.add(lfoControls[1]);
		add(osc0row);
		
		ControlRow osc1row = new ControlRow();
		envelopeControls[2] = 
			new EnvelopeControls(2, getString("Sync")+" "+getString("Envelope"), 0x50, 5) {
				protected boolean hasDelay() { return false; }
			}
		; 
		osc1row.add(envelopeControls[2]);
		oscillatorControls[1] = new MultiWaveOscillatorControls(1, "Oscillator 2", 0x08, false);
		osc1row.add(oscillatorControls[1]);
		lfoControls[2] = new DelayedLFOControls(2, "Width LFO", 0x70, widthLFOConfig);
		osc1row.add(lfoControls[2]);
		add(osc1row);

		ControlRow osc2row = new ControlRow();
		envelopeControls[3] = 
			new EnvelopeControls(3, getString("Sync")+" "+getString("Envelope"), 0x58, 5) {
				protected boolean hasDelay() { return false; }
			}
		; 
		osc2row.add(envelopeControls[3]);
		oscillatorControls[2] = new MultiWaveOscillatorControls(2, "Oscillator 3", 0x10, false);
		osc2row.add(oscillatorControls[2]);
		lfoControls[3] = new DelayedLFOControls(3, "Width LFO", 0x78, widthLFOConfig);
		osc2row.add(lfoControls[3]);
		add(osc2row);
		
		ControlRow lpf1row = new ControlRow();		
		envelopeControls[4] = 
			new EnvelopeControls(4, "State Variable "+getString("Filter")+" "+getString("Envelope"), 0x60) {
				protected boolean hasDelay() { return false; }
			};
		lpf1row.add(envelopeControls[4]);
		filterControls[1] = new StateVariableFilterControls(0, "State Variable Filter", 0x30);
		lpf1row.add(filterControls[1]);
		mixerControls[1] = new MixerControls(1, "SVF Oscillator Mix", 0x38, 3);
		lpf1row.add(mixerControls[1]);
		add(lpf1row);

		ControlRow lpf0row = new ControlRow();		
		envelopeControls[1] = 
			new EnvelopeControls(1, "Low Pass "+getString("Filter")+" "+getString("Envelope"), 0x48) {
				protected boolean hasDelay() { return false; }
			};
		lpf0row.add(envelopeControls[1]);
		filterControls[0] = new MoogFilterControls(0, "Low Pass Filter", 0x20);
		lpf0row.add(filterControls[0]);
		mixerControls[0] = new MixerControls(0, "LPF Oscillator Mix", 0x2d, 3);
		lpf0row.add(mixerControls[0]);
		add(lpf0row);

		ControlRow amprow = new ControlRow();
		envelopeControls[0] = 
			new EnvelopeControls(0, getString("Amplitude")+" "+getString("Envelope"), 0x40) {
				protected boolean hasDelay() { return false; }
			}
		; 
		amprow.add(envelopeControls[0]);
		amplifierControls = new AmplifierControls(0, getString("Amplifier"), 0x60);
		amprow.add(amplifierControls);
		add(amprow);
		
	}

	public MultiWaveOscillatorVariables getOscillatorVariables(int instance) {
		return oscillatorControls[instance];
	}
	
	public FilterVariables getFilterVariables(int instance) {
		return filterControls[instance];
	}
	
	public EnvelopeVariables getEnvelopeVariables(int instance) {
		return envelopeControls[instance];
	}
	
	public AmplifierVariables getAmplifierVariables() {
		return amplifierControls;
	}
	
	public DelayedLFOControls getLFOVariables(int instance) {
		return lfoControls[instance];
	}
	
	public MixerVariables getMixerVariables(int instance) {
		return mixerControls[instance];
	}
}
