package uk.org.toot.audio.vstfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.spi.AudioControlServiceDescriptor;
import uk.org.toot.audio.spi.AudioServiceProvider;
import uk.org.toot.misc.Vst;
import uk.org.toot.service.ServiceDescriptor;
//import uk.org.toot.synth.MidiSynth;
//import uk.org.toot.synth.SynthControls;
//import uk.org.toot.synth.spi.SynthServiceProvider;

import static uk.org.toot.control.id.ProviderId.VST_PROVIDER_ID;

public class VstEffectServiceProvider extends AudioServiceProvider
{
	private static final String VSTFX_CACHE = "vstfx.cache";
	
	public VstEffectServiceProvider() {
		super(VST_PROVIDER_ID, "Various", "VSTfx", "0.1");
		File tootdir = new File(System.getProperty("user.home"), "toot");
		File dir = new File(tootdir, "audio");
		dir.mkdirs();
		File vstfxCache = new File(dir, VSTFX_CACHE);
		if ( !vstfxCache.exists() ) Vst.scan(vstfxCache, false);
		readCache(vstfxCache);
	}

	protected void readCache(File cache) {
		System.out.println("Reading "+VSTFX_CACHE);
		try {
			BufferedReader br = new BufferedReader(new FileReader(cache));
			String line;
			while ((line = br.readLine()) != null) {
				if ( line.charAt(0) == '-') continue; // disabled
				String[] parts = line.split(", ");
				if ( parts.length > 1 ) {
					addVstControls(Integer.parseInt(parts[0]), parts[3], parts[2]);
				}
			}
			br.close();
		} catch ( Exception e ) {
			System.err.println("Failed to read "+cache.getPath());
			e.printStackTrace();
		}
	}
	
	protected void addVstControls(int id, String name, String filename) {
		addControls(VstEffectControls.class, id, name, "VST", "0.1", ChannelFormat.STEREO, filename);		
	}
	
	@Override
    protected AudioControls createControls(ServiceDescriptor d) {
		Class<VstEffectControls> clazz = VstEffectControls.class;
		try {
			Constructor<VstEffectControls> ctor = 
				clazz.getConstructor(AudioControlServiceDescriptor.class);
			return ctor.newInstance((AudioControlServiceDescriptor)d);
		} catch ( Exception e ) {
//			e.printStackTrace();
		}
		return null;
    }
    
	public AudioProcess createProcessor(AudioControls c) {
		if ( !(c instanceof VstEffectControls) ) return null;
		return new VstEffect((VstEffectControls)c);
	}
}
