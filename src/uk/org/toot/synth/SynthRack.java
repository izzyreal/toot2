package uk.org.toot.synth;

import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.midi.core.MidiSystem;

/**
 * A SynthRack is an array of MidiSynths.
 * It adds its MidiSynths to a MidiSystem as MidiInputs.
 * 
 * @author st
 *
 */
public class SynthRack
{
	private MidiSystem midiSystem;
	private MidiSynth[] synths;
	
	public SynthRack(final SynthRackControls controls, MidiSystem midiSystem) {
		this.midiSystem = midiSystem;
		synths = new MidiSynth[controls.size()];
		controls.addObserver(
			new Observer() {
				public void update(Observable obs, Object obj) {
					if ( obj instanceof Integer ) {
						int nsynth = ((Integer)obj).intValue();
						if ( nsynth < 0 || nsynth >= synths.length ) return;
						CompoundControl synthControls = controls.getSynthControls(nsynth);
						if ( synthControls != null ) {
							// SPI lookup plugin Synth for these controls
							MidiSynth synth = SynthServices.createSynth(synthControls);
							if ( synth == null ) {
								System.err.println("No Synth for SynthControls "+synthControls.getName());
								return;
							} else {
								synth.setLocation("Synth "+String.valueOf((char)('A'+nsynth)));
							}
							setMidiSynth(nsynth, synth);
						} else {
							setMidiSynth(nsynth, null);
						}
					}
				}					
			}
		);
	}
	
	/*
	 * TODO
	 * invert control
	 * ask synths to connect
	 */
	public void setMidiSynth(int i, MidiSynth synth) {
		MidiSynth old = synths[i];
		if ( old != null ) {
			midiSystem.removeMidiDevice(old);
			if ( old instanceof BasicMidiSynth ) { // !!!
				((BasicMidiSynth)old).setRack(null);
				for ( int chan = 0; chan < 16; chan++ ) {
					disconnect(((BasicMidiSynth)old).getChannel(chan));
				}
			}
			disconnect(old);
		}
		synths[i] = synth;
		if ( synth == null ) return;
		midiSystem.addMidiDevice(synth);
		connect(synth);
		if ( synth instanceof BasicMidiSynth ) { // !!!
			for ( int chan = 0; chan < 16; chan++ ) {
				connect(((BasicMidiSynth)synth).getChannel(chan));
			}
			((BasicMidiSynth)synth).setRack(this);
		}
	}
	
	public MidiSynth getMidiSynth(int i) {
		return synths[i];
	}
	
	public void close() {
//		System.out.println("Closing All Synths");
		for ( int i = 0; i < synths.length; i++ ) {
			setMidiSynth(i, null);
		}
//		System.out.println("All Synths Closed");
	}
	
	// public as implementation side-effect
	public void connect(Object obj) {	
	}
	
	// public as implementation side-effect
	public void disconnect(Object obj) {
	}
	
}
