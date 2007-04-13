// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.server;

import java.util.List;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioBuffer;

/**
 * BasicAudioServer extends AbstractAudioServer by adding sampled data
 * buffer provision and management.
 */
abstract public class BasicAudioServer extends AbstractAudioServer
{
    private List<AudioBuffer> audioBuffers;

    private float sampleRate;
    private int channelCount;
    private int bufferFrames;

    public BasicAudioServer(float sampleRate, int channels) {
        this.sampleRate = sampleRate;
        channelCount = channels;
        audioBuffers = new java.util.ArrayList<AudioBuffer>();
        bufferFrames = calculateBufferFrames();
    }

    protected AudioBuffer _createAudioBuffer(String name) {
        return new AudioBuffer(name, channelCount, bufferFrames, sampleRate);
    }

    public AudioBuffer createAudioBuffer(String name) {
        AudioBuffer buffer = _createAudioBuffer(name);
        audioBuffers.add(buffer);
        return buffer;
    }

    public void removeAudioBuffer(AudioBuffer buffer)  {
        audioBuffers.remove(buffer);
    }

    protected int calculateBufferFrames() {
        return (int)(sampleRate * getBufferMilliseconds() / 1000);
    }

    protected void resizeBuffers() {
        bufferFrames = calculateBufferFrames();
        for ( AudioBuffer buffer : audioBuffers ) {
            buffer.changeSampleCount(bufferFrames, false); // don't keep old
        }
    }

    protected void work() {
        client.work(bufferFrames);
    }
}
