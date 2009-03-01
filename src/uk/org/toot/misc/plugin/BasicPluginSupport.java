package uk.org.toot.misc.plugin;

import java.util.List;

/**
 * A basic implementation of PluginSupport that may be sufficient if an application
 * is prepared to explicitly invoke the compound methods that notify listeners.
 * 
 * Alternatively specialisations of this class may hook into the application in
 * a less coupled manner.
 * 
 * @author st
 *
 */
public class BasicPluginSupport implements PluginSupport, PluginTempoListener, PluginTransportListener
{
	private List<PluginTempoListener> tempoListeners = new java.util.ArrayList<PluginTempoListener>();
	private List<PluginTransportListener> transportListeners = new java.util.ArrayList<PluginTransportListener>();

	private float prevTempo = 120f;
	
	public void addTempoListener(PluginTempoListener listener) {
		if ( listener == null ) return;
		tempoListeners.add(listener);
		listener.tempoChanged(prevTempo);
	}

	public void removeTempoListener(PluginTempoListener listener) {
		if ( listener == null ) return;
		tempoListeners.remove(listener);
	}

	public void addTransportListener(PluginTransportListener listener) {
		if ( listener == null ) return;
		transportListeners.add(listener);
	}

	public void removeTransportListener(PluginTransportListener listener) {
		if ( listener == null ) return;
		transportListeners.remove(listener);
	}

	public void tempoChanged(float newTempo) {
		for ( PluginTempoListener l : tempoListeners ) {
			l.tempoChanged(newTempo);
		}
		prevTempo = newTempo;
	}

	public void play() {
		for ( PluginTransportListener l : transportListeners ) {
			l.play();
		}
	}

	public void stop() {
		for ( PluginTransportListener l : transportListeners ) {
			l.stop();
		}
	}

}
