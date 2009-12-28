package uk.org.toot.swingui.controlui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.Timer;

import uk.org.toot.control.FloatControl;

public class FloatIndicatorPanel extends ControlPanel
{
	private JLabel nameLabel;
	private JLabel valueLabel;
	
	public FloatIndicatorPanel(final FloatControl indicator) {
		super(indicator);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        nameLabel = new JLabel(indicator.getName(), JLabel.CENTER);
        add(nameLabel);
        valueLabel = new JLabel(indicator.getValueString(), JLabel.CENTER);
        add(valueLabel);
        Timer t = new Timer(500,
    	    new ActionListener() {
	            public void actionPerformed(ActionEvent ae) {
	           		valueLabel.setText(indicator.getValueString());
                }
        	}
        );
    	setTimer(t);
	}
}
