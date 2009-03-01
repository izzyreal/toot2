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
