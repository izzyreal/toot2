// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.misc.plugin;

/**
 * A class which provides static methods for host applications to set an implementation
 * of PluginSupport and for plugins to obtain that implementation.
 * 
 * @author st
 *
 */
public class Plugin
{
	private static PluginSupport pluginSupport;
	
	public static void setPluginSupport(PluginSupport support) {
		pluginSupport = support;
	}
	
	public static PluginSupport getPluginSupport() {
		return pluginSupport;
	}
}
