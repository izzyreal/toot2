package uk.org.toot.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import com.synthbot.audioplugin.vst.vst2.*;

public class Vst
{
	// any more than 127 and dynamic automation won't work
	private final static int MAX_PLUGIN_ID = 127;
	
	private static List<File> paths = new java.util.ArrayList<File>();

	// assume linux uses dlls somehow, WINE?
	// assume Mac uses Mach-O, not Carbon VSTs
	private static final String EXT =
		System.getProperty("os.name").toLowerCase().startsWith("mac os x") ? ".vst" : ".dll";
	
	public static void addPluginPath(String path) {
		paths.add(new File(path));
	}
	
	/**
	 * Scan VST plugins and cache their details for service provision.
	 * This method should be called when the cache does not exist.
	 * If the cache already exists rescan() should be called instead.
	 * @param cache
	 * @param synth
	 */
	public static void scan(File cache, boolean synth) {
		if ( cache.exists() ) return; // mustn't overwrite existing cache
		scan(new VstPrintStreamScanner(cache), synth);
	}
	
	public static void scan(VstScanner scanner, boolean synth) {
		try {
			scanner.begin();
			for ( File path : paths ) {
				scan(path, synth, scanner);
			}
			scanner.end();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	protected static void scan(File path, boolean synth, VstScanner scanner) {
		File[] files = path.listFiles();
		for ( int i = 0; i < files.length; i++ ) {
			File file = files[i];
			if ( file.isDirectory() ) {
				scan(file, synth, scanner);
				continue;
			}
			String filename = file.getPath();
			if ( filename.endsWith(EXT) ) {
				JVstHost2 vst = null;
				try {
					System.out.print(filename+" creating... ");
					vst = JVstHost2.newInstance(file);
					vst.setSampleRate(44100f);
					vst.setBlockSize(8800);
					if ( vst.isSynth() == synth ) {
						String uid = vst.getUniqueId();
						String effectName = vst.getEffectName().replace(',', ' ');
						String vendorName = vst.getVendorName().replace(',', ' ');
						System.out.print(effectName+", "+vendorName+", "+
							vst.numInputs()+"/"+vst.numOutputs()+"/"+vst.numParameters()+
							", uid="+uid+", "+vst.getVstVersion());
						scanner.each(uid, filename, effectName, vendorName);
					}
					System.out.print(", off and unload... ");
					vst.turnOffAndUnloadPlugin();
					System.out.println("unloaded");
					vst = null;
				} catch ( Exception e ) {
					System.out.println("\n"+filename+" "+e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Rescan VST plugins and merge details into an existing cache.
	 * ids must be preserved.
	 * Names must be preserved to maintain user changes.
	 * Disables must be preserved.
	 * Plugins may have been added, deleted or moved.
	 * @param cache
	 * @param synth
	 */
	public static void rescan(File cache, boolean synth) {
		if ( !cache.exists() ) {
			scan(cache, synth);
			return;
		}
		
		final VstPluginInfo[] infos = new VstPluginInfo[MAX_PLUGIN_ID+1];
		final HashMap<String, VstPluginInfo> map = new HashMap<String, VstPluginInfo>();
		
		VstPluginInfo info;
		
		// read in existing cache
		try {
			BufferedReader br = new BufferedReader(new FileReader(cache));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(", ");
				if ( parts.length >= 5 ) {
					int id = Integer.parseInt(parts[0]);
					int pos = Math.abs(id);
					info = new VstPluginInfo(id, parts[1], parts[2], parts[3], parts[4]);
					infos[pos] = info; 				// array indexed by abs(id)
					map.put(info.getUid(), info); 	// map keyed by uid
				}
			}
			br.close();
		} catch ( Exception e ) {
			System.err.println("Failed to read "+cache.getPath());
			e.printStackTrace();
		}
		
		// update preserving pertinent details
		VstScanner scanner = new VstScanner() {
			public void begin() {}

			public void each(String uid, String filename, String effectName, String vendorName) {
				VstPluginInfo info = map.get(uid);
				if ( info == null ) { 	// not found in cache
					for ( int i = 1; i <= MAX_PLUGIN_ID; i++) {
						if ( infos[i] == null ) {
							VstPluginInfo newInfo = new VstPluginInfo(i, uid, filename, effectName, vendorName);
							infos[i] = newInfo;					// plugin added
							map.put(newInfo.getUid(), info); 	// map keyed by uid
							return;
						}
					}
					System.err.println("Too many VST plugins, failed to cache "+effectName);
				} else {				// found in cache
					info.setPath(filename); // in case plugin has moved
				}
			}

			public void end() {}
		};
		scan(scanner, synth);

		// deal with plugin deletions (after moves for simplicity)
		for ( int i = 1; i <= MAX_PLUGIN_ID; i++) {
			info = infos[i];
			if ( info == null ) continue;
			File file = new File(info.getPath());
			if ( !file.exists() ) info.disable();
			// we keep the info in case plugin reappears
			// then it will keep its id
		}
				
		// backup cache
		File backup = new File(cache.getPath()+".backup");
		if ( backup.exists() ) backup.delete();
		cache.renameTo(backup);

		// write out updated cache
		try {
			PrintStream ps = new PrintStream(cache);
			for ( int i = 1; i <= MAX_PLUGIN_ID; i++) {
				info = infos[i];
				if ( info == null ) continue;
				ps.println(info.getId()+", "+info.getUid()+", "+info.getPath()+", "+
						info.getEffectName()+", "+info.getVendorName());			
			}
			ps.close();
		} catch ( Exception e ) {
			e.printStackTrace();
			if ( cache.exists() ) cache.delete();
			backup.renameTo(cache);
			System.out.println("Restored "+cache.getName()+" from backup");
		}
	}
	
	protected static class VstPluginInfo
	{
		private int id;
		private String uid;
		private String path;
		private String effectName;
		private String vendorName;
		
		public VstPluginInfo(int id, String uid, String path, String effectName, String vendorName) {
			this.id = id;
			this.uid = uid;
			this.path = path;
			this.effectName = effectName;
			this.vendorName = vendorName;
		}

		public void disable() {
			if ( id > 0 ) id = -id;
		}
		
		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @param path the path to set
		 */
		public void setPath(String path) {
			this.path = path;
		}

		/**
		 * @return the effectName
		 */
		public String getEffectName() {
			return effectName;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @return the uid
		 */
		public String getUid() {
			return uid;
		}

		/**
		 * @return the vendorName
		 */
		public String getVendorName() {
			return vendorName;
		}
	}
	
	public interface VstScanner
	{
		public void begin() throws Exception;
		public void each(String uid, String filename, String effectName, String vendorName);
		public void end();
	}
	
	protected static class VstPrintStreamScanner implements VstScanner
	{
		private File file;
		private PrintStream ps;
		private int id = 1;
		
		public VstPrintStreamScanner(File file) {
			this.file = file;
		}
		
		public void begin() throws Exception {
			System.out.println("Writing "+file.getName());
			ps = new PrintStream(file);
		}

		public void each(String uid, String filename, String effectName, String vendorName) {
			ps.println((id++)+", "+uid+", "+filename+", "+effectName+", "+vendorName);			
		}

		public void end() {
			ps.close();		
			System.out.println("Wrote "+file.getName());
		}
	}
}
