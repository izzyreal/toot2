// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import uk.org.toot.project.*;
import java.io.*;

/**
 * An exceptionally immature MultiTrackPlayer that is ultimately
 * intended to load a different 'reel' for each project.
 */
public class ProjectMultiTrackPlayer extends MultiTrackPlayer
{
    private SingleTransportProject project;
    private ProjectListener projectListener;

    public ProjectMultiTrackPlayer(SingleTransportProject p, MultiTrackControls mtc) {
        super(p.getTransport(), mtc);
        project = p;
        projectListener = new ProjectListener() {
            public void open() {
                File path = new File(project.getCurrentProjectPath(), "audio");
                loadReel(new File(path, "reel"));
                project.getTransport().locate(0);
            }
            public void save() {
                // ???
            }
        };
        project.addProjectListener(projectListener);
    }

    public void loadReel(File reel) {
        if ( !reel.exists() || !reel.canRead() ) return;
        // clear existing tracks
        for ( int i = 0; i < getTrackLimit(); i++ ) {
            setTrack(i, null, " ");
        }

//        int nTracks = multiTrackControls.getMemberControls().length;
        try {
	        BufferedReader reader = new BufferedReader(new FileReader(reel));
    	    String line;
            File parent = reel.getParentFile();
//            System.out.println("Reel parent path "+parent);
        	while ( (line = reader.readLine()) != null ) {
            	String[] args = line.split("\\s");
                if ( args.length < 2 ) continue; // not a track assigment
                int t = Integer.valueOf(args[0]).intValue() - 1;
                String name;
                if ( args.length > 2 ) {
                    name = args[2];
                } else {
		        	int end = args[1].lastIndexOf('.');
        			name = args[1].substring(0, end > 7 ? 7 : end);
                }
                File file = new File(args[1]);
                if ( !file.isAbsolute() ) {
                    file = new File(parent, args[1]);
                }
                if ( t < getTrackLimit() ) {
		        	setTrack(t, file, name);
                } else {
                    System.err.println("Only "+getTrackLimit()+" tracks, track "+t+", "+file+" ("+name+") not loaded!");
                }
	        }
    	    reader.close();
        } catch ( IOException ioe ) {
        }
    }
}
