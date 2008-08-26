// Copyright (C) 2008 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.project.automation;

import java.io.File;
import uk.org.toot.project.SingleProject;
import uk.org.toot.project.ProjectListener;
import uk.org.toot.control.automation.MidiFileSnapshotAutomation;

public class ProjectMidiFileSnapshotAutomation
{
    /**
     * The sub directory for snapshots
     */
    private final static String SNAPSHOT_DIR = "snapshots";
    
    private SingleProject projectManager;
    private ProjectListener projectListener;

    public ProjectMidiFileSnapshotAutomation(final MidiFileSnapshotAutomation auto,
        							SingleProject p) {
        projectManager = p;
        projectListener = new ProjectListener() {
            public void open() {
		        auto.setPath(new File(projectManager.getCurrentProjectPath(),
                    											SNAPSHOT_DIR));
                auto.configure("default");
                auto.recall("default");
            }
            public void save() {
		        auto.setPath(new File(projectManager.getCurrentProjectPath(),
                    											SNAPSHOT_DIR));
                auto.store("default");
            }
        };
        projectManager.addProjectListener(projectListener); // remove ??? !!! !!!
    }
}
