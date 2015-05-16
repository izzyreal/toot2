/* Generated by TooT */

package uk.org.toot.swingui.miscui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import java.net.URL;

@SuppressWarnings("serial")
public class TootBar extends JToolBar
    implements PropertyChangeListener, ActionListener
{
    public TootBar() {
        super();
   		putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    }

    public TootBar(String name) {
        super(name);
   		putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    }

   	public void propertyChange(PropertyChangeEvent pce) {
    	if ( "orientation".equals(pce.getPropertyName()) ) {
    		setOrientation(((JToolBar)getParent()).getOrientation());
    	}
	}

    public void addNotify() {
        super.addNotify();
        if ( getParent() instanceof JToolBar ) {
            JToolBar parent = (JToolBar)getParent();
            setOrientation(parent.getOrientation());
            parent.addPropertyChangeListener("orientation", this);
        }
    }

    protected JToggleButton makeToggleButton(String imageName, String actionCommand, String toolTipText, String altText, boolean selected) {
        //Create and initialize the button.
        JToggleButton button = new JToggleButton();
        button.setRolloverEnabled(true);
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);
        button.setSelected(selected);
        ImageIcon icon = getImageIcon(imageName);
        if (icon != null) { //image found
            button.setIcon(icon);
        } else { //no image found
            button.setText(altText);
        }
        return button;
    }

	protected ImageIcon getImageIcon(String pathName, String iconName) {
        ImageIcon icon = null;
   	    String imgLocation = pathName + iconName  + ".gif";
       	URL imageURL = this.getClass().getResource(imgLocation);
        if ( imageURL != null ) {
	        icon = new ImageIcon(imageURL);
        }
        return icon;
    }

    // check a few different paths to simplify jlfgr/tgr choice
	protected ImageIcon getImageIcon(String iconName) {
        ImageIcon icon = getImageIcon("/toolbarButtonGraphics/", iconName);
        if ( icon == null ) {
            System.err.println("Resource not found: "+iconName);
        }
        return icon;
    }

    protected JButton makeButton(String imageName, String actionCommand, String toolTipText, String altText, boolean enabled) {
        //Create and initialize the button.
        JButton button = new JButton();
        button.setRolloverEnabled(true);
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);
        button.setEnabled(enabled);
        ImageIcon icon = getImageIcon(imageName);
        if (icon != null) { //image found
            button.setIcon(icon);
        } else { //no image found
            button.setText(altText);
        }
        return button;
    }

    // subclasses override
    public void actionPerformed(ActionEvent e) {
    }
}
