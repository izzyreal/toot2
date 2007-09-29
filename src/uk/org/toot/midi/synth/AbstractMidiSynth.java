// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.midi.synth;

import javax.sound.midi.MidiMessage;
import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.midi.core.MidiInput;
import uk.org.toot.midi.core.AbstractMidiDevice;

public abstract class AbstractMidiSynth extends AbstractMidiDevice 
	implements MidiSynth, MidiInput
{
	public AbstractMidiSynth(String name) {
		super(name);
		addMidiInput(this);		
	}

	public void open() {
	}

	// implement for midi input
	public abstract void transport(MidiMessage msg, long timestamp);

	// implement for audio output
	public abstract int processAudio(AudioBuffer buffer);
	
	public void close() {
	}
}
