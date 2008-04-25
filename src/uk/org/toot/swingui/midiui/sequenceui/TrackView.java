/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.swingui.midiui.sequenceui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import uk.org.toot.midi.sequence.Midi;
import uk.org.toot.midi.sequence.MidiTrack;
import uk.org.toot.midi.sequencer.MidiSequencer;
import uk.org.toot.midi.core.MidiSystem;
//import uk.org.toot.midi.sequence.Recorder;
import uk.org.toot.swingui.miscui.TootBar;
//import java.awt.event.ActionEvent;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiEvent;
import javax.swing.JTextField;

import static uk.org.toot.midi.message.MetaMsg.*;

public class TrackView extends JPanel
{
//    private TrackTable table;
    protected InfoBar infoBar;
    private MidiSequencer tequencer; // !!!

    public TrackView(MidiSequencer seq, MidiSystem midiSystem) {
        super(new BorderLayout());
        tequencer = seq;
        TrackTable table = new TrackTable(new TrackTableModel(tequencer.getMidiSequence(), tequencer), new TrackPopupMenu(), midiSystem);
        JPanel tablePane = new TrackTableView(table);
        // create inner panel so we can have another toolbar
//        add(innerPane, BorderLayout.CENTER);
        JPanel innerPane = new JPanel(new BorderLayout());
        innerPane.add(tablePane, BorderLayout.CENTER);
        innerPane.add(infoBar = new InfoBar(), BorderLayout.SOUTH);
        // create another inner panel so we can have another toolbar
        add(innerPane, BorderLayout.CENTER);
//        add(new TransportBar(), BorderLayout.SOUTH);
    }

    private class InfoBar extends TootBar implements MetaEventListener
    {
        public InfoBar() {
            super("Information");
            tempoF = new JTextField(5);
            add(tempoF);
            //		bar.addSeparator();
            timeSigF = new JTextField(4);
            add(timeSigF);
            //		bar.addSeparator();
            lengthF = new JTextField(5);
            add(lengthF);
            markerF = new JTextField(16);
            add(markerF);
//            tequencer.addMetaEventListener(this);
        }

        public void updateInfo() {
        	tempoF.setText(""+tequencer.getMidiSequence().getBeatsPerMinute()); // per MIDI spec
        	timeSigF.setText("4/4"); // per MIDI spec
    	    markerF.setText("");
            lengthF.setText(Midi.timePosition(tequencer.getMidiSequence().getMicrosecondLength()));
    //        	timePosF.setText(Midi.timePosition(0));

            MidiTrack trk = tequencer.getMidiSequence().getMidiTrack(0) ;

            for (int i = 0; i < trk.size(); i++) {
    	        MidiEvent ev = trk.get(i);
        	    if (ev.getTick() > 0) break; // !!
            	MidiMessage msg = ev.getMessage();
                if ( isMeta(msg) ) {
    	            meta(msg);
        	    }
        	}
    	}

        public void meta(javax.sound.midi.MetaMessage msg) {
            meta((MidiMessage)msg);
        }

        public void meta(MidiMessage msg) {
            switch (getType(msg)) {
                case TRACK_NAME: // Track name
                    //this.trackName = getString(msg) ;
                    break;
                case MARKER: // Marker
                    markerF.setText(getString(msg));
                    break;
            	case CUE_POINT: // Cue Point
            		break;
                case CHANNEL_PREFIX: // Channel Prefix, handle silently
                    break;
                case PORT_PREFIX: // Port Prefix, handle silently
                    break;
                case END_OF_TRACK: // End of Track
                    // update, may be a new track if recording
//                    fireTableChanged();
                    updateInfo();
                    break;
                case TEMPO: // Tempo
                    float bpm = getTempo(msg);
                    // truncate it to 2 digits after dot
                    bpm = (float)(Math.round(bpm * 100.0f) / 100.0f);
                    //            System.out.println("Tempo "+bpm+" bpm") ;
                    tempoF.setText(new Float(bpm).toString());
                    break;
                case SMPTE_OFFSET:
                    // getString(msg);
                    break;
                case TIME_SIGNATURE: // Time Signature
                    timeSigF.setText(getString(msg));
                    break;
                case MAJOR_MINOR_KEY: // Key and major/minor, useless
                    break;
                default: // everything not explicitly cased above
                    break;
            }
        }

        protected JTextField tempoF;
        protected JTextField timeSigF;
        protected JTextField markerF;
        protected JTextField lengthF;
    //        protected JTextField timePosF;
    }
}
