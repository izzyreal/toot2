// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.server;

import java.util.List;
import uk.org.toot.audio.core.AudioBuffer;

/**
 * BasicAudioServer extends AbstractAudioServer by adding sampled data
 * buffer provision and management.
 */
abstract public class BasicAudioServer extends AbstractAudioServer
{
    private List<AudioBuffer> audioBuffers;

    private float sampleRate = 44100f;
    protected int channelCount;
    private int bufferFrames;

    public BasicAudioServer() {
        channelCount = 2; // !!! STEREO
        audioBuffers = new java.util.ArrayList<AudioBuffer>();
    }

    protected AudioBuffer _createAudioBuffer(String name) {
        bufferFrames = calculateBufferFrames();
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

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }
}
