package uk.org.toot.misc;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import com.synthbot.audioplugin.vst.vst2.*;

public class VST
{
	private static List<File> paths = new java.util.ArrayList<File>();

	// assume linux uses dlls somehow, WINE?
	// assume Mac uses Mach-O, not Carbon VSTs
	private static final String EXT =
		System.getProperty("os.name").toLowerCase().startsWith("mac os x") ? ".vst" : ".dll";
	
	public static void addPluginPath(String path) {
		paths.add(new File(path));
	}
	
	public static void scan(File cache, boolean synth) {
		System.out.println("Writing "+cache.getName());
		try {
			PrintStream ps = new PrintStream(cache);
			for ( File path : paths ) {
				scan(path, synth, cache, ps);
			}		
			ps.close();
			System.out.println("Wrote "+cache.getName());
		} catch ( Exception e ) {
			System.err.println("Failed to write "+cache.getPath());			
			e.printStackTrace();
		}
	}
	
	protected static void scan(File path, boolean synth, File cache, PrintStream ps) {
		File[] files = path.listFiles();
		for ( int i = 0; i < files.length; i++ ) {
			File file = files[i];
			if ( file.isDirectory() ) {
				scan(file, synth, cache, ps);
				continue;
			}
			String filename = file.getPath();
			if ( filename.endsWith(EXT) ) {
				JVstHost2 vst = null;
				try {
					vst = JVstHost2.newInstance(file);
					if ( vst.isSynth() == synth ) {
						String effectName = vst.getEffectName().replace(',', ' ');
						String vendorName = vst.getVendorName().replace(',', ' ');
						System.out.println(filename+", "+effectName+", "+vendorName+", "+
							vst.numInputs()+"/"+vst.numOutputs()+"/"+vst.numParameters()+
							", id="+vst.getUniqueId()+", "+vst.getVstVersion());
						ps.println(filename+", "+effectName+", "+vendorName);
					}
					vst.turnOffAndUnloadPlugin();
					vst = null;
				} catch ( Exception e ) {
					System.err.println(filename+" "+e.getMessage());
				}
			}
		}		
	}
	

}
