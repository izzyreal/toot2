// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.audioui.serverui;

import java.awt.*;
import java.awt.event.*;
import java.util.Properties;

import javax.swing.*;
 
public class AudioServerChooser extends JDialog implements ActionListener {
 
    private JButton     okButton;
    private JButton     cancelButton;

    /**
     * @supplierCardinality 1
     * @link aggregation 
     */
    private AudioServerSetup setupPanel;

    public AudioServerChooser(AudioServerSetup setupPanel) {
    	this.setupPanel = setupPanel;
        setTitle("Audio Server Setup");
        setModal(true);
        
        getContentPane().add(setupPanel, BorderLayout.CENTER);
        
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
 
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == okButton ) {
        	setupPanel.store();
        }
        dispose();
    }

    public static void showDialog(final Properties properties) {
    	try {
    		SwingUtilities.invokeAndWait(new Runnable() {
    			public void run() {
    	    		new AudioServerChooser(new AudioServerSetup(properties));    		
    			}
    		});
    	} catch ( Exception e ) {
    		// empty 
    	}
    }

}