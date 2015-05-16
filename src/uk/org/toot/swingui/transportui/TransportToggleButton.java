/* Generated by TooT */

package uk.org.toot.swingui.transportui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JToggleButton;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

@SuppressWarnings("serial")
public class TransportToggleButton extends JToggleButton
{
    private PropertyChangeListener changeListener;
    private ActionListener actionListener;

    public TransportToggleButton(final Action action) {
        super(action);
        setText("");
        setSelected(((TransportAction)action).isSelected());
        changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
//                Log.debug(event.getPropertyName());
                if ( event.getPropertyName().equals("selected") ) {
                    boolean sel = ((Boolean)event.getNewValue()).booleanValue();
                    setSelected(sel);
//                    System.out.println(action.getValue(action.NAME)+" "+sel);
                }
            }
        };
		actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setSelected(((TransportAction)action).isSelected());
            }
        };
    }

    public void addNotify() {
        super.addNotify();
        getAction().addPropertyChangeListener(changeListener);
        addActionListener(actionListener);
    }

    public void removeNotify() {
        removeActionListener(actionListener);
        getAction().removePropertyChangeListener(changeListener);
        super.removeNotify();
    }
}
