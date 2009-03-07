// Copyright (C) 2009 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.project.audio;

import uk.org.toot.project.*;
import uk.org.toot.audio.mixer.AudioMixer;
import uk.org.toot.audio.system.MixerConnectedAudioSystem;
import uk.org.toot.audio.system.AudioConnection;
import java.io.*;

public class ProjectAudioSystem extends MixerConnectedAudioSystem
{
    private SingleTransportProject project;
    private ProjectListener projectListener;

    public ProjectAudioSystem(SingleTransportProject p, AudioMixer mixer) {
    	super(mixer);
        project = p;
        projectListener = new ProjectListener() {
            public void open() {
            	File connfile = getConnectionFile();
            	try {
            		if ( !connfile.exists() ) return;
            		openConnections(connfile);
            	} catch ( Exception e) {
            		System.err.println("Failed to load project audio connections: "+connfile.getPath());
            	}
            }
            public void save() {
            	File connfile = getConnectionFile();
            	try {
            		saveConnections(connfile);
            	} catch ( Exception e) {
            		System.err.println("Failed to save project audio connections: "+connfile.getPath());
            	}
            }
        };
        // TODO figure out if and when this should be removed
        project.addProjectListener(projectListener);
    }
    
    protected File getConnectionFile() {
		return new File(project.getCurrentProjectPath(), "audio.connections");
    }
    
    protected void openConnections(File file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] ports = line.split(">>");
            if ( ports.length > 1 ) {
            	String srcPort = ports[0].trim();
            	String[] src = srcPort.split("@");
            	srcPort = src[0].trim();
            	String srcLocation = src[1].trim();
            	String destPort = ports[1].trim();
            	try {
            		createConnection(srcPort, srcLocation, destPort, 0);
            	} catch ( Exception e ) {
            		System.err.println(
            			"Failed to connect :'"+srcPort+" @ "+srcLocation+"' to '"+destPort+"'");
            	}
            }
        }
        br.close();    	
    }
    
    protected void saveConnections(File file) throws FileNotFoundException {
    	System.out.println("Saving Audio Connections");
    	PrintStream ps = new PrintStream(file);
    	for (AudioConnection conn : getConnections()) {
    		if (conn.isSystem()) continue; // only list User connections
    		ps.println(conn.getOutputName()+" @ "+conn.getOutputLocation()+" >> "+conn.getInputName());
    	}
    	ps.println();
    	ps.close();
    	System.out.println("Audio Connections Saved");
    }
}
