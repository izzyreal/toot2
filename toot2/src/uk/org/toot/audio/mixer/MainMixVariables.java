/* Copyright Steve Taylor 2006 */

package uk.org.toot.audio.mixer;

import uk.org.toot.control.EnumControl;

public interface MainMixVariables extends MixVariables
{
    EnumControl getRouteControl();
}
