// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.core;

abstract public class AudioProcessAdapter implements AudioProcess
{
    /**
     * @link aggregation
     * @supplierCardinality 1 
     */
    private AudioProcess process;

    public AudioProcessAdapter(AudioProcess process) {
        if ( process == null ) {
            throw new IllegalArgumentException("null AudioProcess");
        }
        this.process = process;
    }

    public void open() {
        process.open();
    }

    public int processAudio(AudioBuffer buf) {
        return process.processAudio(buf);
    }

    public void close() {
        process.close();
    }
}
