package uk.org.toot.synth;

import java.util.HashMap;

import uk.org.toot.audio.core.AudioControlsChain;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.mixer.AudioMixer;
import uk.org.toot.audio.mixer.AudioMixerStrip;
import uk.org.toot.audio.mixer.MixerControls;
import uk.org.toot.midi.core.MidiSystem;

import static uk.org.toot.audio.mixer.MixerControlsIds.CHANNEL_STRIP;

public class MixerConnectedSynthRack extends SynthRack 
{
	private AudioMixer mixer;
	private MixerControls mixerControls;
	private HashMap<AudioProcess, AudioMixerStrip> connections;

	public MixerConnectedSynthRack(SynthRackControls rackControls, MidiSystem midiSystem, AudioMixer mixer) {
		super(rackControls, midiSystem);
		this.mixer = mixer;
		mixerControls = mixer.getMixerControls();
		connections = new HashMap<AudioProcess, AudioMixerStrip>();
	}

	// public as implementation side-effect
	public void connect(Object obj) {
		if ( !(obj instanceof AudioProcess) ) return;
		AudioProcess process = (AudioProcess)obj;
		AudioControlsChain stripControls;
		AudioControlsChain namedControls;
		try {
			AudioMixerStrip strip = mixer.getUnusedChannelStrip();
			if ( strip == null ) {
				int i = -1;
				int max = 1 + mixerControls.getControls().size();
				String name;
				do {
					stripControls = mixerControls.getStripControls(CHANNEL_STRIP, ++i);
					name = String.valueOf(i+1);
					namedControls = mixerControls.getStripControls(name);
				} while ( stripControls != null && namedControls != null && i < max );
				// no strip with same number or name
				mixerControls.createStripControls(CHANNEL_STRIP, i, name);
				strip = mixer.getStrip(name);
				if ( strip == null ) {
					System.err.println("Failed to create mixer strip for synth");
					return;
				}
			}
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
