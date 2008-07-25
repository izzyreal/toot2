package uk.org.toot.synth;

import java.util.List;

import javax.sound.midi.MidiChannel;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;

import static uk.org.toot.midi.misc.Controller.*;

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
	
	private int pressure = 0;
	private byte[] polyPressure = new byte[128];
	
	private byte[] controller = new byte[128];
	
	private AudioBuffer.MetaInfo info;
	
	private List<Voice> finished = new java.util.ArrayList<Voice>();

	public SynthChannel(String name) {
        info = new AudioBuffer.MetaInfo(name);
	}
	
	public static float midiFreq(int pitch) { 
		return (float)(440.0 * Math.pow( 2.0, ((double)pitch - 69.0) / 12.0 )); 
	}
	 
	// implement AudioProcess ---------------------------------- 
	
	public void open() {
		
	}
	
	public int processAudio(AudioBuffer buffer) {
        buffer.setMetaInfo(info);
        buffer.setChannelFormat(ChannelFormat.MONO);
		buffer.makeSilence();
		finished.clear();
		synchronized ( voices ) {
			if ( buffer.getSampleRate() != sampleRate ) {
				setSampleRate((int)buffer.getSampleRate());
				for ( Voice voice : voices ) {
					voice.setSampleRate(sampleRate);
				}
			}
			for ( Voice voice : voices ) {
				if ( !voice.mix(buffer) ) {
					finished.add(voice);
				}
			}
			for ( Voice voice : finished ) {
				voices.remove(voice);
			}
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
			voices.add(createVoice(pitch, velocity, sampleRate));
		}
	}

	public void noteOff(int pitch) {
		synchronized ( voices ) {
			for ( Voice voice : voices ) {
				if ( voice.getPitch() == pitch && !voice.isReleased() ) {
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

	public void resetAllControllers() {
		for ( int i = 0; i < controller.length; i++) {
			controller[i] = 0;
		}
		// expression 127, 127
		controller[EXPRESSION] = 127;
		controller[EXPRESSION+0x20] = 127;
		// volume 100
		controller[VOLUME] = 100;
		// pan 64, 64
		controller[PAN] = 64;
		controller[PAN+0x20] = 64;
		
		// channel pressure 0
		pressure = 0;
		// pitch wheel centre
		setPitchBend(8192);
		
	}

	public int getProgram() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void programChange(int arg0) {
		// TODO Auto-generated method stub		
	}

	public void programChange(int arg0, int arg1) {
		// TODO Auto-generated method stub		
	}

	public int getChannelPressure() {
		return pressure;
	}

	public void setChannelPressure(int arg0) {
		pressure = arg0;
	}

	public int getPolyPressure(int arg0) {
		return polyPressure[arg0];
	}

	public void setPolyPressure(int arg0, int arg1) {
		polyPressure[arg0] = (byte)arg1;
	}

	public boolean getSolo() {
		return false;
	}

	public boolean getMute() {
		return false;
	}

	public boolean getMono() {
		return polyphony == 1; // ???
	}

	public boolean getOmni() {
		return false;
	}

	public void setSolo(boolean arg0) {
	}

	public void setMute(boolean arg0) {
	}

	public void setMono(boolean mono) {
		if ( mono ) {
			lastPolyphony = polyphony;
			polyphony = 1;
		} else {
			polyphony = lastPolyphony;
		}
		
	}

	public void setOmni(boolean arg0) {
	}

	public boolean localControl(boolean arg0) {
		// TODO Auto-generated method stub
		return false;
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

	public interface Voice
	{
		int getPitch();
		void release(); // begin amplitude release phase
		boolean isReleased();
		void stop();    // sound off immediately
		void setSampleRate(int sr);
		boolean mix(AudioBuffer buffer); // return false when finished
	}
	
	public abstract class AbstractVoice implements Voice
	{
		protected int pitch;
		protected int velocity;
		protected float amplitude;
		protected float frequency;
		protected boolean release = false;
		protected boolean stop = false;

		public AbstractVoice(int pitch, int velocity) {
			this.pitch = pitch;
			this.velocity = velocity;
			amplitude = (float)velocity / 128;
			frequency = midiFreq(pitch);
		}

		public int getPitch() {
			return pitch;
		}

		public void release() {
			release = true;			
		}

		public boolean isReleased() {
			return release;
		}
		
		public void stop() {
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