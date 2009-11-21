package uk.org.toot.synth;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.system.AudioOutput;

/**
 * Paraphonic in that it is not fully polyphonic, a Voice may have an oscillator, but
 * not nessarily its own filter or amplifier. It may have a single filter and/or amplifier
 * shared by all Voices, These shared paraphonic parts can be applied in postProcessAudio().
 * Obviously these paraphonic parts are always after the mixing of the individual Voices
 * so prost processing is appropriate.
 * @author st
 *
 */
public abstract class ParaphonicSynthChannel extends PolyphonicSynthChannel 
	implements AudioOutput
{

	public ParaphonicSynthChannel(String name) {
		super(name);
	}

	@Override
	public int processAudio(AudioBuffer buffer) {
		int ret = super.processAudio(buffer);
		postProcessAudio(buffer);
		return ret;
	}
	
	abstract protected int postProcessAudio(AudioBuffer buffer);
	
}