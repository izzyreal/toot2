// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.project;

import uk.org.toot.transport.Transport;
import java.io.File;

import uk.org.toot.audio.server.NonRealTimeAudioServer;

/**
 * Single Project with Transport
 **/
public class SingleTransportProject extends SingleProject
{
    private Transport transport;
    private NonRealTimeAudioServer nonRealTimeAudioServer = null;

    public SingleTransportProject(Transport transport) {
        super();
        this.transport = transport;
    }

    public SingleTransportProject(Transport transport, String appDir) {
        super(appDir);
        this.transport = transport;
    }

    public SingleTransportProject(Transport transport, File appPath) {
        super(appPath);
        this.transport = transport;
    }

    public void openProject(String name) {
        if ( transport.isPlaying() ) transport.stop();
        super.openProject(name);
    }

/*    public boolean canOpenProject() {
        return super.canOpenProject() && !transport.isPlaying(); // !!!
    }

    public boolean canSaveProject() {
        return super.canSaveProject() && !transport.isPlaying(); // !!!
    } */

    public Transport getTransport() {
        return transport;
    }

    public void setNonRealTimeAudioServer(NonRealTimeAudioServer server) {
        nonRealTimeAudioServer = server;
    }

    public NonRealTimeAudioServer getNonRealTimeAudioServer() {
        return nonRealTimeAudioServer;
    }
}
