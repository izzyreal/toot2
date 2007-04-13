/* Copyright Steve Taylor 2006 */

package uk.org.toot.audio.server;

import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;

public interface IOAudioProcess extends AudioProcess
{
    ChannelFormat getChannelFormat();

    String getName();
}
