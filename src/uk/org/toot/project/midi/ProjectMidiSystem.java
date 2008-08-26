// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.project.midi;

import uk.org.toot.project.*;
import uk.org.toot.midi.core.DefaultConnectedMidiSystem;
import uk.org.toot.midi.core.MidiConnection;
import java.io.*;

public class ProjectMidiSystem extends DefaultConnectedMidiSystem
{
    private SingleTransportProject project;
    private ProjectListener projectListener;

    public ProjectMidiSystem(SingleTransportProject p) {
        project = p;
        projectListener = new ProjectListener() {
            public void open() {
            	try {
            		File connfile = getConnectionFile();
            		if ( !connfile.exists() ) return;
            		openConnections(connfile);
            	} catch ( Exception e) {
            		System.err.println("Failed to load project midi connections");
            	}
            }
            public void save() {
            	try {
            		File connfile = getConnectionFile();
//            		if ( connfile.exists() ) return;
            		saveConnections(connfile);
            	} catch ( Exception e) {
            		System.err.println("Failed to save project midi connections");
            	}
            }
        };
        // TODO figure out if and when this should be removed
        project.addProjectListener(projectListener);
    }
    
    protected File getConnectionFile() {
		return new File(project.getCurrentProjectPath(), "midi.connections");
    }
    
    protected void openConnections(File file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] ports = line.split(">>");
            if ( ports.length > 1 ) {
                createMidiConnection(ports[0].trim(), ports[1].trim(), 0);
            }
        }
        br.close();    	
    }
    
    protected void saveConnections(File file) throws FileNotFoundException {
    	PrintStream ps = new PrintStream(file);
    	for (MidiConnection conn : getMidiConnections()) {
    		if (conn.isSystem()) continue; // only list User connections
    		ps.println(conn.getMidiOutput().getName()+" >> "+conn.getMidiInput().getName());
    	}
    	ps.println();
    	ps.close();
    }
}
