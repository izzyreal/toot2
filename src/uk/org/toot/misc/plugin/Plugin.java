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
