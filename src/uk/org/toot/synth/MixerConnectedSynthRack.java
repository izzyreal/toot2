package uk.org.toot.synth;

import java.util.HashMap;

import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.mixer.AudioMixer;
import uk.org.toot.audio.mixer.AudioMixerStrip;
import uk.org.toot.midi.core.MidiSystem;

public class MixerConnectedSynthRack extends SynthRack 
{
	private AudioMixer mixer;
	private HashMap<AudioProcess, AudioMixerStrip> connections;

	public MixerConnectedSynthRack(SynthRackControls rackControls, MidiSystem midiSystem, AudioMixer mixer) {
		super(rackControls, midiSystem);
		this.mixer = mixer;
		connections = new HashMap<AudioProcess, AudioMixerStrip>();
	}

	// public as implementation side-effect
	public void connect(Object obj) {
		if ( !(obj instanceof AudioProcess) ) return;
		AudioProcess process = (AudioProcess)obj;
		try {
			AudioMixerStrip strip = mixer.getUnusedChannelStrip();
			strip.setInputProcess(process);
			connections.put(process, strip);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// public as implementation side-effect
	public void disconnect(Object obj) {
		if ( !(obj instanceof AudioProcess) ) return;
		AudioProcess process = (AudioProcess)obj;
		try {
			AudioMixerStrip strip = connections.get(process);
			if ( strip != null ) {
				strip.setInputProcess(null);
				connections.remove(process);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
