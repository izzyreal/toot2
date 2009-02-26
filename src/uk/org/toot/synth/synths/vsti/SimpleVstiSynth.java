package uk.org.toot.synth.synths.vsti;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;

/**
 * This class has a single audio output, for mono and stereo vstis.
 * @author st
 *
 */
public class SimpleVstiSynth extends VstiSynth implements AudioProcess
{
	private int nOutChan;
	private int nInChan;
	private float[][] inSamples;
	private float[][] outSamples;
	private int nsamples;
	private int sampleRate;
	private AudioBuffer.MetaInfo info;
	private ChannelFormat format;
	private boolean mustClear = false;
	private VstiSynthControls controls;
	
	public SimpleVstiSynth(VstiSynthControls controls) {
		super(controls);
		this.controls = controls;
		nsamples = vsti.getBlockSize();
		sampleRate = (int)vsti.getSampleRate();
		
		nOutChan = vsti.numOutputs();
		outSamples = new float[nOutChan][nsamples];
		if ( nOutChan >= 2 ) {
			nOutChan = 2;
			format = ChannelFormat.STEREO;
		} else if ( nOutChan == 1 ) {
			format = ChannelFormat.MONO;
		}
		                          
		nInChan = vsti.numInputs();
		inSamples = new float[nInChan][nsamples];

		mustClear = vsti.getVendorName().indexOf("Steinberg") >= 0;
		System.out.print("Using "+controls.getName());
		if ( mustClear ) System.err.println(" !!! Must Clear");
		else System.out.println();
	}

	public void setLocation(String location) {
        info = new AudioBuffer.MetaInfo(getName(), location);
	}

	public void open() throws Exception {
		System.out.print("Opening audio: "+controls.getName()+" ... ");
		vsti.turnOn();
		System.out.println("opened");
	}

	public int processAudio(AudioBuffer buffer) {
	    buffer.setMetaInfo(info);
	    buffer.setChannelFormat(format);
		int ns = buffer.getSampleCount();
		if ( ns != nsamples ) {
			vsti.turnOff(); // !!!
			nsamples = ns;
			outSamples = new float[vsti.numOutputs()][nsamples];
			inSamples = new float[nInChan][nsamples];
			vsti.setBlockSize(nsamples);
			vsti.turnOn(); // !!!
		}
		int sr = (int)buffer.getSampleRate();
		if ( sr != sampleRate ) {
			sampleRate = sr;
			vsti.setSampleRate(sampleRate);
		}
		if ( mustClear ) {
			for ( int i = 0; i < nOutChan; i++ ) {
				for ( int j = 0; j < nsamples; j++ ) {
					outSamples[i][j] = 0f;
				}					
			}
		}
		vsti.processReplacing(inSamples, outSamples, nsamples);
		for ( int i = 0; i < nOutChan; i++ ) {
			float[] from = outSamples[i];
			float[] to = buffer.getChannel(i);
			System.arraycopy(from, 0, to, 0, nsamples);
		}
		return AudioProcess.AUDIO_OK;
	}

	public void close() throws Exception {
		System.out.print("Closing audio: "+controls.getName()+" ... ");
		vsti.turnOffAndUnloadPlugin();
		System.out.println("closed");
	}
}