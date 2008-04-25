package uk.org.toot.swingui.synthui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.SynthRackControls;

public class SynthRackPanel extends JPanel
{
	private SynthRackControls rackControls;
	private int midiSynthCount;
	
	public SynthRackPanel(SynthRackControls controls) {
		rackControls = controls;
		midiSynthCount = controls.getMidiSynthCount();
		TableModel tableModel = new SynthTableModel();
		JTable table = new JTable(tableModel);
		add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}
	
	private class SynthChannelButton extends JButton
	{
		private int synth;
		private int chan;
		
		public SynthChannelButton(int synth, int chan) {
			this.synth = synth;
			this.chan = chan;
		}
		
		public String toString() {
			SynthControls synthControls = rackControls.getSynthControls(synth, chan);
			return synthControls == null ? "<none>" : synthControls.getName();
		}
	}
	
	private class SynthTableModel extends AbstractTableModel
	{
		private SynthChannelButton[][] synthChannelButton;

		public SynthTableModel() {
			synthChannelButton = new SynthChannelButton[midiSynthCount][16];
			for ( int synth = 0; synth < midiSynthCount; synth++) {
				for ( int chan = 0; chan < 16; chan++ ) {
					synthChannelButton[synth][chan] = new SynthChannelButton(synth, chan);
				}
			}
		}
		
		public int getColumnCount() { return midiSynthCount; }
        
		public int getRowCount() { return 16;}
        
		public Object getValueAt(int row, int col) { 
        	return synthChannelButton[col][row]; 
        }			
		
	}
}
