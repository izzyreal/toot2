package uk.org.toot.swingui.pitchui;

import java.util.List;

import javax.swing.JList;

import uk.org.toot.pitch.Chord;

public class ChordList extends JList 
{
	public ChordList() {
		super();
	}

	public void setChords(List<Chord> chords) {
		setListData(chords.toArray());
	}

	public void setChordsVoicings(List<Chord.Voicing> voicings) {
		setListData(voicings.toArray());
	}

	public void setChordsPitchedVoicings(List<Chord.PitchedVoicing> pitchedVoicings) {
		setListData(pitchedVoicings.toArray());
	}
}
