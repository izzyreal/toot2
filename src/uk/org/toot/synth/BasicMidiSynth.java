package uk.org.toot.synth;

import javax.sound.midi.MidiMessage;

import uk.org.toot.midi.core.AbstractMidiDevice;
import uk.org.toot.midi.message.*;

/**
 * A BasicMidiSynth is a MidiSynth with 16 SynthChannels which may be set by the user.
 * So it is multitimbral and each SynthChannel may be a different implementation,
 * think a multitimbral synth with channel 1 a TX81Z, channel 2 a Moog etc.
 * @author st
 *
 */
abstract public class BasicMidiSynth extends AbstractMidiDevice implements MidiSynth
{
    /**
     * @link aggregationByValue
     * @supplierCardinality 16 
     */
	private SynthChannel[] synthChannels = new SynthChannel[16];
	protected SynthRack rack; // !!!
	private String location;
	
	public BasicMidiSynth(String name) {
		super(name);
		addMidiInput(this);
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getLocation() {
		return location;
	}
	
	protected void setChannel(int chan, SynthChannel synthChannel) {
		synthChannels[chan] = synthChannel;
	}

	public SynthChannel[] getChannels() {
		return synthChannels; 
	}

	public SynthChannel getChannel(int chan) {
		return synthChannels[chan];
	}
	
	public void setRack(SynthRack rack) {
		this.rack = rack;
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
			} else {
				int cmd = ChannelMsg.getCommand(msg);
				switch ( cmd ) {
				case ChannelMsg.PITCH_BEND:
					synthChannel.setPitchBend(ChannelMsg.getData1and2(msg));
					break;
				case ChannelMsg.CONTROL_CHANGE:
					synthChannel.controlChange(ChannelMsg.getData1(msg), ChannelMsg.getData2(msg));
					break;
				}
			}
		}
	}
	
	public void closeMidi() {}
}
