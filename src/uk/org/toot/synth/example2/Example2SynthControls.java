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
 * @author st
 *
 */
public class Example2SynthControls extends SynthControls
{
	// OFFSETS MUST NOT BE CHANGED TO PRESERVE PERSISTENCE PORTABILITY
	// OFFSETS ARE SLIGHTLY SPARSE TO ALLOW EXTENSION OF EXISTING MODULES
	private final static int OSC1_OFFSET 	= 0x00;
	private final static int OSC2_OFFSET 	= 0x08;
	private final static int OSC3_OFFSET 	= 0x10;
	private final static int LFOVIB_OFFSET 	= 0x18;
	private final static int LPF_OFFSET 	= 0x20;
	private final static int LPFMIX_OFFSET 	= 0x28;
	private final static int SVFMIX_OFFSET 	= 0x2D;
	private final static int SVF_OFFSET 	= 0x30;
	private final static int AMP_OFFSET 	= 0x38;
	private final static int AMPENV_OFFSET 	= 0x40;
	private final static int LPFENV_OFFSET 	= 0x48;
	private final static int SVFENV_OFFSET 	= 0x50;
	private final static int OSC2ENV_OFFSET	= 0x58;
	private final static int OSC3ENV_OFFSET	= 0x60;
	private final static int OSC1LFO_OFFSET	= 0x68;
	private final static int OSC2LFO_OFFSET	= 0x70;
	private final static int OSC3LFO_OFFSET	= 0x78;

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
		lfoControls[0] = new DelayedLFOControls(0, "Vibrato", LFOVIB_OFFSET, vibratoConfig);
		osc0row.add(lfoControls[0]);
		oscillatorControls[0] = new MultiWaveOscillatorControls(0, "Oscillator 1", OSC1_OFFSET, true);
		osc0row.add(oscillatorControls[0]);
		lfoControls[1] = new DelayedLFOControls(2, "Width LFO", OSC1LFO_OFFSET, widthLFOConfig);
		osc0row.add(lfoControls[1]);
		add(osc0row);
		
		ControlRow osc1row = new ControlRow();
		envelopeControls[2] = 
			new EnvelopeControls(2, getString("Sync")+" "+getString("Envelope"), OSC2ENV_OFFSET, 5) {
				protected boolean hasDelay() { return false; }
			}
		; 
		osc1row.add(envelopeControls[2]);
		oscillatorControls[1] = new MultiWaveOscillatorControls(1, "Oscillator 2", OSC2_OFFSET, false);
		osc1row.add(oscillatorControls[1]);
		lfoControls[2] = new DelayedLFOControls(2, "Width LFO", OSC2LFO_OFFSET, widthLFOConfig);
		osc1row.add(lfoControls[2]);
		add(osc1row);

		ControlRow osc2row = new ControlRow();
		envelopeControls[3] = 
			new EnvelopeControls(3, getString("Sync")+" "+getString("Envelope"), OSC3ENV_OFFSET, 5) {
				protected boolean hasDelay() { return false; }
			}
		; 
		osc2row.add(envelopeControls[3]);
		oscillatorControls[2] = new MultiWaveOscillatorControls(2, "Oscillator 3", OSC3_OFFSET, false);
		osc2row.add(oscillatorControls[2]);
		lfoControls[3] = new DelayedLFOControls(3, "Width LFO", OSC3LFO_OFFSET, widthLFOConfig);
		osc2row.add(lfoControls[3]);
		add(osc2row);
		
		ControlRow lpf1row = new ControlRow();		
		envelopeControls[4] = 
			new EnvelopeControls(4, "State Variable "+getString("Filter")+" "+getString("Envelope"), SVFENV_OFFSET) {
				protected boolean hasDelay() { return false; }
			};
		lpf1row.add(envelopeControls[4]);
		filterControls[1] = new StateVariableFilterControls(0, "State Variable Filter", SVF_OFFSET);
		lpf1row.add(filterControls[1]);
		mixerControls[1] = new MixerControls(1, "SVF Oscillator Mix", SVFMIX_OFFSET, 3);
		lpf1row.add(mixerControls[1]);
		add(lpf1row);

		ControlRow lpf0row = new ControlRow();		
		envelopeControls[1] = 
			new EnvelopeControls(1, "Low Pass "+getString("Filter")+" "+getString("Envelope"), LPFENV_OFFSET) {
				protected boolean hasDelay() { return false; }
			};
		lpf0row.add(envelopeControls[1]);
		filterControls[0] = new MoogFilterControls(0, "Low Pass Filter", LPF_OFFSET);
		lpf0row.add(filterControls[0]);
		mixerControls[0] = new MixerControls(0, "LPF Oscillator Mix", LPFMIX_OFFSET, 3);
		lpf0row.add(mixerControls[0]);
		add(lpf0row);

		ControlRow amprow = new ControlRow();
		envelopeControls[0] = 
			new EnvelopeControls(0, getString("Amplifier")+" "+getString("Envelope"), AMPENV_OFFSET) {
				protected boolean hasDelay() { return false; }
			}
		; 
		amprow.add(envelopeControls[0]);
		amplifierControls = new AmplifierControls(0, getString("Amplifier"), AMP_OFFSET);
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
