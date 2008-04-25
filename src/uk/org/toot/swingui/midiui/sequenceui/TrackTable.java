package uk.org.toot.swingui.midiui.sequenceui;

import uk.org.toot.midi.sequence.MidiTrack;
import uk.org.toot.swingui.miscui.ClickAdapter;
import uk.org.toot.swingui.midiui.MidiChannelCombo;
import uk.org.toot.midi.misc.GM;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
import javax.swing.AbstractListModel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import uk.org.toot.midi.core.MidiSystem;

class TrackTable extends JTable
{
//    private MidiSystem rack;
	private GMMelodyProgramModel melodyProgramModel = new GMMelodyProgramModel();
	private GMDrumProgramModel drumProgramModel = new GMDrumProgramModel();	

    public TrackTable(TrackTableModel model, MidiSystem rack) {
        super(model) ;
        setRowHeight(getRowHeight()+4);
/*        model.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModeEvent e) {
				repaint?
            }
        }}); */
//        this.rack = rack;
        setup() ;
    }

    public TrackTable(TrackTableModel model, TrackPopupMenu popup, MidiSystem rack) {
        this(model, rack) ;
        setPopup(popup) ;
    }

    public Component prepareRenderer(TableCellRenderer renderer,
                                         int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        if (!isCellSelected(rowIndex, vColIndex)) {
            c.setBackground(((TrackTableModel)getModel()).getRowColor(rowIndex, getBackground()));
        } else {
            c.setBackground(((TrackTableModel)getModel()).getSelectedColor(rowIndex, getSelectionBackground()));
        }
        return c;
    }

    protected void setPopup(TrackPopupMenu popup) {
        addMouseListener(new TrackPopupAdapter(popup)); // !! and remove? !!
    }

    protected void setup() {
        try {
	    	//        sizeColumnsToFit(0);
            Dimension dimension = getPreferredScrollableViewportSize();
            dimension.height = (5+getModel().getRowCount())*getRowHeight();
            setPreferredScrollableViewportSize(dimension);
    	    TableColumn col = getColumn("M");
        	col.setMaxWidth(20);
	        col = getColumn("S");
    	    col.setMaxWidth(20);
            col = getColumn("Track");
            col.setMinWidth(32);
            col = getColumn("Program");
            col.setMinWidth(32);
            col.setCellEditor(new ProgramCellEditor());
            col = getColumn("Bank");
            col.setMaxWidth(32);
            col.setMinWidth(32);
    		col = getColumn("Ch");
	        col.setMaxWidth(32);
            col.setMinWidth(16);
			col.setCellEditor(new DefaultCellEditor(new MidiChannelCombo()));
//            col = getColumn("Device");
//            col.setCellEditor(new DefaultCellEditor(new MidiPortCombo(rack, true)));
        } catch ( IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    private class ProgramCellEditor extends DefaultCellEditor
    {
    	public ProgramCellEditor() {
    		super(new JComboBox());
    	}
    	
    	public Component getTableCellEditorComponent(JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
    		JComboBox c = (JComboBox)super.getTableCellEditorComponent(
    				table, value, isSelected, row, column);
    		// set the model appropriate for row
    		c.setModel(TrackTable.this.getComboBoxModel(row));
    		return c;
    	}
    }
    
    protected ComboBoxModel getComboBoxModel(int row) {
    	boolean drum = ((TrackTableModel)getModel()).isDrumTrack(row);
    	int prg = ((TrackTableModel)getModel()).getProgram(row);
    	ComboBoxModel model = drum ? drumProgramModel : melodyProgramModel;
    	String prgName = (String)model.getElementAt(prg);
    	model.setSelectedItem(prgName);
    	return model;
    }
    
    private class GMMelodyProgramModel extends AbstractListModel implements ComboBoxModel
    {
    	private Object item = getElementAt(0);
    	
    	public Object getElementAt(int index) {
    		return (1+index)+" "+GM.melodicProgramName(index);
    	}
    	
    	public int getSize() {
    		return 128;
    	}

		public Object getSelectedItem() {
			return item;
		}

		public void setSelectedItem(Object anItem) {
			item = anItem;
		}
    }
    
    private class GMDrumProgramModel extends AbstractListModel implements ComboBoxModel
    {
    	private Object item = getElementAt(0);

    	public Object getElementAt(int index) {
    		return (1+index)+" "+GM.drumProgramName(index);
    	}
    	
    	public int getSize() {
    		return 128;
    	}

		public Object getSelectedItem() {
			return item;
		}

		public void setSelectedItem(Object anItem) {
			item = anItem;
		}
    }
    
    private class TrackPopupAdapter extends ClickAdapter {

        public TrackPopupAdapter(TrackPopupMenu popup) {
            super(popup);
        }

        public void showPopup(MouseEvent e) {
			int trk = rowAtPoint(e.getPoint());
            MidiTrack track = ((TrackTableModel)getModel()).getSequence().getMidiTrack(trk);
            if ( track.getChannel() < 0 ) {
                return;
            }
            changeSelection(trk, -1, false, false); // new selection
            ((TrackPopupMenu)popup).setTrack(track);
            super.showPopup(e);
        }
    }
}


