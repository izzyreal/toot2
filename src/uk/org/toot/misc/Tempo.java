package uk.org.toot.misc;

import java.util.List;

/**
 * This class knows the tempo.
 * It is needed in case there isn't something like a sequencer that has tempo
 * so that the user can set the tempo.
 * If there is something like a sequencer that has tempo it should set its
 * tempo using this class.
 * @author st
 *
 */
public class Tempo
{
	private static List<TempoListener> listeners = new java.util.ArrayList<TempoListener>();
	private static float tempo = 123f;
	
	private Tempo() {} // prevent instantiation
	
	public static float getTempo() {
		return tempo;
	}
	
	public static void setTempo(float newTempo) {
		tempo = newTempo;
		for ( TempoListener l : listeners ) {
			l.tempoChanged(newTempo);
		}
	}
	
	public static void addTempoListener(TempoListener listener) {
		if ( listener == null ) return;
		listeners.add(listener);
		listener.tempoChanged(tempo);
	}
	
	public static void removeTempoListener(TempoListener listener) {
		if ( listener == null ) return;		
		listeners.remove(listener);
	}
}
