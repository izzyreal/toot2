/* Copyright Steve Taylor 2006 */

package uk.org.toot.audio.server;

import java.util.List;

public interface ExtendedAudioServer extends AudioServer
{
    float getPeakLoad();

    int getBufferUnderRuns();

    float getMaximumJitterMilliseconds();

    float getLowestLatencyMilliseconds();

    float getActualLatencyMilliseconds();

    void setLatencyMilliseconds(float latencyMilliseconds);

    float getMinimumLatencyMilliseconds();

    float getBufferMilliseconds();

    void setBufferMilliseconds(float bufferMilliseconds);

    float getLatencyMilliseconds();

    void resetMetrics();

    int getInputLatencyFrames();

    int getOutputLatencyFrames();

    // these two are candidates for promoting to AudioServer
    List<AudioLine> getOutputs();

    List<AudioLine> getInputs();
}
