// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.misc.plugin;

import uk.org.toot.misc.TimeSignatureListener;
import uk.org.toot.misc.TempoListener;

/**
 * An interface that specifies the support required by plugins to receive
 * notifications concerning tempo changes and transport state changes.
 * 
 * A plugin can obtain the appropriate implementation of PluginSupport by means of
 * Plugin.getPluginSupport().
 * 
 * A host application will previously have set the appropriate implementation by means
 * of Plugin.setPluginSupport(PluginSupport support)
 * 
 * @author st
 *
 */
public interface PluginSupport
{
	void addTempoListener(TempoListener listener);
	void removeTempoListener(TempoListener listener);
	
	void addTimeSignatureListener(TimeSignatureListener listener);
	void removeTimeSignatureListener(TimeSignatureListener listener);

	void addTransportListener(PluginTransportListener listener);
	void removeTransportListener(PluginTransportListener listener);
}