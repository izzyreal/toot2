// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.automation;

import javax.sound.midi.*;
import uk.org.toot.control.*;
import uk.org.toot.control.automation.MidiPersistence;
import uk.org.toot.control.automation.MidiSequenceSnapshotAutomation;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.SynthRackControls;
import uk.org.toot.synth.SynthServices;

import static uk.org.toot.control.automation.ControlSysexMsg.*;
import static uk.org.toot.midi.message.MetaMsg.*;

/**
 * Stores and recalls synth rack automation snaphots as Midi Sequences.
 * To concretise this class extend it and:
 *  Implement configure(String name) to call configureSequence(Sequence s)
 *  Implement recall(String name) to call recallSequence(Sequence s)
 *  Implement store(String name) to call storeSequence(String name)
 */
public class SynthRackControlsMidiSequenceSnapshotAutomation
	implements MidiSequenceSnapshotAutomation
{
	private SynthRackControls rackControls;
	private String synthNames = "ABCDEFGH";
	
    public SynthRackControlsMidiSequenceSnapshotAutomation(SynthRackControls controls) {
    	rackControls = controls;
    }

    protected String encodeRackPlace(int synth, int chan) {
    	return synthNames.substring(synth, 1+synth)+String.valueOf(chan);
    }
    
    protected int decodeSynth(String rackPlace) {
    	return rackPlace.charAt(0) - 'A';
    }
    
    protected int decodeChan(String rackPlace) {
    	return Integer.valueOf(rackPlace.substring(1));
    }
    
    public void configureSequence(Sequence snapshot) {
        Track[] tracks = snapshot.getTracks();
        Track track;
        String rackPlace;
        SynthControls synthControls;
        for ( int t = 0; t < tracks.length; t++ ) {
            track = tracks[t];
            MidiMessage msg = track.get(0).getMessage();
            if ( !isMeta(msg) ) continue;
           	rackPlace = getString(msg);
           	msg = track.get(1).getMessage();
           	if ( !isControl(msg) ) continue;
            String name = SynthServices.lookupModuleName(
                        getProviderId(msg), getModuleId(msg));
            if ( name == null ) continue;
            synthControls = SynthServices.createControls(name);
            if ( synthControls == null ) continue;
           	int synth = decodeSynth(rackPlace);
           	int chan = decodeChan(rackPlace);
           	try {
           		rackControls.setSynthControls(synth, chan, synthControls);
           	} catch ( Exception e ) {
           		e.printStackTrace();
           	}
        }
    }

    public void recallSequence(Sequence snapshot) {
        Track[] tracks = snapshot.getTracks();
        Track track;
        SynthControls module = null;
        for ( int t = 0; t < tracks.length; t++ ) {
            track = tracks[t];
            MidiMessage msg = track.get(0).getMessage();
            if ( !isMeta(msg) ) {
                System.out.println("recall: no name in track "+t);
                continue;
            }
            String rackPlace = getString(msg);
           	int synth = decodeSynth(rackPlace);
           	int chan = decodeChan(rackPlace);
	        module = rackControls.getSynthControls(synth, chan);
        	if ( module == null ) {
            	continue;
        	}
            int providerId = module.getProviderId();
            int moduleId = module.getId();
            for ( int i = 1; i < track.size(); i++ ) {
                msg = track.get(i).getMessage();
                if ( !isControl(msg) ) continue;
                int pid = getProviderId(msg);
                int mid = getModuleId(msg);
                if ( pid != providerId || mid != moduleId ) continue;
                int cid = getControlId(msg);
                Control control = module.deepFind(cid);
                if ( control == null ) {
                    continue;
                }
                int newValue = getValue(msg);
		        if ( newValue == control.getIntValue() ) continue;
//                System.out.println("recall: "+control.getControlPath());
                control.setIntValue(newValue);
            }
        }
    }

    public Sequence storeSequence(String name) {
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
        for ( int synth = 0; synth < rackControls.getMidiSynthCount(); synth++ ) {
        	for ( int chan = 0; chan < 16; chan++ ) {
        		SynthControls synthControls = rackControls.getSynthControls(synth, chan);
        		if ( synthControls == null ) continue;
        		String place = encodeRackPlace(synth, chan);
        		/*            AutomationControls autoc = strip.find(AutomationControls.class);
            	if ( autoc != null && !autoc.canStore() ) continue; */
        		Track t = snapshot.createTrack();
        		try {
        			MidiMessage msg = createMeta(TRACK_NAME, place);
        			t.add(new MidiEvent(msg, 0L));
        		} catch ( InvalidMidiDataException imde ) {
        			System.out.println("store: error storing synth at "+place);
        		}
        		CompoundControl cc = synthControls;
//      		System.out.println("store: storing module "+cc.getName());
        		providerId = cc.getProviderId();
        		moduleId = cc.getId();
        		instanceIndex = 0; //cc.getInstanceIndex();
        		MidiPersistence.store(providerId, moduleId, instanceIndex, cc, t);
        	}
        }
        return snapshot;
    }
}
