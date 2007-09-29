/*
 * Copyright 1998-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package uk.org.toot.midi.sequencer;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.ControllerEventListener;



/**
 * EventDispatcher.  Used by various classes in the Java Sound implementation
 * to send events.
 *
 * @version 1.41 07/08/03
 * @author David Rivas
 * @author Kara Kytle
 * @author Florian Bomers
 */
class EventDispatcher implements Runnable {

	/**
	 * List of events
	 */
	private ArrayList<EventInfo> eventQueue = new ArrayList<EventInfo>();


	/**
	 * Thread object for this EventDispatcher instance
	 */
	private Thread thread = null;


	/**
	 * This start() method starts an event thread if one is not already active.
	 */
	synchronized void start() {

		if(thread == null) {
			thread = JSSecurityManager.createThread(this,
					"Java Sound Event Dispatcher",   // name
					true,  // daemon
					-1,    // priority
					true); // doStart
		}
	}


	/**
	 * Invoked when there is at least one event in the queue.
	 * Implement this as a callback to process one event.
	 */
	protected void processEvent(EventInfo eventInfo) {
		int count = eventInfo.getListenerCount();

		// process a MetaMessage
		if (eventInfo.getEvent() instanceof MetaMessage) {
			MetaMessage event = (MetaMessage)eventInfo.getEvent();
			for (int i = 0; i < count; i++) {
				try {
					((MetaEventListener) eventInfo.getListener(i)).meta(event);
				} catch (Throwable t) {
					if (Printer.err) t.printStackTrace();
				}
			}
			return;
		}

		// process a Controller or Mode Event
		if (eventInfo.getEvent() instanceof ShortMessage) {
			ShortMessage event = (ShortMessage)eventInfo.getEvent();
			int status = event.getStatus();

			// Controller and Mode events have status byte 0xBc, where
			// c is the channel they are sent on.
			if ((status & 0xF0) == 0xB0) {
				for (int i = 0; i < count; i++) {
					try {
						((ControllerEventListener) eventInfo.getListener(i)).controlChange(event);
					} catch (Throwable t) {
						if (Printer.err) t.printStackTrace();
					}
				}
			}
			return;
		}

		Printer.err("Unknown event type: " + eventInfo.getEvent());
	}


	/**
	 * Wait until there is something in the event queue to process.  Then
	 * dispatch the event to the listeners.The entire method does not
	 * need to be synchronized since this includes taking the event out
	 * from the queue and processing the event. We only need to provide
	 * exclusive access over the code where an event is removed from the
	 *queue.
	 */
	protected void dispatchEvents() {

		EventInfo eventInfo = null;

		synchronized (this) {

			// Wait till there is an event in the event queue.
			try {

				if (eventQueue.size() == 0) {
					wait();
				}
			} catch (InterruptedException e) {
			}
			if (eventQueue.size() > 0) {
				// Remove the event from the queue and dispatch it to the listeners.
				eventInfo = (EventInfo) eventQueue.remove(0);
			}

		} // end of synchronized
		if (eventInfo != null) {
			processEvent(eventInfo);
		}
	}


	/**
	 * Queue the given event in the event queue.
	 */
	private synchronized void postEvent(EventInfo eventInfo) {
		eventQueue.add(eventInfo);
		notifyAll();
	}


	/**
	 * A loop to dispatch events.
	 */
	public void run() {

		while (true) {
			try {
				dispatchEvents();
			} catch (Throwable t) {
				if (Printer.err) t.printStackTrace();
			}
		}
	}


	/**
	 * Send audio and MIDI events.
	 */
	void sendAudioEvents(Object event, List listeners) {
		if ((listeners == null)
				|| (listeners.size() == 0)) {
			// nothing to do
			return;
		}

		start();

		EventInfo eventInfo = new EventInfo(event, listeners);
		postEvent(eventInfo);
	}


	// /////////////////////////////////// INNER CLASSES ////////////////////////////////////////// //

	/**
	 * Container for an event and a set of listeners to deliver it to.
	 */
	private class EventInfo {

		private Object event;
		private Object[] listeners;

		/**
		 * Create a new instance of this event Info class
		 * @param event the event to be dispatched
		 * @param listeners listener list; will be copied
		 */
		EventInfo(Object event, List listeners) {
			this.event = event;
			this.listeners = listeners.toArray();
		}

		Object getEvent() {
			return event;
		}

		int getListenerCount() {
			return listeners.length;
		}

		Object getListener(int index) {
			return listeners[index];
		}

	} // class EventInfo


} // class EventDispatcher
