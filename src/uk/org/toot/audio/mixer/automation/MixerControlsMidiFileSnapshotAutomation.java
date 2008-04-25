// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.mixer.automation;

import java.io.File;
import java.io.IOException;
import javax.sound.midi.*;
import uk.org.toot.audio.mixer.MixerControls;
import uk.org.toot.audio.mixer.MixerControlsSnapshotAutomation;

/**
 * Redefines the snapshot automation API in terms of standard midi files.
 * @author st
 *
 */
public class MixerControlsMidiFileSnapshotAutomation
    extends MixerControlsMidiSequenceSnapshotAutomation
    implements MixerControlsSnapshotAutomation
{
    protected File snapshotPath;
    
    /**
     * The sub directory for snapshots
     */
    public final static String SNAPSHOT_DIR = "snapshots";
    
    /**
     * The file extension for snapshots
     */
    public final static String SNAPSHOT_EXT = ".snapshot";

    public MixerControlsMidiFileSnapshotAutomation(
        MixerControls controls, File path) {
        super(controls);
        snapshotPath = path;
    }

    public void configure(String name) {
        File file = getSnapshotFile(name);
        if ( file == null ) return;
        if ( !file.exists() ) return;
        try {
            configureSequence(MidiSystem.getSequence(file));
        } catch ( InvalidMidiDataException imde ) {
            System.err.println("Failed to configure Snapshot "+name);
        } catch ( IOException ioe ) {
            System.err.println("Failed to congigure or read Snapshot file "+file.getPath());
        }
    }

    public void recall(String name) {
        File file = getSnapshotFile(name);
        if ( file == null ) return;
        if ( !file.exists() ) return;
        try {
            recallSequence(MidiSystem.getSequence(file));
        } catch ( InvalidMidiDataException imde ) {
            System.err.println("Failed to recall Snapshot "+name);
        } catch ( IOException ioe ) {
            System.err.println("Failed to read Snapshot file "+file.getPath());
        }
    }

    public void store(String name) {
        File file = getSnapshotFile(name);
        if ( file == null ) return;
        if ( file.exists() ) {
            file.delete(); // !!! !!! confirmation?
        }
        Sequence snapshot = null;
        try {
            snapshot = storeSequence(name);
	        file.createNewFile();
	        MidiSystem.write(snapshot, 1, file);
//            System.out.println("Stored Snapshot "+file.getCanonicalPath());
//        } catch ( InvalidMidiDataException imde ) {
//            System.err.println("Failed to create Snapshot "+name);
        } catch ( IOException ioe ) {
            System.err.println("Failed to create or write Snapshot file "+file.getPath());
        }
    }

    public String[] list() {
        if ( snapshotPath == null ) return null;
        return snapshotPath.list();
    }

    protected File getSnapshotFile(String name) {
        if ( snapshotPath == null ) return null;
        return new File(snapshotPath, name+SNAPSHOT_EXT);
    }

}
