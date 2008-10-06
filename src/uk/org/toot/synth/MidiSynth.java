package uk.org.toot.synth;

import uk.org.toot.midi.core.MidiDevice;
import uk.org.toot.midi.core.MidiInput;

/**
 * A MidiSynth is a MidiDevice which is a MidiInput and which has 16 MidiChannels.
 * An implementation may implement AudioProcess at this level, per SynthChannel, or
 * even by both these methods, so this is not defined here.
 * @author st
 *
 */
public interface MidiSynth extends MidiDevice, MidiInput 
{
	public SynthChannel[] getChannels();
	public SynthChannel getChannel(int chan);
	public void setRack(SynthRack rack); // !!! public OTT
	public void setLocation(String location);
	public String getLocation();
}
