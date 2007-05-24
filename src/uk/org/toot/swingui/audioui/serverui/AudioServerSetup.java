// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.audioui.serverui;

import javax.swing.*;
import java.util.Properties;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AudioServerSetup extends JPanel
{
    /**
     * @link aggregationByValue
     * @supplierCardinality 1 
     */
    private AudioServerCombo serverCombo;

    /**
     * @link aggregationByValue
     * @supplierCardinality 1 
     */
    private SampleRateCombo sampleRateCombo;

    private Properties properties;
    
    public AudioServerSetup(final Properties properties) {
    	this.properties = properties;
    	// server
        JLabel serverLabel = new JLabel("Audio Server");
        add(serverLabel);
        String serverName = properties.getProperty("server");
        add(serverCombo = new AudioServerCombo(serverName));
        serverLabel.setLabelFor(serverCombo);
        serverCombo.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		// needs to update server-specific UI
        	}
        });
        
        // sample rate
 		JLabel sampleRateLabel = new JLabel("Sample Rate (Hz)");
        add(sampleRateLabel);
        String sampleRate = properties.getProperty("sample.rate");
        add(sampleRateCombo = new SampleRateCombo(sampleRate));
        sampleRateLabel.setLabelFor(sampleRateCombo);
    }
    
    public void store() {
		properties.put("server", serverCombo.getSelectedItem());
		properties.put("sample.rate", sampleRateCombo.getSelectedItem());    	
    }
}
