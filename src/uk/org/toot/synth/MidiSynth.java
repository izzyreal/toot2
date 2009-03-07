package uk.org.toot.synth;

import uk.org.toot.audio.system.AudioDevice;
import uk.org.toot.midi.core.MidiDevice;
import uk.org.toot.midi.core.MidiInput;

/**
 * A MidiSynth is a MidiDevice which is a MidiInput.
 * An implementation may implement AudioProcess at any level, so this is not defined here.
 * @author st
 *
 */
public interface MidiSynth extends MidiDevice, MidiInput, AudioDevice
{
	public void setLocation(String location); /// !!! called once by SynthRack
}
