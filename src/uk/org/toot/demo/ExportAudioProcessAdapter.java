// Copyright (C) 2007 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import uk.org.toot.audio.core.AudioProcessAdapter;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioBuffer;
import org.tritonus.share.TCircularBuffer;
import javax.sound.sampled.AudioFormat;
import java.io.File;

/**
 * State transitions in an export cycle:
 * start();
 * reading = true;
 * writing = true;
 * stop();
 * reading = false;
 * writing = false;
 *
 * export is active when reading || writing
 * Writing lags reading because it represents the output end of
 * the circular buffer writing to file.
 */
abstract public class ExportAudioProcessAdapter extends AudioProcessAdapter
    implements Runnable
{
    protected String name;
    protected TCircularBuffer circularBuffer;
    protected AudioFormat format;
    protected boolean reading = false; // reading the buffer
    protected boolean writing = false; // writing the file
    protected File file;
    private Thread writeThread;

    public ExportAudioProcessAdapter(AudioProcess process, AudioFormat format, String name) {
        super(process);
        this.format = format;
        this.name = name;
        circularBuffer = new TCircularBuffer(100000, true, true, null);
    }

    public int processAudio(AudioBuffer buf) {
        int ret = super.processAudio(buf);
        if ( reading ) {
            // enforce required format
//    		int nbytes = buf.convertToByteArray(byteBuffer, 0, format);
//			circularBuffer.write(byteBuffer, 0, nbytes);
        }
        return ret;
    }

    public void setFile(File file) {
        if ( reading || writing ) {
            throw new IllegalStateException("Can't setFile() when already exporting");
        }
        this.file = file;
    }

    public void start() {
        if ( file == null ) {
            throw new IllegalStateException("null export file");
        }
        if ( reading || writing ) return; // already started
        reading = true;
        // start file write thread which opens file, writes, then closes file
        writeThread = new Thread(this, name+" Export");
        writeThread.start();
    }

    public void stop() {
        if ( !reading ) return; // already stopped
        reading = false;
    }

    public void run() {
        writing = true;
        // open file with format
        while ( reading || circularBuffer.availableRead() > 0 ) {
            // write file
        }
        // close file
        writing = false;
    }
}
