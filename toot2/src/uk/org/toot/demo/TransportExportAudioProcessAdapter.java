/* Copyright Steve Taylor 2006 */

package uk.org.toot.demo;

import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.transport.Transport;
import javax.sound.sampled.AudioFormat;

/**
 * This class implements ExportAudioProcessAdaptor for the Toot transport framework
 */
public class TransportExportAudioProcessAdapter extends ExportAudioProcessAdapter
{
    private Transport transport;

    public TransportExportAudioProcessAdapter(AudioProcess process,
        	AudioFormat format, String name,Transport transport) {
        super(process, format, name);
        this.transport = transport;
    }
}
