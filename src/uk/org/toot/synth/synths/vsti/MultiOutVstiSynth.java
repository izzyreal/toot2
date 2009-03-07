package uk.org.toot.synth.synths.vsti;

import java.util.Arrays;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.system.AudioOutput;
import uk.org.toot.misc.plugin.Plugin;
import uk.org.toot.misc.plugin.PluginSupport;
import uk.org.toot.misc.plugin.PluginTempoListener;
import uk.org.toot.misc.plugin.PluginTransportListener;

import com.synthbot.audioplugin.vst.vst2.VstPinProperties;

/**
 * This class has multiple mono/stereo audio outputs
 * @author st
 *
 */
public class MultiOutVstiSynth extends VstiSynth
{
	private int nOutChan;
	private int nInChan;
	private float[][] inSamples;
	private float[][] outSamples;
	private int nsamples;
	private int sampleRate;
	private String location;
	private boolean mustClear = false;
	private VstiSynthControls controls;
	private PluginSupport support;
	private PluginTransportListener transportListener;
	private PluginTempoListener tempoListener;
	
	public MultiOutVstiSynth(final VstiSynthControls controls) {
		super(controls);
		this.controls = controls;
		nsamples = vsti.getBlockSize();
		sampleRate = (int)vsti.getSampleRate();
		
		nOutChan = vsti.numOutputs();
		VstPinProperties props;
		int nchan = 1;
		String outName = "";
		for ( int i = 0, j = 0; i < nOutChan; i += nchan, j++ ) {
			props = vsti.getOutputProperties(i);
			if ( !props.isValid() ) {
				nchan = 2;
				outName = controls.getName()+" "+String.valueOf(1+j);
			} else {
				nchan = props.isFirstInStereoPair() ? 2 : 1;
				outName = props.getShortLabel(); 
			}
			addAudioOutput(new VstiOutput(i, nchan, outName, i == 0));
		}
		outSamples = new float[nOutChan][nsamples];
		                          
		nInChan = vsti.numInputs();
		inSamples = new float[nInChan][nsamples];

		mustClear = vsti.getVendorName().indexOf("Steinberg") >= 0;
//		System.out.print("Using "+controls.getName());
//		if ( mustClear ) System.out.println(" !!! Must Clear");
//		else System.out.println();
		
		support = Plugin.getPluginSupport();
		
		transportListener = new PluginTransportListener() {
			public void play() {
			}

			public void stop() {
			}			
		};
		
		tempoListener = new PluginTempoListener() {
			public void tempoChanged(float newTempo) {
				vsti.setTempo(newTempo);				
			}			
		};
	}

	public void setLocation(String location) {
		this.location = location;
	}

	protected class VstiOutput implements AudioOutput
	{
		private int startChan;
		private int nChans;
		private String name;
		private AudioBuffer.MetaInfo info;
		private ChannelFormat format;
		private boolean master;
		
		public VstiOutput(int startChan, int nChans, String name, boolean master) {
			this.startChan = startChan;
			this.nChans = nChans;
			this.name = name;
			this.master = master;
			format = nChans > 1 ? ChannelFormat.STEREO : ChannelFormat.MONO;
		}

		public void open() throws Exception {
	        info = new AudioBuffer.MetaInfo(getName(), location);
			if ( !master ) return;
			System.out.print("Opening audio: "+controls.getName()+" ... ");
			vsti.turnOn();
			System.out.println("opened");
			support.addTempoListener(tempoListener);
			support.addTransportListener(transportListener);
		}

		public int processAudio(AudioBuffer buffer) {
			if ( master ) {
				int ns = buffer.getSampleCount();
				if ( ns != nsamples ) {
					vsti.turnOff(); // !!!
					nsamples = ns;
					outSamples = new float[nOutChan][nsamples];
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
						Arrays.fill(outSamples[i], 0f);
					}
				}
				vsti.processReplacing(inSamples, outSamples, nsamples);
			}
		    buffer.setMetaInfo(info);
		    buffer.setChannelFormat(format);
			for ( int i = 0, j = startChan; i < nChans; i++, j++ ) {
				float[] from = outSamples[j];
				float[] to = buffer.getChannel(i);
				System.arraycopy(from, 0, to, 0, nsamples);
			}
			return AudioProcess.AUDIO_OK;
		}
		
		public void close() throws Exception {
			if ( !master ) return;
			System.out.print("Closing audio: "+controls.getName()+" ... ");
			vsti.turnOffAndUnloadPlugin();
			System.out.println("closed");
			support.removeTempoListener(tempoListener);
			support.removeTransportListener(transportListener);
		}

		public String getLocation() {
			return location;
		}

		public String getName() {
			return name;
		}

	}
}
