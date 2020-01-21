package uk.org.toot;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.server.IOAudioProcess;

public class DummyAudioOutputProcess implements IOAudioProcess {

    private final String name;

    DummyAudioOutputProcess(String name) {
        this.name = name;
    }

    @Override
    public ChannelFormat getChannelFormat() {
        return ChannelFormat.STEREO;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void open() {

    }

    @Override
    public int processAudio(AudioBuffer buffer) {
        return AUDIO_OK;
    }

    @Override
    public void close() {

    }
}
