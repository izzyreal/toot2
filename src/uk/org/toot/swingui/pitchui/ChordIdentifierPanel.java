package uk.org.toot.swingui.pitchui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.util.List;
import uk.org.toot.pitch.Chords;
import uk.org.toot.pitch.Chord;
import uk.org.toot.pitch.PitchClass;

public class ChordIdentifierPanel extends JPanel 
{
	private NoteField noteField;
	private ChordList chordList;
	
	public ChordIdentifierPanel() {
		build();
	}
	
	protected void build() {
		setLayout(new BorderLayout());
		noteField = new NoteField();
		add(noteField, BorderLayout.NORTH);
		chordList = new ChordList();
		JScrollPane scrollPane = new JScrollPane(chordList);
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
//		System.out.println("Analysing "+PitchClass.names(notes));
		List<Chord.PitchedVoicing> voicings = Chords.withNotes(notes);
//		System.out.println(chords.size()+" Chords match");
		chordList.setChordsPitchedVoicings(voicings);
		
	}
}
