package uk.org.toot.synth;

import java.util.List;
import uk.org.toot.control.CompoundControl;

import static uk.org.toot.control.id.ProviderId.TOOT_PROVIDER_ID;;

/**
 * This class is a list of SynthControls
 * @author st
 *
 */
public class SynthRackControls extends CompoundControl
{
	private SynthRack synthRack;
	private SynthControls[][] synthControls;
	private int midiSynthCount;
	
	public SynthRackControls(SynthRack rack) {
		super(2, "Synth Rack"); // !!! 2, MixerControls use 1, but are they ever used? TODO
		synthRack = rack;
		// ASSUME SynthRack has had ALL MidiSynths addedd already !!!
		midiSynthCount = synthRack.getMidiSynths().size();
		synthControls = new SynthControls[midiSynthCount][16];
	}
	
	public int getMidiSynthCount() {
		return midiSynthCount;
	}

	public SynthControls getSynthControls(int midisynth, int chan) {
		return synthControls[midisynth][chan];
	}
	
	public void add(int synth, int chan, SynthControls controls) {
		List<MidiSynth> synths = synthRack.getMidiSynths();
		if ( synth >= synths.size() ) {
			throw new IllegalArgumentException("Illegal MidiSynth "+synth);
		}
		if ( chan < 0 || chan > 15 ) {
			throw new IllegalArgumentException("Illegal MIDI channel "+chan);
		}
		// SPI lookup plugin SynthChannel for these controls
		SynthChannel synthChannel = SynthServices.createSynthChannel(controls);
		if ( synthChannel == null ) {
			throw new IllegalArgumentException("No SynthChannel for SynthControls");
		}
		// add SynthChannel to MidiSynth in synthRack
		synths.get(synth).setChannel(chan, synthChannel);
		synthControls[synth][chan] = controls;
		super.add(controls);
	}
	
	public int getProviderId() {
		return TOOT_PROVIDER_ID;
	}
	// causes plugins to show Preset menu
	public boolean isPluginParent() { 
		return true; 
	}	
}
