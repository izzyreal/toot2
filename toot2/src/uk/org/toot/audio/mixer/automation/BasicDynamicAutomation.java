/* Copyright Steve Taylor 2006 */

package uk.org.toot.audio.mixer.automation;

import uk.org.toot.audio.mixer.MixerControls;

/**
 * This class ensures mixer strips have dynamic automation controls.
 */
abstract public class BasicDynamicAutomation extends BasicAutomation
{
    public BasicDynamicAutomation(MixerControls controls) {
        super(controls);
    }

    protected void ensureAutomationControls(AutomationControls autoc) {
        autoc.ensureDynamicControls();
    }
}
