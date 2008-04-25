// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.swingui.projectui;

import uk.org.toot.swingui.transportui.*;
import uk.org.toot.project.*;
import uk.org.toot.transport.*;
import javax.swing.KeyStroke;
import javax.swing.Action;
import javax.swing.JToolBar;
import java.awt.event.KeyEvent;
import java.awt.KeyEventDispatcher;
import uk.org.toot.audio.server.NonRealTimeAudioServer;

public class SingleTransportProjectPanel extends SingleProjectPanel 
{
    private Transport transport;
    private TransportActions transportActions;
    @SuppressWarnings("unused")
	private KeyEventDispatcher transportDispatcher;

    public SingleTransportProjectPanel(SingleTransportProject p, JToolBar toolBar) {
        super(p, toolBar);
        transport = p.getTransport();
        transportActions = new TransportActions(transport);
        NonRealTimeAudioServer nonRealTimeAudioServer = p.getNonRealTimeAudioServer();
        if ( nonRealTimeAudioServer != null ) {
	        Action realTimeAction = transportActions.getRealTimeAction(nonRealTimeAudioServer);
        	toolBar.addSeparator();
        	toolBar.add(new TransportToggleButton(realTimeAction));
        }
        TransportActions.addTransportTools(transportActions, toolBar);
//        transportDispatcher = new TransportKeyEventDispatcher();
    }

    protected void dispose() {
//        transport.stop();
		unhookKeys();
        transportActions.dispose();
        transportActions = null;
        super.dispose();
    }

    public void addNotify() {
        super.addNotify();
//        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(transportDispatcher);
        hookKeys();
    }

    public void removeNotify() {
        super.removeNotify();
//        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(transportDispatcher);
        unhookKeys();
    }

    protected void hookKeys() {
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, 0), "stop");
        getActionMap().put("stop", transportActions.getStopAction());
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "play");
        getActionMap().put("play", transportActions.getPlayAction());
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), "record");
        getActionMap().put("record", transportActions.getRecordAction());
    }

    protected void unhookKeys() {
        getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, 0));
        getActionMap().remove("stop");
        getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        getActionMap().remove("play");
        getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0));
        getActionMap().remove("record");
    }

    protected class TransportKeyEventDispatcher implements KeyEventDispatcher
    {
        public boolean dispatchKeyEvent(KeyEvent e) {
            if ( e.getID() == KeyEvent.KEY_TYPED ) {
                printTyped(e);
            } else if ( e.getID() == KeyEvent.KEY_PRESSED ) {
                println("Pressed", e);
            } else if ( e.getID() == KeyEvent.KEY_RELEASED ) {
                println("Released", e);
            }
            return false;
        }

        private void printTyped(KeyEvent e) {
            System.out.println("Typed "+
                ": char="+e.getKeyChar()+
                ", mod="+KeyEvent.getKeyModifiersText(e.getModifiers()));
        }

        private void println(String prefix, KeyEvent e) {
            System.out.println(prefix+
                ": text="+KeyEvent.getKeyText(e.getKeyCode())+
                ", code="+e.getKeyCode()+
                ", loc="+e.getKeyLocation()+
                ", mod="+KeyEvent.getKeyModifiersText(e.getModifiers()));
        }
    }
}
