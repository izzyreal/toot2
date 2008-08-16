package uk.org.toot.synth;

import uk.org.toot.audio.mixer.AudioMixer;

/*
 * It's a shame Controls are creating Processes but it works.
 */
public class MixerConnectedSynthRackControls extends SynthRackControls
{
	private AudioMixer mixer;
	private int firstStrip;

	public MixerConnectedSynthRackControls(SynthRack rack, AudioMixer mixer, int firstStrip) {
		super(rack);
		this.mixer = mixer;
		this.firstStrip = firstStrip;
	}

	protected void connect(int synth, int chan, SynthChannel synthChannel) {
		int c = firstStrip + chan + 16 * synth;		
		try {
			mixer.getStrip(String.valueOf(c)).setInputProcess(synthChannel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void disconnect(int synth, int chan, SynthChannel synthChannel) {
		connect(synth, chan, null);
	}
	
}
