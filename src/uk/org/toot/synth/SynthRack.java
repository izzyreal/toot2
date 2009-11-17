package uk.org.toot.synth;

import java.util.Observable;
import java.util.Observer;

import uk.org.toot.audio.system.AudioSystem;
import uk.org.toot.control.CompoundControl;
import uk.org.toot.midi.core.MidiSystem;

/**
 * A SynthRack is an array of MidiSynths.
 * It adds its MidiSynths to a MidiSystem as MidiInputs.
 * It adds one or more AudioOutputs to an AudioSystem.
 * 
 * @author st
 *
 */
public class SynthRack
{
	private MidiSystem midiSystem;
	private AudioSystem audioSystem;
	private MidiSynth[] synths;
	
	public SynthRack(final SynthRackControls controls, MidiSystem midiSystem, AudioSystem audioSystem) {
		this.midiSystem = midiSystem;
		this.audioSystem = audioSystem;
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
								synth.setLocation(synthControls.getName()+" "+String.valueOf((char)('A'+nsynth)));
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
	
	protected void setMidiSynth(int i, MidiSynth synth) {
		MidiSynth old = synths[i];
		if ( old != null ) {
			midiSystem.removeMidiDevice(old);
			audioSystem.removeAudioDevice(old);
		}
		synths[i] = synth;
		if ( synth == null ) return;
		midiSystem.addMidiDevice(synth);
		audioSystem.addAudioDevice(synth);
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
}
