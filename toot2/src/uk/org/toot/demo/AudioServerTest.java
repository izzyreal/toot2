// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import javax.swing.JPanel;
import uk.org.toot.audio.core.AudioBuffer;
import javax.sound.sampled.AudioFormat;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.server.*;
import uk.org.toot.swingui.projectui.*;
import uk.org.toot.swingui.audioui.mixerui.*;
import uk.org.toot.swingui.audioui.serverui.AudioServerPanel;

/**
 * TransportProjectDemo displays a full user interface over the underlying
 * demonstration problem domain model, with the multi-track player and automated
 * mixer in separate tabs.
 */
public class AudioServerTest extends AbstractDemo 
{
    public AudioServerTest(String[] args) {
        super(args);
    }

    protected void createUI(String[] args) {
        super.createUI(args);
        frame(new AudioServerPanel(extendedServer), "Audio Server Test");
    }

    public static void main(String[] args) {
        new AudioServerTest(args);
    }

    protected void create(String[] args) {
        try {
            // 48kHz 16 bit stereo for my SB Live
            AudioFormat format = new AudioFormat(48000, 16, 2, true, false);
            server = new JavaSoundAudioServer(format);
            server.setClient(new TestClient(server));
			createUI(args);
    	    try {
        	    Thread.currentThread().sleep(2000);
	        } catch ( InterruptedException ie ) {
    	    }
            server.start();
        } catch ( Exception e ) {
            e.printStackTrace();
        	waitForKeypress();
        }
    }

    protected class TestClient implements AudioClient
    {
        private AudioBuffer buffer;
        private AudioProcess output;

        public TestClient(AudioServer server) {
            buffer = server.createAudioBuffer("AudioServerTest");
            buffer.makeSilence();
            try {
				output = server.openAudioOutput("1/2", "Line Out");
            } catch ( Exception e ) {
                System.out.println("Failed to create audio output");
            }
        }

        public void work(int nFrames) {
            output.processAudio(buffer);
        }
    }
}
