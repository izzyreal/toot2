// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.swingui.controlui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uk.org.toot.control.EnumControl;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.BorderFactory;
import java.util.Observable;

public class EnumControlPanel extends ControlPanel
{
    private final EnumControl control;
    private JButton button;
    private JPopupMenu popupMenu;
	private ActionListener popupListener;
	private ActionListener buttonListener;
	private String buttonText;

    public EnumControlPanel(final EnumControl control) {
        super(control);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.control = control;
        final String name = abbreviate(control.getValue().toString());
        buttonText = name;
        button = new JButton(name) {
        	@Override
            public Dimension getMaximumSize() {
                Dimension size = super.getPreferredSize();
                if ( control.isWidthLimited() ) {
	                size.width = 40;
                } else {
                    size.width = 128;
                }
                return size;
            }
            @Override
            public String getText() {
            	return buttonText;
            }
        };
   	    popupListener = new ActionListener() {
       		public void actionPerformed(ActionEvent ae) {
       			String cmd = ae.getActionCommand();
       			for ( Object o : control.getValues() ) {
       				if ( o.toString().equals(cmd) ) {
       		            control.setValue(o);
       					return;
       				}
       			}
    		}
    	};
        popupMenu = createPopupMenu();
        buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                popupMenu.show(button, 0, 0);
            }
        };
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setAlignmentX(0.5f);
        add(button);
    }

    public void addNotify() {
        super.addNotify();
   	    button.addActionListener(buttonListener);
    }

    public void removeNotify() {
   	    button.removeActionListener(buttonListener);
        super.removeNotify();
    }

    public void update(Observable obs, Object obj) {
        buttonText = abbreviate(control.getValue().toString());
       	button.repaint();
    }

    protected JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem item;
        for ( Object o : control.getValues() ) {
            item = new JMenuItem(o.toString());
            item.addActionListener(popupListener); // !!! what about remove !!!
            menu.add(item);
        }
        return menu;
    }
}
