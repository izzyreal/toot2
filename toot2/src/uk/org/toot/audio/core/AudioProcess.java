// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.core;

/**
 * The simple contract for an AudioProcess, the fundamental unit of digital
 * signal processing, separate from the concern of control.
 * 'Inputs' should fill the buffer,
 * 'Processes' should process the buffer,
 * 'Outputs' should output the buffer,
 * Something else provides the buffer.
 *
 * There is intentionally no support for routing, that's a separate concern.
 */
public interface AudioProcess
{
    /**
     * Returned from processAudio() to indicate a valid processed buffer.
     */
    final static int AUDIO_OK = 0;

    /**
     * Returned from processAudio() to indicate the buffer has not been
     * processed so that subsequent processing may be avoided.
     */
    final static int AUDIO_DISCONNECT = 1;

    /**
     * Open any resources required by this AudioProcess.
     */
    void open();

    /**
     * Process the supplied buffer
     * @param buffer the AudioBuffer to process.
     * @return int AUDIO_OK or AUDIO_DISCONNECT.
     */
    int processAudio(AudioBuffer buffer);

    /**
     * Close any resources opened by this AudioProcess.
     */
    void close();
}
