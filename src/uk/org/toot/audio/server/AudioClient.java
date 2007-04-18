// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.server;

public interface AudioClient
{
    void work(int nFrames);

    /**
     * When not enabled, work() may not be called and should be ignored if it
     * is called.
     */
    void setEnabled(boolean enabled);
}
