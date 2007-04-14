// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.mixer.automation;

import java.io.File;
import uk.org.toot.project.SingleProject;
import uk.org.toot.project.ProjectListener;
import uk.org.toot.audio.mixer.MixerControls;

public class SingleProjectMidiFileSnapshotAutomation extends
    MixerControlsMidiFileSnapshotAutomation
{
    private SingleProject projectManager;
    private ProjectListener projectListener;

    public SingleProjectMidiFileSnapshotAutomation(
        							MixerControls controls, SingleProject p) {
        super(controls, null);
        projectManager = p;
        projectListener = new ProjectListener() {
            public void open() {
		        snapshotPath = new File(projectManager.getCurrentProjectPath(),
                    											SNAPSHOT_DIR);
        		snapshotPath.mkdirs();
                configure("default");
                recall("default");
            }
            public void save() {
		        snapshotPath = new File(projectManager.getCurrentProjectPath(),
                    											SNAPSHOT_DIR);
        		snapshotPath.mkdirs();
                store("default");
            }
        };
        projectManager.addProjectListener(projectListener); // remove ??? !!! !!!
    }
}
