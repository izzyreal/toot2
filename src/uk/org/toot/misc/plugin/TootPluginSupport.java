// Copyright (C) 2009 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.misc.plugin;

import uk.org.toot.misc.Tempo;
import uk.org.toot.misc.TempoListener;
import uk.org.toot.misc.TimeSignature;
import uk.org.toot.misc.TimeSignatureListener;
import uk.org.toot.transport.Transport;
import uk.org.toot.transport.TransportListener;

/**
 * This class is a specialisation that ties in to the Toot Transport, TimeSignature and 
 * Tempo models.
 * @author st
 *
 */
public class TootPluginSupport extends BasicPluginSupport
{
	public TootPluginSupport(Transport transport) {
		transport.addTransportListener(
			new TransportListener() {
				public void locate(long microseconds) {
				}
				public void play() {
					TootPluginSupport.this.play();
				}
				public void record(boolean rec) {
				}
				public void stop() {
					TootPluginSupport.this.stop();
				}				
			}
		);
		
		Tempo.addTempoListener(
			new TempoListener() {
				public void tempoChanged(float newTempo) {
					TootPluginSupport.this.tempoChanged(newTempo);
				}				
			}
		);

		TimeSignature.addTimeSignatureListener(
				new TimeSignatureListener() {
					public void timeSignatureChanged(int numerator, int denominator) {
						TootPluginSupport.this.timeSignatureChanged(numerator, denominator);
					}				
				}
			);
	}
}
