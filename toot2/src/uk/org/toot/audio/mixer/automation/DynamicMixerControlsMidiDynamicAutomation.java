/* Copyright Steve Taylor 2006 */

package uk.org.toot.audio.mixer.automation;

import uk.org.toot.audio.mixer.MixerControls;
import uk.org.toot.project.SingleProject;
import javax.sound.midi.MidiMessage;

public class DynamicMixerControlsMidiDynamicAutomation
    extends MixerControlsMidiDynamicAutomation
{
    public DynamicMixerControlsMidiDynamicAutomation(MixerControls controls) {
        super(controls);
    }

    protected void write(String name, MidiMessage msg) {
    }
}
