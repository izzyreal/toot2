/* Copyright Steve Taylor 2006 */

package uk.org.toot.control;

import java.util.List;

public interface CompoundControlPersistence
{
    /*#List getPresets(CompoundControl c);*/
    List<String> getPresets(CompoundControl c);

    void loadPreset(CompoundControl c, String name);

    void savePreset(CompoundControl c, String name);
}
