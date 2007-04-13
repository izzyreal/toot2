/* Copyright Steve Taylor 2006 */

package uk.org.toot.audio.mixer.automation;

import javax.sound.midi.*;
import uk.org.toot.control.*;
import uk.org.toot.audio.core.*;
import java.util.List;
import uk.org.toot.audio.mixer.MixerControls;

import static uk.org.toot.audio.mixer.MixerControlsIds.*;
import static uk.org.toot.control.ControlSysexMsg.*;
import static uk.org.toot.midi.message.MetaMsg.*;
import static uk.org.toot.midi.message.NoteMsg.*;

/**
 * Stores and recalls mixer automations snaphots as Midi Sequences.
 * To concretise this class extend it and:
 *  Implement configure(String name) to call configureSequence(Sequence s)
 *  Implement recall(String name) to call recallSequence(Sequence s)
 *  Implement store(String name) to call storeSequence(String name)
 */
abstract public class MixerControlsMidiSequenceSnapshotAutomation extends BasicSnapshotAutomation 
{
    public MixerControlsMidiSequenceSnapshotAutomation(MixerControls controls) {
        super(controls);
    }

    protected void configureSequence(Sequence snapshot) {
        Track[] tracks = snapshot.getTracks();
        Track track;
        String stripName;
        AudioControlsChain stripControls;
        for ( int t = 0; t < tracks.length; t++ ) {
            track = tracks[t];
            MidiMessage msg = track.get(0).getMessage();
            if ( !isMeta(msg) ) continue;
           	stripName = getString(msg);
            stripControls = mixerControls.getStripControls(stripName);
            // to create strips we need to know the strip id and instanceIndex
            // only dynamically create channel and group strips
            // not fx or aux or main strips at this stage
            if ( stripControls == null ) {
                msg = track.get(0).getMessage();
                if ( !isNote(msg) ) continue;
                int stripId = getData1(msg);
                if ( stripId != CHANNEL_STRIP &&
                     stripId != GROUP_STRIP ) continue;
                int stripInstanceIndex = getData2(msg);
                stripControls = mixerControls.createStripControls(
                    stripId, stripInstanceIndex, stripName);
            }
            AutomationControls autoc = stripControls.find(AutomationControls.class);
            if ( autoc != null && !autoc.canRecall() ) continue;
            stripControls.setMutating(true);
	        // reconstruct control hierarchy !!!
            // make list of needed modules
            List<AutomationIndices> needed = new java.util.ArrayList<AutomationIndices>();
            for ( int i = 1; i < track.size(); i++ ) {
                msg = track.get(i).getMessage();
                if ( isControl(msg) ) {
                    AutomationIndices triple = new AutomationIndices(getProviderId(msg),
                        getModuleId(msg), getInstanceIndex(msg));
                	// only store if needed doesn't already contain this triple
                    if ( !needed.contains(triple) ) {
                        needed.add(triple);
                    }
                }
            }

            // deletes first to reduce move and insert costs
            // don't delete automation controls !!!
            List<String> deletions = new java.util.ArrayList<String>();
            for ( Control c : stripControls.getControls() ) {
                if ( c instanceof CompoundControl ) {
                    CompoundControl cc = (CompoundControl)c;
                    if ( !cc.canBeDeleted() ) continue;
                    AutomationIndices triple = new AutomationIndices(cc.getProviderId(),
                        cc.getId(), cc.getInstanceIndex());
                    if ( !needed.contains(triple) ) {
                        deletions.add(cc.getName());
                    }
                }
            }
            for ( String s : deletions ) {
                 stripControls.delete(s);
            }

            // moves, as a sort
            int size = stripControls.getControls().size();
            CompoundControl cc1, cc2;
            AutomationIndices ti1, ti2;
            for ( int i = 1; i < size; i++ ) {
                cc1 = (CompoundControl)stripControls.getControls().get(i-1);
                if ( !cc1.canBeMovedBefore() ) continue;
                cc2 = (CompoundControl)stripControls.getControls().get(i);
                if ( !cc2.canBeMoved() ) continue;
                ti1 = new AutomationIndices(cc1);
                ti2 = new AutomationIndices(cc2);
                int ni1 = needed.indexOf(ti1);
                int ni2 = needed.indexOf(ti2);
                if ( ni2 > 0 && ni1 > ni2 ) {
	                stripControls.move(cc2.getName(), cc1.getName());
/*                    System.out.println(stripName+
                        ": move "+cc2.getName()+" before "+cc1.getName()+
                        " because "+needed.indexOf(ti1)+" > "+needed.indexOf(ti2)); */
                }
            }

            // inserts, loop incrementing instanceIndex
            // assumes deletes and moves have been done
            // !!! but they may not have been, so cope! !!! !!!
            int ii = 0;
			AutomationIndices tin;
            AutomationIndices tia;
            CompoundControl cc;
            boolean missing = true; // assume some missing to start with
            while ( missing && ii < 10 ) {
                missing = false; // assume we'll finish on this pass
	            for ( int n = 0, a = 0; n < needed.size(); ) {
                    cc = a >= stripControls.getControls().size() ? null :
                        (CompoundControl)stripControls.getControls().get(a);
                    tin = needed.get(n);
                    if ( cc != null ) {
	                    tia = new AutomationIndices(cc);
        	            if ( tia.equals(tin) ) { // already matched
            	            a += 1;
                            n += 1;
                	        continue;
                    	} else if ( !cc.canBeInsertedBefore() ) {
        	                a += 1;
    	                    continue;
	                    }
                    }
                    if ( tin.getInstanceIndex() != ii ) { // delay insertion
                        missing = true; // need another pass
                        n += 1;
                        continue;
                    }
                    // to be inserted now
                    String name = AudioServices.lookupModuleName(
                        tin.getProviderId(), tin.getModuleId());
                    if ( name == null || name.length() == 0 ) {
/*                        System.err.println("configure: no name to lookup for "
                            +tin.getProviderId()+"/"+tin.getModuleId()+"/"+ii+
                            " in "+stripName); */
                        n += 1;
                        continue;
                    }
                    stripControls.insert(name, cc == null ? null : cc.getName());
                    n += 1;
                    a += 1;
    	        }
                ii += 1; // next instanceIndex
        	}
            if ( stripControls.getControls().size() < needed.size() ) {
                System.err.println(stripName+": only configured "+stripControls.getControls().size()+" of "+needed.size()+" needed modules");
            }
            stripControls.setMutating(false);
        }
    }

    protected void recallSequence(Sequence snapshot) {
        Track[] tracks = snapshot.getTracks();
        Track track;
        int providerId = 0;
        int moduleId = 0;
        int instanceIndex = -1;
        int controlId = 0;
        CompoundControl module = null;
        for ( int t = 0; t < tracks.length; t++ ) {
            track = tracks[t];
            MidiMessage msg = track.get(0).getMessage();
            if ( !isMeta(msg) ) {
                System.out.println("recall: no name in track "+t);
                continue;
            }
            String stripName = getString(msg);
	        CompoundControl cc = mixerControls.getStripControls(stripName);
        	if ( cc == null || !(cc instanceof AudioControlsChain) ) {
            	System.out.println("recall: no strip named "+stripName);
            	continue;
        	}
            AudioControlsChain strip = (AudioControlsChain)cc;
            AutomationControls autoc = strip.find(AutomationControls.class);
            if ( autoc != null && !autoc.canRecall() ) continue;
            module = null;
            for ( int i = 1; i < track.size(); i++ ) {
                msg = track.get(i).getMessage();
                if ( !isControl(msg) ) continue;
                int pid = getProviderId(msg);
                int mid = getModuleId(msg);
                int ii = getInstanceIndex(msg);
                int cid = getControlId(msg);
                // only find module if id's changed
                if ( module == null || pid != providerId ||
                     mid != moduleId || ii != instanceIndex ) {
                    module = strip.find(pid, mid, ii);
                    providerId = pid;
                    moduleId = mid;
                    instanceIndex = ii;
                }
                if ( module == null ) {
	                System.out.println("recall: no module "+providerId+"/"+moduleId+"/"+instanceIndex+" in "+stripName);
                    continue;
                }
                Control control = module.deepFind(cid);
                if ( control == null ) {
/*	                System.out.println("recall: no control "+cid+" in "+module.getControlPath()+" ?"+stripName);
                    for ( Control c : module.getControls() ) {
                        if ( c instanceof CompoundControl ) {
		                    for ( Control c2 : ((CompoundControl)c).getControls() ) {
		                        System.out.println(" "+c2.getName()+" "+c2.getId());
                            }
                        } else {
	                        System.out.println(" "+c.getName()+" "+c.getId());
                        }
                    } */
                    continue;
                }
                int newValue = getValue(msg);
		        if ( newValue == control.getIntValue() ) continue;
//                System.out.println("recall: "+control.getControlPath());
                control.setIntValue(newValue);
            }
        }
        // should be able to use a template of which strips/modules to recall
    }

    protected Sequence storeSequence(String name) {
        // all events are at zero tick so sequence resolution is pointless
        // also, events waste space because tick is always zero !!!
        Sequence snapshot;
        try {
        	snapshot = new Sequence(Sequence.PPQ, 1);
        } catch ( InvalidMidiDataException imde ) {
            return null;
        }
        int providerId = -1;
        int moduleId = -1;
        int instanceIndex = -1;
	    // must separate strips
        for ( Control c : mixerControls.getMemberControls() ) {
            if ( c.getId() < 0 ) continue;
            AudioControlsChain strip = (AudioControlsChain)c;
            AutomationControls autoc = strip.find(AutomationControls.class);
            if ( autoc != null && !autoc.canStore() ) continue;
            Track t = snapshot.createTrack();
	        try {
    		    MidiMessage msg = createMeta(TRACK_NAME, strip.getName());
                t.add(new MidiEvent(msg, 0L));
                // note off msg misused to allow configure to create strips
                msg = off(0, strip.getId(), strip.getInstanceIndex());
                t.add(new MidiEvent(msg, 0L));
	        } catch ( InvalidMidiDataException imde ) {
                System.out.println("store: error storing strip "+strip.getName());
       		}
            // store all modules in this strip
            for ( Control m : ((CompoundControl)c).getMemberControls() ) {
                CompoundControl cc = (CompoundControl)m;
//                System.out.println("store: storing module "+cc.getName());
                providerId = cc.getProviderId();
                moduleId = cc.getId();
                instanceIndex = cc.getInstanceIndex();
            	MidiPersistence.store(providerId, moduleId, instanceIndex, cc, t);
            }
        }
        return snapshot;
    }

    static public class AutomationIndices
    {
        private int providerId;
        private int moduleId;
        private int instanceIndex;

        public AutomationIndices(int vId, int mId, int iIndex) {
            providerId = vId;
            moduleId = mId;
            instanceIndex = iIndex;
        }

        public AutomationIndices(CompoundControl cc) {
            this(cc.getProviderId(), cc.getId(), cc.getInstanceIndex());
        }

        public int getProviderId() { return providerId; }

        public int getModuleId() { return moduleId; }

        public int getInstanceIndex() { return instanceIndex; }

        public boolean equals(Object obj) {
            if ( obj == null ) return false;
            if ( !(obj instanceof AutomationIndices) ) return false;
            AutomationIndices ti = (AutomationIndices)obj;
            return ( ti.getProviderId() == getProviderId() &&
                	 ti.getModuleId() == getModuleId() &&
                     ti.getInstanceIndex() == getInstanceIndex() );
        }

        public int hashCode() {
            return providerId ^ moduleId ^ instanceIndex;
        }
    }
}
