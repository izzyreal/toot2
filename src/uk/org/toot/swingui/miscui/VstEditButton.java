package uk.org.toot.swingui.miscui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

public class VstEditButton extends JButton implements ActionListener
{
	private JVstHost2 vst;
	private String frameTitle;
	
	public VstEditButton(JVstHost2 vst, String title) {
		super("Edit");
		this.vst = vst;
		frameTitle = title;
		addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent ae) {
		if ( vst.isEditorOpen() ) {
			vst.topEditor();
		} else {
			vst.openEditor(frameTitle);
		}
	}
}
