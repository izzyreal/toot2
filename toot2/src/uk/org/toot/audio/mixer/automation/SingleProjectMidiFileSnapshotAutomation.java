/* Copyright Steve Taylor 2006 */

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
