/* Copyright Steve Taylor 2006 */

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
