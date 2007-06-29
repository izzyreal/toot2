package uk.org.toot.swingui.pitchui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.util.List;
import uk.org.toot.pitch.Keys;
import uk.org.toot.pitch.Key;
import uk.org.toot.pitch.PitchClass;

public class KeyChooserPanel extends JPanel 
{
	private NoteField noteField;
	private KeyList keyList;
	
	public KeyChooserPanel() {
		build();
	}
	
	protected void build() {
		setLayout(new BorderLayout());
		noteField = new NoteField();
		add(noteField, BorderLayout.NORTH);
		keyList = new KeyList();
		JScrollPane scrollPane = new JScrollPane(keyList);
		add(scrollPane, BorderLayout.CENTER);
		
		// update keyList when noteField changed
		noteField.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					String notes = noteField.getText();
					setNotesImpl(PitchClass.values(notes));
				}
			}
		);
	}

	public void setNotes(int[] notes) {
		noteField.setText(PitchClass.names(notes));
		setNotesImpl(notes);
	}
	
	protected void setNotesImpl(int[] notes) {
		System.out.println("Analysing "+PitchClass.names(notes));
		List<Key> keys = Keys.withNotes(notes);
		System.out.println(keys.size()+" Keys match");
/*					for ( Key key : keys ) {
			System.out.println(key);
		} */
		keyList.setKeys(keys);
	}
}
