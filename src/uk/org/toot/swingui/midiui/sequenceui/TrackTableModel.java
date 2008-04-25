package uk.org.toot.swingui.midiui.sequenceui;

import uk.org.toot.midi.misc.GM;
import uk.org.toot.midi.sequence.MidiSequence;
import uk.org.toot.midi.sequence.MidiTrack;
import uk.org.toot.midi.sequencer.MidiSequencer;
import uk.org.toot.midi.sequencer.TrackControls;
import uk.org.toot.swingui.midiui.MidiColor;
import java.awt.Color;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.sound.midi.InvalidMidiDataException;

class TrackTableModel extends AbstractTableModel
{
    private MidiSequence sequence;
    private MidiSequencer sequencer;
    //						            Bass,   Double Bass, 1, , 2
    final String[] names = { "M", "S", "Track", "Program", "Bank", "Ch" };
    private int columnCount = names.length;
    private static final int MUTE = 0;
    private static final int SOLO = MUTE+1;
    private static final int TRACK = SOLO+1;
    private static final int PROGRAM = TRACK+1;
    private static final int BANK = PROGRAM+1;
    private static final int CHANNEL = BANK+1;
//    private final static int INSTRUMENT = CHANNEL+1;
//    private final static int DEVICE = CHANNEL+1;

	public TrackTableModel(MidiSequence aSequence, MidiSequencer aSequencer) {
        sequence = aSequence;
        // !!! !!! sequencer only used for getPlaybackTracks, prg update
        // latter should be by sequencer listening to sequence changes
        sequencer = aSequencer;
        if ( sequence == null ) {
            System.err.println("TTM: null sequence passed to <init>");
            return;
        }
//        Log.debug("TTM: adding ChangeListener to "+sequence);
        sequence.getChangeSupport().addChangeListener(
            new ChangeListener() {
            	public void stateChanged(ChangeEvent ce) {
                	fireTableDataChanged();
//                    Log.debug("TTM: fired TableDataChanged");
            	}
        	});
    }

	public TrackTableModel(MidiSequence sequence, MidiSequencer sequencer, int columnCount) {
		this(sequence, sequencer);
        this.columnCount = columnCount;
    }

    private Float getHue(MidiTrack track) { return (Float)track.getClientProperty("Hue"); }

    public Color getRowColor(int row, Color background) {
        MidiTrack track = sequence.getMidiTrack(row);
        Float fhue = getHue(track);
        if ( track.getChannel() < 0 || fhue == null )
            return background;
        return MidiColor.asHSB(fhue, 0.25f, 1.0f);
    }

    public Color getSelectedColor(int row, Color color) {
        MidiTrack track = sequence.getMidiTrack(row);
        Float fhue = getHue(track);
        if ( track.getChannel() < 0 || fhue == null )
            return color;
        return MidiColor.asHSB(fhue, 0.5f, 0.85f);
    }

    public int getColumnCount() { return columnCount; }

    public int getRowCount() {
        if ( sequence == null ) return 0;
        return sequence.getMidiTrackCount();
    }

    public boolean isDrumTrack(int row) {
        return sequence.getMidiTrack(row).isDrumTrack();
    }
    
    public int getProgram(int row) {
        MidiTrack track = sequence.getMidiTrack(row);
        return track.getProgram();
    }
    
    public int getBank(int row) {
        MidiTrack track = sequence.getMidiTrack(row);
        return track.getBank();
    }
    
    public String getProgramName(int row) {
        MidiTrack track = sequence.getMidiTrack(row);
        if ( track.getChannel() < 0 ) return "";
    	int prg = getProgram(row);
    	if ( prg < 0 ) return "";
    	String name = isDrumTrack(row) ? GM.drumProgramName(prg) : GM.melodicProgramName(prg);
    	return (1+prg)+" "+name;
    }
    
    public String channelsString(int used) {
        if ( used == 0 ) return "";
        for ( int i = 0; i < 16; i++ ) {
            int mask = 1 << i;
            if ( (used & mask) == mask ) return String.valueOf(1+i);
            if ( (used & mask) != 0 ) return String.valueOf(1+i)+"+";
        }
        return "!";
    }

    public Object getValueAt(int trk, int col) {
        MidiTrack track = getSequence().getMidiTrack(trk);
        TrackControls controls = sequencer.getTrackControls(track);
        switch (col) {
            case MUTE:
                return controls == null ? false : Boolean.valueOf(controls.isMute());
            case SOLO:
                return controls == null ? false : Boolean.valueOf(controls.isSolo());
            case TRACK:
                return track.getTrackName();
            case PROGRAM:
            	return getProgramName(trk);
            case BANK:
            	int bank = getBank(trk);
            	if ( bank < 0 ) return "";
            	return 1+bank;
            case CHANNEL:
                return 1+track.getChannel();
            default:
                break;
        }
        return "?";
    }

    public String getColumnName(int col) { return names[col]; }

    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int trk, int col) {
        MidiTrack track = getSequence().getMidiTrack(trk);
        if ( col == MUTE ) return true;
        if ( track.getChannel() >= 0 ) {
	        if ( col == SOLO    ) return true;
	       	if ( col == PROGRAM ) return true;
	       	if ( col == BANK    ) return true;
	       	if ( col == CHANNEL ) return true;
        }
        if ( col == TRACK && trk > 0 ) return true;
        return false;
    }

    public void setValueAt(Object val, int trk, int col) {
        MidiTrack track = getSequence().getMidiTrack(trk);
        TrackControls controls = sequencer.getTrackControls(track);
        switch (col) {
            case MUTE:
                controls.setMute(((Boolean)val).booleanValue());
                break;
            case SOLO:
                controls.setSolo(((Boolean)val).booleanValue());
                break;
        	case TRACK:
                try {
	                track.setTrackName((String)(val));
        		} catch ( InvalidMidiDataException imde ) {
            		imde.printStackTrace();
        		}
                break;
        	case PROGRAM:
//                int prg = ((Integer)val).intValue();
        		String str = (String)val;
        		int prg = Integer.decode(str.split("\\s")[0]).intValue()-1;
                track.setProgram(prg);

        		// !!! synthesizer should listen for sequence property change
/*                if ( sequencer.isRunning() && sequencer instanceof MidiSequencer ) {
					((MidiSequencer)sequencer).updateProgram(trk, prg);
                } */
                break;
        	case BANK:
                int bank = ((Integer)val).intValue() - 1;
                track.setBank(bank);
        		break;
	        case CHANNEL:
                int chan = ((Integer)val).intValue() - 1;
                track.changeChannel(chan);
                break;
/*        	case DEVICE:
                try {
                	track.setDeviceName((String)val);
        		} catch ( InvalidMidiDataException imde ) {
            		imde.printStackTrace();
        		}
                break; */
        }
        fireTableCellUpdated(trk, col);
    }

    public MidiSequence getSequence() {
        return sequence;
    }
}


