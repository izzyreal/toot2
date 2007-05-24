// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.spi;

import uk.org.toot.control.spi.ControlServiceDescriptor;
import uk.org.toot.audio.core.ChannelFormat;

public class AudioControlServiceDescriptor extends ControlServiceDescriptor
{
    private ChannelFormat channelFormat = null;

    public AudioControlServiceDescriptor(Class<?> clazz, int moduleId,
        	String name, String description, String version) {
        super(clazz, moduleId, name, description, version);
    }

    public AudioControlServiceDescriptor(Class<?> clazz, int moduleId,
        String name, String description, String version, ChannelFormat format) {
        this(clazz, moduleId, name, description, version);
        channelFormat = format;
    }

    public ChannelFormat getChannelFormat() {
        return channelFormat;
    }
}
