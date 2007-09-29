package uk.org.toot.swingui.tonalityui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;

import uk.org.toot.music.tonality.*;

public class ChordChooserPanel extends JPanel 
{
	private ScaleCombo scaleCombo;
	private ModeChordsPanel modeChordsView;
	
	public ChordChooserPanel() {
		build();
	}
	
	protected void build() {
		setLayout(new BorderLayout());
		scaleCombo = new ScaleCombo();
		add(scaleCombo, BorderLayout.NORTH);
		modeChordsView = new ModeChordsPanel();
		add(modeChordsView, BorderLayout.CENTER);
		
		// update scale when scaleCombo changed
		scaleCombo.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Scale scale = Scales.getScale((String)scaleCombo.getSelectedItem());
					ChordChooserPanel.this.setScaleImpl(scale);
				}
			}
		);
		Scale scale = Scales.getScale((String)scaleCombo.getSelectedItem());
		setScaleImpl(scale);
	}
	
	public void setScale(Scale scale) {
		scaleCombo.setSelectedItem(scale);
		setScaleImpl(scale);			
	}

	protected void setScaleImpl(Scale scale) {
		modeChordsView.setScale(scale);			
	}
}
