package uk.org.toot.swingui.controlui;

import uk.org.toot.control.Control;

/**
 * An interface that control components should implement such that MIDI
 * learning can be easily setup from the UI.
 * @author pjl
 */
public interface ControlComponent 
{
	Control getControl();
}
