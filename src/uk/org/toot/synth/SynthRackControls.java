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
	
	public void setSynthControls(int synth, int chan, SynthControls controls) {
		List<MultiMidiSynth> synths = synthRack.getMidiSynths();
		if ( synth >= synths.size() ) {
			throw new IllegalArgumentException("Illegal MidiSynth "+synth);
		}
		if ( chan < 0 || chan > 15 ) {
			throw new IllegalArgumentException("Illegal MIDI channel "+chan);
		}
		
		SynthChannel old = synths.get(synth).getChannels()[chan];
		if ( old != null ) disconnect(synth, chan, old);
		
		if ( controls != null ) {
			// SPI lookup plugin SynthChannel for these controls
			SynthChannel synthChannel = SynthServices.createSynthChannel(controls);
			if ( synthChannel == null ) {
				throw new IllegalArgumentException("No SynthChannel for SynthControls");
			}
			// add SynthChannel to MidiSynth in synthRack
			synths.get(synth).setChannel(chan, synthChannel);
			synthControls[synth][chan] = controls;
			super.add(controls);
			connect(synth, chan, synthChannel);
		} else {
			synths.get(synth).setChannel(chan, null);
			super.remove(synthControls[synth][chan]);
			synthControls[synth][chan] = null;
		}
		synthControlsSet(synth, chan, synthControls[synth][chan]);
	}

	protected void connect(int synth, int chan, SynthChannel channel) {	
	}
	
	protected void disconnect(int synth, int chan, SynthChannel channel) {
	}
	
	public int getProviderId() {
		return TOOT_PROVIDER_ID;
	}
	// causes plugins to show Preset menu
	public boolean isPluginParent() { 
		return true; 
	}
	
	private List<SynthControlsListener> listeners = new java.util.ArrayList<SynthControlsListener>();
	
	protected void synthControlsSet(int synth, int chan, SynthControls controls) {
		for ( SynthControlsListener l : listeners ) {
			l.synthControlsSet(synth, chan, controls);
		}
	}
	
	public void addSynthControlsListener(SynthControlsListener l) {
		listeners.add(l);
	}
	
	public void removeSynthControlsListener(SynthControlsListener l) {
		listeners.remove(l);
	}
	
	public interface SynthControlsListener
	{
		void synthControlsSet(int synth, int chan, SynthControls controls);
	}
}
