package uk.org.toot.synth;

import javax.sound.midi.MidiMessage;

import uk.org.toot.midi.core.AbstractMidiDevice;
import uk.org.toot.midi.core.MidiInput;
import uk.org.toot.midi.message.*;

public class MidiSynth extends AbstractMidiDevice implements MidiInput
{
	private SynthChannel[] synthChannels = new SynthChannel[16];
	
	public MidiSynth(String name) {
		super(name);
		addMidiInput(this);
	}

	public void setChannel(int chan, SynthChannel synthChannel) {
		synthChannels[chan] = synthChannel;
	}

	public SynthChannel[] getChannels() {
		return synthChannels; 
	}

	public void transport(MidiMessage msg, long timestamp) {
		if ( ChannelMsg.isChannel(msg) ) {
			int chan = ChannelMsg.getChannel(msg);
			SynthChannel synthChannel = synthChannels[chan];
			if ( synthChannel == null ) return;
			if ( NoteMsg.isNote(msg) ) {
				int pitch = NoteMsg.getPitch(msg);
				int velocity = NoteMsg.getVelocity(msg);
				boolean on = NoteMsg.isOn(msg);
				if ( on ) {
					synthChannel.noteOn(pitch, velocity);
				} else {
					synthChannel.noteOff(pitch, velocity);
				}
			}
		}
	}
}
