package uk.org.toot.synth;

import java.util.List;

import javax.sound.midi.MidiChannel;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;

/**
 * A SynthChannel is a MidiChannel that generates audio as an AudioProcess.
 * It is polyphonic, supporting multiple Voices.
 * 
 * @author st
 *
 */
public abstract class SynthChannel implements MidiChannel, AudioProcess
{
	private List<Voice> voices = new java.util.ArrayList<Voice>();
	
	private int sampleRate;
	private int polyphony = 8;
	private int lastPolyphony = polyphony;
	
	private int rawBend = 8192;
	private int bendRange = 2; 		// max bend in semitones
	private float bendFactor = 1;	// current bend factor
	private final static double ONE_SEMITONE = 1.0594630943592952645618252949463;
	
	private byte[] controller = new byte[128];
	
	private AudioBuffer.MetaInfo info;
	
	public SynthChannel(String name) {
        info = new AudioBuffer.MetaInfo(name);
	}
	
	// implement AudioProcess ---------------------------------- 
	
	public void open() {
		
	}
	
	public int processAudio(AudioBuffer buffer) {
        // attach source name meta info so our mixer strip shows our name
        buffer.setMetaInfo(info);
        buffer.setChannelFormat(ChannelFormat.MONO); // a mono input
		buffer.makeSilence();
		List<Voice> finished = new java.util.ArrayList<Voice>(); // !!!
		synchronized ( voices ) {
			if ( buffer.getSampleRate() != sampleRate ) {
				setSampleRate((int)buffer.getSampleRate());
				// change the sample rate of existing voices
				for ( Voice voice : voices ) {
					voice.setSampleRate(sampleRate);
				}
			}
			for ( Voice voice : voices ) {
				if ( !voice.mix(buffer) ) {
					finished.add(voice);
				}
			}
		}
		for ( Voice voice : finished ) {
//			System.out.print('f');
			voices.remove(voice);
		}
		return AudioProcess.AUDIO_OK;
	}
	
	protected void setSampleRate(int rate) {
		sampleRate = rate;
	}
	
	public void close() {
		
	}
	
	public void setPolyphony(int p) {
		polyphony = p;
	}
	
	public int getPolyphony() {
		return polyphony;
	}
	
	// implement MidiChannel ------------------------------------
	
	protected abstract Voice createVoice(int pitch, int velocity, int sampleRate);
	
	public void noteOn(int pitch, int velocity) {
		synchronized ( voices ) {
			if ( voices.size() >= polyphony ) {
				// oldest note stealing
				Voice steal = voices.get(0);
				steal.stop();
				voices.remove(steal);
			}
//			System.out.print("\n"+voices.size());
			voices.add(createVoice(pitch, velocity, sampleRate));
		}
	}

	public void noteOff(int pitch) {
		synchronized ( voices ) {
			for ( Voice voice : voices ) {
				if ( voice.getPitch() == pitch ) {
					voice.release();
					return;
				}
			}
		}
	}

	public void noteOff(int pitch, int velocity) {
		noteOff(pitch);			
	}

	public void allNotesOff() {
		synchronized ( voices ) {
			for ( Voice voice : voices ) {
				voice.release();
			}
		}
	}

	public void allSoundOff() {
		synchronized ( voices ) {
			for ( Voice voice : voices ) {
				voice.stop();
			}
		}
	}

	public void controlChange(int arg0, int arg1) {
		controller[arg0] = (byte)arg1;
		// reset the LSB if a MSB is set
		if ( arg0 < 0x20 ) controller[arg0+0x20] = 0;
	}

	public int getController(int arg0) {
		return controller[arg0];
	}

	public int getChannelPressure() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getMono() {
		return polyphony == 1; // ???
	}

	public boolean getMute() {
		return false;
	}

	public boolean getOmni() {
		return false;
	}

	public int getPolyPressure(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getProgram() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getSolo() {
		return false;
	}

	public boolean localControl(boolean arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void programChange(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public void programChange(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void resetAllControllers() {
		// TODO Auto-generated method stub
		
	}

	public void setChannelPressure(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setMono(boolean mono) {
		if ( mono ) {
			lastPolyphony = polyphony;
			polyphony = 1;
		} else {
			polyphony = lastPolyphony;
		}
		
	}

	public void setMute(boolean arg0) {
	}

	public void setOmni(boolean arg0) {
	}

	public void setPitchBend(int bend) {
		rawBend = bend;
		bend -= 8192; // -8192..+8192
		float b = (float)bendRange * bend / 8192;
		bendFactor = (float)Math.pow(ONE_SEMITONE, b);
	}

	public int getPitchBend() {
		return rawBend;
	}

	// return the bend factor, the factor that should be applied
	// to the current frequency
	public float getBendFactor() {
		return bendFactor;
	}

	public void setPolyPressure(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void setSolo(boolean arg0) {
	}

	public interface Voice
	{
		int getPitch();
		void release(); // begin amplitude release phase
		void stop();    // sound off immediately
		void setSampleRate(int sr);
		boolean mix(AudioBuffer buffer); // return false when finished
	}
	
	public abstract class AbstractVoice implements Voice
	{
		protected int pitch;
		protected int velocity;
		protected boolean release = false;
		protected boolean stop = false;

		public AbstractVoice(int pitch, int velocity) {
			this.pitch = pitch;
			this.velocity = velocity;
		}

		public int getPitch() {
			return pitch;
		}

		public void release() {
//			System.out.print('r');
			release = true;			
		}

		public void stop() {
//			System.out.print('s');
			stop = true;
		}

		public boolean mix(AudioBuffer buffer) {
			if ( stop ) return false;
			float[] samples = buffer.getChannel(0);
			int nsamples = buffer.getSampleCount();
			for ( int i = 0; i < nsamples; i++ ) {
				samples[i] += getSample();
			}
			return !isComplete();
		}
		
		protected abstract float getSample();
		
		protected abstract boolean isComplete();
		
	}

}