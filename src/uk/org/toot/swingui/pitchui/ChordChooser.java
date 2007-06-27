package uk.org.toot.swingui.pitchui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import uk.org.toot.pitch.*;

public class ChordChooser extends JPanel 
{
	private ScaleCombo scaleCombo;
	private ModeChordsView modeChordsView;
	
	public ChordChooser() {
		build();
	}
	
	protected void build() {
		setLayout(new BorderLayout());
		scaleCombo = new ScaleCombo();
		add(scaleCombo, BorderLayout.NORTH);
		modeChordsView = new ModeChordsView();
		add(modeChordsView, BorderLayout.CENTER);
		
		// update scale when scaleCombo changed
		scaleCombo.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Scale scale = Scales.getScale((String)scaleCombo.getSelectedItem());
					ChordChooser.this.setScale(scale);
				}
			}
		);
		Scale scale = Scales.getScale((String)scaleCombo.getSelectedItem());
		setScale(scale);
	}
	
	public void setScale(Scale scale) {
		modeChordsView.setScale(scale);			
	}
}
