package uk.org.toot.misc.plugin;

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
	void addTempoListener(PluginTempoListener listener);
	void removeTempoListener(PluginTempoListener listener);
	
	void addTransportListener(PluginTransportListener listener);
	void removeTransportListener(PluginTransportListener listener);
}
