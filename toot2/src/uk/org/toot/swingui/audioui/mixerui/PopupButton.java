/* Copyright Steve Taylor 2006 */

package uk.org.toot.swingui.audioui.mixerui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PopupButton extends JButton
{
    private ActionListener buttonListener;

    public PopupButton(final JPopupMenu popup) {
        buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                popup.show(PopupButton.this, 0, PopupButton.this.getSize().height);
            }
        };
        addActionListener(buttonListener);
    }
}
