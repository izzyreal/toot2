// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.misc.plugin;

/**
 * A plugin wishing to receive transport notifications should provide an implementation
 * of this interface and add (and remove) it using PluginSupport.
 * @author st
 *
 */
public interface PluginTransportListener
{
	void play();
	void stop();
}
