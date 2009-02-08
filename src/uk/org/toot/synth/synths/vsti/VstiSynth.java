package uk.org.toot.synth.synths.vsti;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

import uk.org.toot.synth.MidiSynth;
import uk.org.toot.midi.core.AbstractMidiDevice;

public abstract class VstiSynth extends AbstractMidiDevice implements MidiSynth
{
	protected JVstHost2 vsti;
	
	public VstiSynth(VstiSynthControls controls) {
		super(controls.getName());
		addMidiInput(this);
		vsti = controls.getVst();
	}
	
	public void closeMidi() {
	}

	public void transport(MidiMessage msg, long timestamp) {
		if ( msg instanceof ShortMessage ) {
			vsti.queueMidiMessage((ShortMessage)msg); 
		}
	}
}
