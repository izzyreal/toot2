package uk.org.toot.misc.plugin;

/**
 * A plugin wishing to receive tempo change notifications should implement this
 * interface and add (and remove) it using PluginSupport.
 * @author st
 *
 */
public interface PluginTempoListener
{
	void tempoChanged(float newTempo);
}
