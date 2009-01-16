package uk.org.toot.synth.synths.vsti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.misc.VST;
import uk.org.toot.service.ServiceDescriptor;
import uk.org.toot.synth.MidiSynth;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.spi.SynthServiceProvider;

import static uk.org.toot.control.id.ProviderId.TOOT_PROVIDER_ID;
import static uk.org.toot.synth.id.TootSynthControlsId.VSTI_SYNTH_ID;

public class VstiSynthServiceProvider extends SynthServiceProvider
{
	private static final String VSTI_CACHE = "vsti.cache";
	
	public VstiSynthServiceProvider() {
		super(TOOT_PROVIDER_ID, "Toot Software", "VSTi", "0.1");
		File tootdir = new File(System.getProperty("user.home"), "toot");
		File synthdir = new File(tootdir, "synths");
		synthdir.mkdirs();
		File vstiCache = new File(synthdir, VSTI_CACHE);
		if ( !vstiCache.exists() ) VST.scan(vstiCache, true);
		readCache(vstiCache);
	}

	protected void readCache(File cache) {
		System.out.println("Reading "+VSTI_CACHE);
		try {
			BufferedReader br = new BufferedReader(new FileReader(cache));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(", ");
				if ( parts.length > 1 ) {
					addVstiControls(parts[1], parts[0]);
				}
			}
			br.close();
		} catch ( Exception e ) {
			System.err.println("Failed to read "+cache.getPath());
			e.printStackTrace();
		}
	}
	
	protected void addVstiControls(String name, String filename) {
		addControls(VstiSynthControls.class, VSTI_SYNTH_ID, name, filename, "0.1");		
	}
	
	@Override
    protected SynthControls createControls(ServiceDescriptor d) {
		Class<VstiSynthControls> clazz = VstiSynthControls.class;
		try {
			Constructor<VstiSynthControls> ctor = 
				clazz.getConstructor(ServiceDescriptor.class);
			return ctor.newInstance(d);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
    }
    
	public MidiSynth createSynth(CompoundControl c) {
		return new SimpleVstiSynth((VstiSynthControls)c);
	}
}
