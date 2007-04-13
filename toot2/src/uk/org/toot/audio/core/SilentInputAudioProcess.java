/* Copyright Steve Taylor 2006 */

package uk.org.toot.audio.core;

public class SilentInputAudioProcess extends NullAudioProcess {
    /**
     * @link aggregation
     * @supplierCardinality 1 
     */
    private ChannelFormat channelFormat;
    private AudioBuffer.MetaInfo metaInfo;

    public SilentInputAudioProcess(ChannelFormat format, String label) {
        channelFormat = format;
        metaInfo = new AudioBuffer.MetaInfo(label);
    }

    public int processAudio(AudioBuffer buffer) {
        buffer.setMetaInfo(metaInfo);
        buffer.setChannelFormat(channelFormat);
        buffer.makeSilence();
    	return AUDIO_OK;
    }

    public ChannelFormat getChannelFormat() { return channelFormat; }

    public String getName() { return "Silence"; }
}
