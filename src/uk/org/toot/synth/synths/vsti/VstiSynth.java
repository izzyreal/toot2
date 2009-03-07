package uk.org.toot.synth.synths.vsti;

import java.util.Collections;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

import uk.org.toot.synth.MidiSynth;
import uk.org.toot.audio.system.AudioInput;
import uk.org.toot.audio.system.AudioOutput;
import uk.org.toot.midi.core.AbstractMidiDevice;

public abstract class VstiSynth extends AbstractMidiDevice implements MidiSynth
{
	private List<AudioOutput> audioOutputs;
	
	protected JVstHost2 vsti;
	
	public VstiSynth(VstiSynthControls controls) {
		super(controls.getName());
		audioOutputs = new java.util.ArrayList<AudioOutput>();
		addMidiInput(this);
		vsti = controls.getVst();
	}
	
	public void closeMidi() {
	}

	public void transport(MidiMessage msg, long timestamp) {
		if ( msg instanceof ShortMessage ) {
			vsti.queueMidiMessage((ShortMessage)msg); 
		} else if ( msg instanceof SysexMessage ) {
			vsti.queueMidiMessage((SysexMessage)msg);
		}
	}
	
    protected void addAudioOutput(AudioOutput output) {
        audioOutputs.add(output);
        setChanged();
        notifyObservers(output);
    }

	public List<AudioOutput> getAudioOutputs() {
        return Collections.unmodifiableList(audioOutputs);
	}
	
	public List<AudioInput> getAudioInputs() {
		return Collections.emptyList();
	}
	
	public void closeAudio() {	
	}
	

}
