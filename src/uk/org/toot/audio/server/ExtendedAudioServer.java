// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

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
