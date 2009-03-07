// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.tool;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.control.*;
import java.util.List;

public class DenormalControls extends AudioControls
{
    private static int MODE_ID = 1;

    private static List<Object> modeValues;

    static {
        modeValues = new java.util.ArrayList<Object>();
        modeValues.add("Count");
        modeValues.add("Zero");
    }

    public DenormalControls() {
        super(ToolIds.DENORMAL_ID, "Denorm");
        add(new ModeControl());
        IntegerLaw law = new IntegerLaw(3, 7, "");
        add(new IntegerControl(ToolIds.DENORMAL_ID+1, "Chan", law, 0f, 5));
    }

    public boolean canBypass() { return true; }

    private class ModeControl extends EnumControl
    {
        public ModeControl() {
            super(MODE_ID, "Mode", "Zero");
        }

        public List getValues() {
            return modeValues;
        }
    }
}
