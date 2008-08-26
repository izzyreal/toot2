// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.control.automation;

import javax.sound.midi.*;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;

import static uk.org.toot.control.automation.ControlSysexMsg.*;

// used by CompoundControlMidiPersistence and AbstractMixerControlMidiAiutomation
public class MidiPersistence
{
    /** @link dependency */
    /*#ControlSysexMsg lnkControlSysexMsg;*/

    private MidiPersistence() {
        // prevent instantiation
    }

    // can be used to store a strip or a module
    public static void store(int providerId, int moduleId, int instanceIndex,
									        CompoundControl parent, Track t) {
        for ( Control c : parent.getMemberControls() ) {
            if ( c instanceof CompoundControl ) {
	            store(providerId, moduleId, instanceIndex, (CompoundControl)c, t);
            } else {
		        try {
                    if ( !c.isIndicator() && c.getId() > 0 ) {
	    		    	MidiMessage msg = createControl(
   		        			providerId, moduleId, instanceIndex, c.getId(), c.getIntValue());
	                	t.add(new MidiEvent(msg, 0L));
                    }
		        } catch ( InvalidMidiDataException imde ) {
        		}
            }
        }
    }
}
