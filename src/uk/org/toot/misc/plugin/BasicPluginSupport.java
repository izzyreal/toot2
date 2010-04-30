// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.misc.plugin;

import java.util.List;

import uk.org.toot.misc.TimeSignature;
import uk.org.toot.misc.Tempo;

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
public class BasicPluginSupport implements 
	PluginSupport, Tempo.Listener, TimeSignature.Listener, PluginTransportListener
{
	private List<Tempo.Listener> tempoListeners = new java.util.ArrayList<Tempo.Listener>();
	private List<TimeSignature.Listener> timeSignatureListeners = new java.util.ArrayList<TimeSignature.Listener>();
	private List<PluginTransportListener> transportListeners = new java.util.ArrayList<PluginTransportListener>();

	private float prevTempo = 120f;
	private int prevNumerator = 4;
	private int prevDenominator = 4;
	
	public void addTempoListener(Tempo.Listener listener) {
		if ( listener == null ) return;
		tempoListeners.add(listener);
		listener.tempoChanged(prevTempo);
	}

	public void removeTempoListener(Tempo.Listener listener) {
		if ( listener == null ) return;
		tempoListeners.remove(listener);
	}

	public void addTimeSignatureListener(TimeSignature.Listener listener) {
		if ( listener == null ) return;
		timeSignatureListeners.add(listener);
		listener.timeSignatureChanged(prevNumerator, prevDenominator);
		
	}

	public void removeTimeSignatureListener(TimeSignature.Listener listener) {
		// TODO Auto-generated method stub
		if ( listener == null ) return;
		timeSignatureListeners.remove(listener);		
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
		for ( Tempo.Listener l : tempoListeners ) {
			l.tempoChanged(newTempo);
		}
		prevTempo = newTempo;
	}

	public void timeSignatureChanged(int numerator, int denominator) {
		for ( TimeSignature.Listener l : timeSignatureListeners ) {
			l.timeSignatureChanged(numerator, denominator);
		}
		prevNumerator = numerator;
		prevDenominator = denominator;
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
