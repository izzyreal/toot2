package uk.org.toot.synth;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.system.AudioOutput;

/**
 * A SynthChannel which is monophonic.
 * Supports glide, i.e. fingered portamento
 * @author st
 *
 */
public abstract class MonophonicSynthChannel extends SynthChannel implements AudioOutput 
{
	private AudioBuffer.MetaInfo info;
	private String location;
	private String name;
	private int noteCount = 0;
	private float frequency;
	private float targetFrequency;
	private boolean gliding = false;
	private float glideFactor;
	private int sampleCount = 256;
	protected float amplitude;

	public MonophonicSynthChannel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setLocation(String location) {
		this.location = location;
        info = new AudioBuffer.MetaInfo(name, location);
	}

	public String getLocation() {
		return location;
	}
	
	@Override
	public void noteOn(int pitch, int velocity) {
		noteCount += 1;
		targetFrequency = midiFreq(pitch);
		amplitude = velocity / 128f;
		if ( noteCount == 1 || !isGlideEnabled() ) {	// immediate, no glide
			frequency = targetFrequency; 	
			trigger(amplitude);
		} else { 										// fingered portamento
			int nblocks = getGlideMilliseconds() * sampleRate / 1000 / sampleCount;				
			glideFactor = (float)Math.pow(targetFrequency/frequency, 1.0/nblocks);
			gliding = true;
		}
	}

	@Override
	public void noteOff(int pitch) {
		noteCount -= 1;
		if ( noteCount == 0 ) {
			release();
		}
	}

	public void open() {	
	}
	
	public int processAudio(AudioBuffer buffer) {
		if ( isComplete() ) return AudioProcess.AUDIO_DISCONNECT;
	    buffer.setMetaInfo(info);
	    buffer.setChannelFormat(ChannelFormat.MONO);
		sampleCount = buffer.getSampleCount();
		int sr = (int)buffer.getSampleRate(); 
		if ( sr != sampleRate ) {
			setSampleRate(sr); // method call allows overriding
			sampleRate = sr;
		}
		if ( gliding ) glide();
		update(frequency);
		float[] samples = buffer.getChannel(0);
		int nsamples = buffer.getSampleCount();
		for ( int i = 0; i < nsamples; i++ ) {
			samples[i] += getSample();
		}
		return AudioProcess.AUDIO_OK;
	}

	public void close() {
	}
	
	private void glide() {
		frequency *= glideFactor;
		if ( glideFactor < 1f ) {
			if ( frequency <= targetFrequency ) {
				frequency = targetFrequency;
				gliding = false;
			}
		} else {
			if ( frequency >= targetFrequency ) {
				frequency = targetFrequency;
				gliding = false;				
			}
		}
	}
	
	@Override
	public void allNotesOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void allSoundOff() {
		// TODO Auto-generated method stub
		
	}

	abstract protected boolean isComplete();
	
	abstract protected void trigger(float amp); // 0..1
	
	abstract protected void release();
	
	abstract protected void update(float frequency); // Hz
	
	abstract protected float getSample();
	
	abstract protected boolean isGlideEnabled();
	
	abstract protected int getGlideMilliseconds();
}
