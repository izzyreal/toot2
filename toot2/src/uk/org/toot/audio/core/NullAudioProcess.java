// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.core;

/**
 * An AudioProcess that does nothing.
 * Useful for testing.
 */
public class NullAudioProcess implements AudioProcess
{
    public void open() {}

    public int processAudio(AudioBuffer buffer) {
    	return AUDIO_DISCONNECT;
    }

    public void close() {}
}
