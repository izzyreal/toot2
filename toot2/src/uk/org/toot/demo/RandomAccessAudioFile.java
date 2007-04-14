// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;

public class RandomAccessAudioFile extends RandomAccessFile
{
    @SuppressWarnings("unused")
	private long offset; // byte offset of audio data (after any header)
    @SuppressWarnings("unused")
	private long startFrame = 0; // frame that represents zero microseconds

    public RandomAccessAudioFile(File file, String mode)
    	throws FileNotFoundException {
        super(file, mode);
    }

    public RandomAccessAudioFile(String name, String mode)
    	throws FileNotFoundException {
        super(name, mode);
    }

    public void locate(long microseconds) {
        // long frame = startFrame + microseconds / ???;
        // seek(offset + frame * frameSize);
    }
}
