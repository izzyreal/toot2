/* Copyright Steve Taylor 2006 */

package uk.org.toot.audio.mixer.automation;

import uk.org.toot.audio.mixer.MixerControls;

/**
 * This class ensures mixer strips have snapshot automation controls.
 */
abstract public class BasicSnapshotAutomation extends BasicAutomation
{
    public BasicSnapshotAutomation(MixerControls controls) {
        super(controls);
    }

    protected void ensureAutomationControls(AutomationControls autoc) {
        autoc.ensureSnapshotControls();
    }
}
