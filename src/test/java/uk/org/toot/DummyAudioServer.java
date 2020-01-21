package uk.org.toot;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.server.AudioClient;
import uk.org.toot.audio.server.AudioServer;
import uk.org.toot.audio.server.IOAudioProcess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DummyAudioServer implements AudioServer {

    private boolean running = false;
    private AudioClient client;

    private List<AudioBuffer> buffers = new ArrayList<>();

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void close() {
        stop();
    }

    @Override
    public void setClient(AudioClient client) {
        this.client = client;
    }

    @Override
    public List<String> getAvailableOutputNames() {
        System.out.println("getAvailableOutputNames");
        return Arrays.asList("Stereo Output 1", "Stereo Output 2");
    }

    @Override
    public List<String> getAvailableInputNames() {
        return Collections.emptyList();
    }

    @Override
    public IOAudioProcess openAudioOutput(String name, String label) {
        System.out.println("Opening audio output " + name);
        return new DummyAudioOutputProcess(name);
    }

    @Override
    public IOAudioProcess openAudioInput(String name, String label) {
        return null;
    }

    @Override
    public void closeAudioOutput(IOAudioProcess output) {

    }

    @Override
    public void closeAudioInput(IOAudioProcess input) {

    }

    @Override
    public AudioBuffer createAudioBuffer(String name) {
        System.out.println("Creating audio buffer with name " + name);
        AudioBuffer buf = new AudioBuffer(name, 2, 512, getSampleRate());
        buffers.add(buf);
        return buf;
    }

    @Override
    public void removeAudioBuffer(AudioBuffer buffer) {
        buffers.remove(buffer);
    }

    @Override
    public float getSampleRate() {
        return 44100;
    }

    @Override
    public float getLoad() {
        return 0;
    }

    @Override
    public int getInputLatencyFrames() {
        return 0;
    }

    @Override
    public int getOutputLatencyFrames() {
        return 0;
    }

    @Override
    public int getTotalLatencyFrames() {
        return 0;
    }

    public List<AudioBuffer> getBuffers() {
        return buffers;
    }
}
