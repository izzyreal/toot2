// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import java.awt.Container;
import java.io.IOException;
import java.util.Properties;
import uk.org.toot.control.*;
import uk.org.toot.swingui.miscui.SwingApplication;
import uk.org.toot.audio.mixer.*;
import uk.org.toot.audio.mixer.automation.*;
import uk.org.toot.audio.server.*;
import uk.org.toot.project.*;
import uk.org.toot.transport.*;
import java.io.File;
import uk.org.toot.audio.core.*;
import javax.swing.*;
import java.awt.Color;

/**
 * AbstractDemo creates a problem domain containing an automated mixer and
 * multi-track player with common transport which is extended by MixerDemo
 * and TransportProjectDemo to provide different user interfaces of the same
 * problem domain.
 */
abstract public class AbstractDemo
{
    protected Transport transport;
    protected SingleTransportProject project;
    protected Properties properties;

    protected AudioServer realServer;
    protected AudioServer server;

    /**
     * @link aggregationByValue
     * @supplierCardinality 1 
     */
    protected MultiTrackPlayer multiTrack;
    protected boolean hasMultiTrack = true;

    /**
     * @link aggregationByValue 
     * @supplierCardinality 1
     */
    protected MultiTrackControls multiTrackControls;
    protected MixerControls mixerControls;

    protected DemoSourceControls demoSourceControls;

    protected void waitForKeypress() {
        try {
            System.in.read();
		} catch ( IOException ioe ) {};
    }

    protected void frame(Container panel, String title) {
		SwingApplication.createFrame(panel, title);
    }

	public AbstractDemo(String[] args) {
        create(args);
    }

    protected String property(String key) {
        return properties.getProperty(key);
    }

    protected int intProperty(String key) {
        return Integer.parseInt(property(key));
    }

    protected int intProperty(String key, int def) {
        String prop = property(key);
        return prop == null ? def : Integer.parseInt(prop);
    }

    protected boolean booleanProperty(String key, boolean def) {
        String prop = property(key);
        return prop == null ? def : Boolean.parseBoolean(prop);
    }

    /*
     * Each creational property is obtained from one of several sources,
     * tried in the following order.
     * 1. java -D command line properties
     * 2. demo.properties file
     * 3. coded defaults
     *
     * i.e. the properties file overrides the defaults and the command line
	 * overrides everything.
     */
    protected void create(String[] args) {
        try {
            int nSources = 0;
            // create the shared transport
            transport = new DefaultTransport();
            // create the shared project 'manager'
            project = new SingleTransportProject(transport);
            // load the demo properties
            properties = new DemoProperties(project.getApplicationPath());
            // create the audio server
//            realServer = new JavaSoundAudioServer(format);
            realServer = AudioServerServices.createServer(property("server"));
            realServer.setSampleRate((float)intProperty("sample.rate", 44100));
            realServer.setSampleSizeInBits(intProperty("sample.bits", 16));
            // hook it for non-real-time
            server = new NonRealTimeAudioServer(realServer);
//            server = extendedServer;
            // hack the non real time audio server into the project 'manager'
            if ( server instanceof NonRealTimeAudioServer ) {
            	project.setNonRealTimeAudioServer((NonRealTimeAudioServer)server);
            }
            // set the projects root
            String projectsRoot = property("projects.root");
            if ( projectsRoot != null ) {
                project.setProjectsRoot(projectsRoot);
            }

            // create the multitrack player controls
            if ( hasMultiTrack ) {
	            int nTapeTracks = intProperty("tape.tracks", 24);
    	        nSources += nTapeTracks;
        	    multiTrackControls =
            	    new MultiTrackControls(nTapeTracks);
            	// create the multitrack player
				multiTrack = new ProjectMultiTrackPlayer(project, multiTrackControls);
            }

            // create the mixer controls
            int nMixerChans = intProperty("mixer.chans", 32);
            if ( nMixerChans < nSources ) nMixerChans = nSources; // for sanity
	        mixerControls = new MixerControls("Mixer");
//			MixerControlsFactory.createBusses(mixerControls,
//                intProperty("mixer.fx", 3), intProperty("mixer.aux", 1));
			mixerControls.createFxBusControls("FX#1", null);
			mixerControls.createFxBusControls("FX#2", null);
			mixerControls.createFxBusControls("FX#3", null);
			mixerControls.createAuxBusControls("Aux#1", ChannelFormat.MONO);
			mixerControls.createAuxBusControls("Aux#2", ChannelFormat.QUAD);
			MixerControlsFactory.createBusStrips(mixerControls, "L-R", ChannelFormat.STEREO,
                intProperty("mixer.returns", 2));
        	MixerControlsFactory.createGroupStrips(mixerControls,
                intProperty("mixer.groups", 2));
        	MixerControlsFactory.createChannelStrips(mixerControls, nMixerChans);
            // add snapshot automation of the mixer controls
			MixerControlsSnapshotAutomation snapshotAutomation =
                new SingleProjectMidiFileSnapshotAutomation(mixerControls, project);
            mixerControls.setSnapshotAutomation(snapshotAutomation);
            // add dynamic automation of the mixer controls
//			MixerControlsDynamicAutomation dynamicAutomation =
//                new TestMixerControlsMidiDynamicAutomation(mixerControls);
            // create the automated mixer
            AudioMixer mixer = new AudioMixer(mixerControls, server);
            connect(mixer);

            // add module persistence to ~/toot/presets/
            CompoundControl.setPersistence(
                new CompoundControlMidiPersistence(
                	new File(System.getProperty("user.home"),
                    	"toot"+File.separator+"presets")
                )
            );

            // the multitrack and the mizer are clients of the server
            CompoundAudioClient compoundAudioClient = new CompoundAudioClient();
			compoundAudioClient.add(multiTrack);
            compoundAudioClient.add(mixer);
            server.setClient(compoundAudioClient);

			createUI(args);
    	    try {
        	    Thread.sleep(1000);
	        } catch ( InterruptedException ie ) {
    	    }
            server.start();
        } catch ( Exception e ) {
            e.printStackTrace();
        	waitForKeypress();
        }
    }

    protected void connect(AudioMixer mixer) throws Exception {
            // connect an output to the main mixer bus
            AudioProcess output = server.openAudioOutput(property("main.output"), "Line Out");
            // hook it for WAV export although not currently possible to export
//            output = new TransportExportAudioProcessAdapter(output, format, "Mixer Main Bus", transport);
        	mixer.getMainBus().setOutputProcess(output);
			mixer.getBus("Aux#2").setOutputProcess(new NullAudioProcess());
            int s = 1;

            if ( hasMultiTrack ) {
    	        // connect multitrack outputs 1..n to mixer inputs 1..n
        	    for ( AudioProcess p : multiTrack.getProcesses() ) {
            	    mixer.getStrip(String.valueOf(s++)).setInputProcess(p);
	            }
            }

            // create a demo source connected to the next available strip
/*            String demoSourceStripName = String.valueOf(s++);
            demoSourceControls = new DemoSourceControls(mixerControls, demoSourceStripName, "A");
            DemoSourceProcess dsp = new DemoSourceProcess(demoSourceControls);
            mixer.getStrip(demoSourceStripName).setInputProcess(dsp); */

            // create an input connected to the next available strip
            String inputStripName = String.valueOf(s++);
            mixer.getStrip(inputStripName).setInputProcess(
                server.openAudioInput(property("main.input"), "Line In"));
	}

    protected void createUI(String[] args) {
        UIManager.put("ToolTip.background", new Color(255, 255, 225));
    };
}
