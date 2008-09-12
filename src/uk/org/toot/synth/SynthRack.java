package uk.org.toot.synth;

import java.util.List;
import java.util.Observable;
import java.util.Collections;
import uk.org.toot.midi.core.MidiSystem;

/**
 * A SynthRack is a List of MidiSynths.
 * It adds its MidiSynths to a MidiSystem as MidiInputs.
 * 
 * @author st
 *
 */
public class SynthRack extends Observable
{
	private MidiSystem midiSystem;
	private List<MultiMidiSynth> synths;
	
	public SynthRack(MidiSystem midiSystem) {
		this.midiSystem = midiSystem;
		synths = new java.util.ArrayList<MultiMidiSynth>();
	}
	
	public void addMidiSynth(MultiMidiSynth synth) {
		synths.add(synth);
		midiSystem.addMidiDevice(synth);
		setChanged();
		notifyObservers();
	}
	
	public void removeMidiSynth(MultiMidiSynth synth) {
		midiSystem.removeMidiDevice(synth);
		synths.remove(synth);
		setChanged();
		notifyObservers();
	}
	
	public List<MultiMidiSynth> getMidiSynths() {
		return Collections.unmodifiableList(synths);
	}
}
