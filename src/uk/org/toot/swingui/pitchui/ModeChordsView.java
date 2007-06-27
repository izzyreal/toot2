package uk.org.toot.swingui.pitchui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

import uk.org.toot.pitch.*;

public class ModeChordsView extends JPanel 
{
	private Scale scale;
	private int degrees = 0;
	
	public ModeChordsView() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}
	
	public void setScale(Scale scale) {
		int d = scale.length();
		if ( d > degrees) {
			for ( int i = degrees; i < d; i++) {
				add(new DegreeChordsView(i));
			}
		} else if ( d < degrees ) {
			for ( int i = degrees-1; i > d; i--) {
				this.remove(i);
			}
		}
		degrees = d;
		this.scale = scale;
		
		System.out.println(scale.getName()+": "+Interval.spell(scale.getIntervals()));

		for ( int i = 0; i < degrees; i++) {
			Component comp = getComponent(i);
			if ( comp instanceof DegreeChordsView ) {
				((DegreeChordsView)comp).updateChords();
			}
		}
	}
	
	class DegreeChordsView extends JPanel
	{
		private ChordList chordList;
		private int degree;
		
		public DegreeChordsView(int degree) {
			setLayout(new BorderLayout());
			add(new JLabel(String.valueOf(degree+1)), BorderLayout.NORTH);
			chordList = new ChordList();
			add(chordList, BorderLayout.CENTER);
			this.degree = degree;
		}
		
		public void updateChords() {
			int[] chordMode = scale.getChordMode(degree);
			System.out.println((degree+1)+": "+Interval.spell(chordMode));
			List<Chord> chords = Chords.fromChordMode(chordMode);
			chordList.setChords(chords);			
		}
	}
}
