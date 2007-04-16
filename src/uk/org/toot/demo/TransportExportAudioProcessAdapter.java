// Copyright (C) 2007 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.transport.Transport;
import javax.sound.sampled.AudioFormat;

/**
 * This class implements ExportAudioProcessAdaptor for the Toot transport framework
 */
public class TransportExportAudioProcessAdapter extends ExportAudioProcessAdapter
{
    @SuppressWarnings("unused")
	private Transport transport;

    public TransportExportAudioProcessAdapter(AudioProcess process,
        	AudioFormat format, String name,Transport transport) {
        super(process, format, name);
        this.transport = transport;
    }
}
