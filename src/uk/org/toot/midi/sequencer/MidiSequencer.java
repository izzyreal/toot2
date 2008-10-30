/*
 * Copyright 2003-2007 Sun Microsystems, Inc.  All Rights Reserved.
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
/*
 * Copyright 2007 Steve Taylor
 */

package uk.org.toot.midi.sequencer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.sound.midi.*;

import uk.org.toot.midi.core.*;
import uk.org.toot.transport.TransportListener;
import uk.org.toot.midi.sequence.MidiSequence;
import uk.org.toot.midi.sequence.MidiTrack;


/**
 * A Real Time MIDI Sequencer
 *
 * derived from version 1.23, 07/08/03
 * @author Florian Bomers
 * @author Steve Taylor, Toot Software
 */

/* TODO:
 * DONE - test playback
 * - test recording
 * DONE - mute/solo to SequencerTrack
 * - recording?
 * 		how many inputs?
 * 		recordEnable()/recordDisable() to SequencerTrack
 * - generalise DefaultSequencerTrack.pump()
 * DONE - expose SequencerTracks
 * - add/remove SequencerTracks?
 */
public class MidiSequencer extends AbstractMidiDevice implements TransportListener
{
	// STATIC VARIABLES

	/** debugging flags */
	private final static boolean DEBUG_PUMP = false;
	private final static boolean DEBUG_PUMP_ALL = false;


	/**
	 * Event Dispatcher thread. Should be using a shared event
	 * dispatcher instance with a factory in EventDispatcher
	 */
	private static final EventDispatcher eventDispatcher;

	private static Sequencer.SyncMode[] masterSyncModes	= { Sequencer.SyncMode.INTERNAL_CLOCK };
	private static Sequencer.SyncMode[] slaveSyncModes	= { Sequencer.SyncMode.NO_SYNC };

	private static Sequencer.SyncMode masterSyncMode	= Sequencer.SyncMode.INTERNAL_CLOCK;
	private static Sequencer.SyncMode slaveSyncMode	= Sequencer.SyncMode.NO_SYNC;


	/**
	 * Sequence on which this sequencer is operating.
	 */
	private MidiSequence sequence = null;

	// caches

	/**
	 * Same for setTempoInMPQ...
	 * -1 means not set.
	 */
	private double cacheTempoMPQ = -1;


	/**
	 * cache value for tempo factor until sequence is set
	 * -1 means not set.
	 */
	private float cacheTempoFactor = -1;


	/** tempo cache for getMicrosecondPosition */
	private MidiUtils.TempoCache tempoCache = new MidiUtils.TempoCache();

	/**
	 * True if the sequence is running.
	 */
	private boolean running = false;


	/** the play engine for pushing out the MIDI messages */
	private PlayEngine playEngine;


	/**
	 * True if we are recording
	 */
	private boolean recording = false;


	static {
		// create and start the global event thread
		eventDispatcher = new EventDispatcher();
		eventDispatcher.start();
	}


	public MidiSequencer() {
		super("Sequencer");
	}

	/* ****************************** SEQUENCER METHODS ******************** */

	public synchronized void setMidiSequence(MidiSequence sequence)
	throws InvalidMidiDataException {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: setSequence(" + sequence +")");

		if (sequence != this.sequence) {
			if (this.sequence != null && sequence == null) {
				setCaches();
				stop();
				// initialize some non-cached values
				if (getDataPump() != null) {
					getDataPump().setTickPos(0);
				}
			}

			if (playEngine != null) {
				playEngine.setSequence(sequence);
			}

			// store this sequence (do not copy - we want to give the possibility
			// of modifying the sequence at runtime)
			this.sequence = sequence;

			if (sequence != null) {
				tempoCache.refresh(sequence);
				// rewind to the beginning
				setTickPosition(0);
				// propagate caches
				propagateCaches();
			}
		}
		else if (sequence != null) {
			tempoCache.refresh(sequence);
			if (playEngine != null) {
				playEngine.setSequence(sequence);
			}
		}

		if (Printer.trace) Printer.trace("<< RealTimeSequencer: setSequence(" + sequence +") completed");
	}


/*	public synchronized void setSequence(InputStream stream) throws IOException, InvalidMidiDataException {

		if (Printer.trace) Printer.trace(">> RealTimeSequencer: setSequence(" + stream +")");

		if (stream == null) {
			setSequence((Sequence) null);
			return;
		}

		Sequence seq = javax.sound.midi.MidiSystem.getSequence(stream); // can throw IOException, InvalidMidiDataException

		setSequence(seq);

		if (Printer.trace) Printer.trace("<< RealTimeSequencer: setSequence(" + stream +") completed");

	} */


	public MidiSequence getMidiSequence() {
		return sequence;
	}


	public synchronized void play() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: start()");

		// sequencer not open: throw an exception
		if (!isOpen()) {
			throw new IllegalStateException("sequencer not open");
		}

		// sequence not available
		if (sequence == null) {
			return;
		}

		// already running: return quietly
		if (running == true) {
			return;
		}

		// start playback
		implPlay();

		if (Printer.trace) Printer.trace("<< RealTimeSequencer: start() completed");
	}


	public synchronized void stop() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: stop()");

		if (!isOpen()) {
			throw new IllegalStateException("sequencer not open");
		}
		stopRecording();

		// not running; just return
		if (running == false) {
			if (Printer.trace) Printer.trace("<< RealTimeSequencer: stop() not running!");
			return;
		}

		// stop playback
		implStop();

		if (Printer.trace) Printer.trace("<< RealTimeSequencer: stop() completed");
	}


	public boolean isOpen() {
		return true; // TODO
	}

	public boolean isRunning() {
		return running;
	}

	// for TransportListener implementation
	public void record(boolean rec) {
		if ( rec ) 
			startRecording(); 
		else 
			stopRecording();
	}
	
	public void startRecording() {
		if (!isOpen()) {
			throw new IllegalStateException("Sequencer not open");
		}

		play();
		recording = true;
	}


	public void stopRecording() {
		if (!isOpen()) {
			throw new IllegalStateException("Sequencer not open");
		}
		recording = false;
	}


	public boolean isRecording() {
		return recording;
	}


	public float getTempoInBPM() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: getTempoInBPM() ");

		return (float) MidiUtils.convertTempo(getTempoInMPQ());
	}


	public void setTempoInBPM(float bpm) {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: setTempoInBPM() ");
		if ( bpm <= 0 || bpm > 242 ) {
			throw new IllegalArgumentException("bpm must be between 1 and 242");
		}

		setTempoInMPQ((float) MidiUtils.convertTempo((double) bpm));
	}


	public float getTempoInMPQ() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: getTempoInMPQ() ");

		if (needCaching()) {
			// if the sequencer is closed, return cached value
			if (cacheTempoMPQ != -1) {
				return (float) cacheTempoMPQ;
			}
			// if sequence is set, return current tempo
			if (sequence != null) {
				return tempoCache.getTempoMPQAt(getTickPosition());
			}

			// last resort: return a standard tempo: 120bpm
			return (float) MidiUtils.DEFAULT_TEMPO_MPQ;
		}
		return (float)getDataPump().getTempoMPQ();
	}


	public void setTempoInMPQ(float mpq) {
		if (mpq <= 0) {
			throw new IllegalArgumentException("mpq must be > 0");
		}

		if (Printer.trace) Printer.trace(">> RealTimeSequencer: setTempoInMPQ() ");

		if (needCaching()) {
			// cache the value
			cacheTempoMPQ = mpq;
		} else {
			// set the native tempo in MPQ
			getDataPump().setTempoMPQ(mpq);

			// reset the tempoInBPM and tempoInMPQ values so we won't use them again
			cacheTempoMPQ = -1;
		}
	}


	public void setTempoFactor(float factor) {
		if ( factor <= 0.1f || factor > 10 ) {
			throw new IllegalArgumentException("tempo factor must be between 0.1 and 10");
		}

		if (Printer.trace) Printer.trace(">> RealTimeSequencer: setTempoFactor() ");

		if (needCaching()) {
			cacheTempoFactor = factor;
		} else {
			getDataPump().setTempoFactor(factor);
			// don't need cache anymore
			cacheTempoFactor = -1;
		}
	}


	public float getTempoFactor() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: getTempoFactor() ");

		if (needCaching()) {
			if (cacheTempoFactor != -1) {
				return cacheTempoFactor;
			}
			return 1.0f;
		}
		return getDataPump().getTempoFactor();
	}


	public long getTickLength() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: getTickLength() ");

		if (sequence == null) {
			return 0;
		}

		return sequence.getTickLength();
	}


	public synchronized long getTickPosition() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: getTickPosition() ");

		if (getDataPump() == null || sequence == null) {
			return 0;
		}

		return getDataPump().getTickPos();
	}


	public synchronized void setTickPosition(long tick) {
		if (tick < 0) {
			throw new IllegalArgumentException("tick position must be > 0");
		}

		if (Printer.trace) Printer.trace(">> RealTimeSequencer: setTickPosition("+tick+") ");

		if (getDataPump() == null) {
			if (tick != 0) {
				throw new IllegalStateException("cannot set non-zero position in closed state");
			}
		}
		else if (sequence == null) {
			if (tick != 0) {
				throw new IllegalStateException("cannot set non-zero position if sequence is not set");
			}
		} else {
			getDataPump().setTickPos(tick);
		}
	}


	public long getMicrosecondLength() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: getMicrosecondLength() ");

		if (sequence == null) {
			return 0;
		}

		return sequence.getMicrosecondLength();
	}


	public long getMicrosecondPosition() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: getMicrosecondPosition() ");

		if (getDataPump() == null || sequence == null) {
			return 0;
		}
		synchronized (tempoCache) {
			return MidiUtils.tick2microsecond(sequence, getDataPump().getTickPos(), tempoCache);
		}
	}

	// for TransportListener implementation
	public void locate(long microseconds) {
		setMicrosecondPosition(microseconds);
	}
	
	public void setMicrosecondPosition(long microseconds) {
		if (microseconds < 0) {
			throw new IllegalArgumentException("microsecond position must be > 0");
		}

		if (Printer.trace) Printer.trace(">> RealTimeSequencer: setMicrosecondPosition("+microseconds+") ");

		if (getDataPump() == null) {
			if (microseconds != 0) {
				throw new IllegalStateException("cannot set non-zero position in closed state");
			}
		}
		else if (sequence == null) {
			if (microseconds != 0) {
				throw new IllegalStateException("cannot set non-zero position if sequence is not set");
			}
		} else {
			synchronized(tempoCache) {
				setTickPosition(MidiUtils.microsecond2tick(sequence, microseconds, tempoCache));
			}
		}
	}


	public void setMasterSyncMode(Sequencer.SyncMode sync) {
		// not supported
	}


	public Sequencer.SyncMode getMasterSyncMode() {
		return masterSyncMode;
	}


	public Sequencer.SyncMode[] getMasterSyncModes() {
		Sequencer.SyncMode[] returnedModes = new Sequencer.SyncMode[masterSyncModes.length];
		System.arraycopy(masterSyncModes, 0, returnedModes, 0, masterSyncModes.length);
		return returnedModes;
	}


	public void setSlaveSyncMode(Sequencer.SyncMode sync) {
		// not supported
	}


	public Sequencer.SyncMode getSlaveSyncMode() {
		return slaveSyncMode;
	}


	public Sequencer.SyncMode[] getSlaveSyncModes() {
		Sequencer.SyncMode[] returnedModes = new Sequencer.SyncMode[slaveSyncModes.length];
		System.arraycopy(slaveSyncModes, 0, returnedModes, 0, slaveSyncModes.length);
		return returnedModes;
	}

	protected int getTrackCount() {
		MidiSequence seq = getMidiSequence();
		if (seq != null) {
			// $$fb wish there was a nicer way to get the number of tracks...
			return sequence.getTracks().length;
		}
		return 0;
	}


	// note that the returned List is not necessarily ordered
	// the same as MidiSequence.getMidiTracks()
	public List<TrackControls> getTrackControls() {
		return getDataPump().getTrackControls();
	}

	public TrackControls getTrackControls(MidiTrack t) {
		for ( TrackControls tc : getTrackControls() ) {
			if ( !(tc instanceof DataPump.DefaultSequencerTrack) ) return null;
			DataPump.DefaultSequencerTrack dst = (DataPump.DefaultSequencerTrack)tc;
			if ( dst.getMidiTrack() == t ) return tc;
		}
		return null;
	}
	/* *********************************** play control ************************* */

	/*
	 */
	public void open() throws MidiUnavailableException {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: implOpen()");
		//openInternalSynth();

		// create PlayThread
		playEngine = new PlayEngine();

		//id = nOpen();
		//if (id == 0) {
		//    throw new MidiUnavailableException("unable to open sequencer");
		//}
		if (sequence != null) {
			playEngine.setSequence(sequence);
		}

		// propagate caches
		propagateCaches();

		if (Printer.trace) Printer.trace("<< RealTimeSequencer: implOpen() succeeded");
	}

	private synchronized void propagateCaches() {
		// only set caches if open and sequence is set
		if (sequence != null && isOpen()) {
			if (cacheTempoFactor != -1) {
				setTempoFactor(cacheTempoFactor);
			}
			if (cacheTempoMPQ == -1) {
				setTempoInMPQ((new MidiUtils.TempoCache(sequence)).getTempoMPQAt(getTickPosition()));
			} else {
				setTempoInMPQ((float) cacheTempoMPQ);
			}
		}
	}

	/** populate the caches with the current values */
	private synchronized void setCaches() {
		cacheTempoFactor = getTempoFactor();
		cacheTempoMPQ = getTempoInMPQ();
	}



	public synchronized void closeMidi() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: implClose() ");

		if (playEngine == null) {
			if (Printer.err) Printer.err("RealTimeSequencer.implClose() called, but playThread not instanciated!");
		} else {
			// Interrupt playback loop.
			playEngine.close();
			playEngine = null;
		}

//		super.implClose();

		sequence = null;
		running = false;
		cacheTempoMPQ = -1;
		cacheTempoFactor = -1;

		if (Printer.trace) Printer.trace("<< RealTimeSequencer: implClose() completed");
	}

	protected void implPlay() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: implStart()");

		if (playEngine == null) {
			if (Printer.err) Printer.err("RealTimeSequencer.implStart() called, but playThread not instanciated!");
			return;
		}

		tempoCache.refresh(sequence);
		if (!running) {
			running  = true;
			playEngine.start();
		}
		if (Printer.trace) Printer.trace("<< RealTimeSequencer: implStart() completed");
	}


	protected void implStop() {
		if (Printer.trace) Printer.trace(">> RealTimeSequencer: implStop()");

		if (playEngine == null) {
			if (Printer.err) Printer.err("RealTimeSequencer.implStop() called, but playThread not instanciated!");
			return;
		}

		recording = false;
		if (running) {
			running = false;
			playEngine.stop();
		}
		if (Printer.trace) Printer.trace("<< RealTimeSequencer: implStop() completed");
	}


	private boolean needCaching() {
		return !isOpen() || (sequence == null) || (playEngine == null);
	}

	/**
	 * return the data pump instance, owned by play thread
	 * if playthread is null, return null.
	 * This method is guaranteed to return non-null if
	 * needCaching returns false
	 */
	private DataPump getDataPump() {
		if (playEngine != null) {
			return playEngine.getDataPump();
		}
		return null;
	}

	private MidiUtils.TempoCache getTempoCache() {
		return tempoCache;
	}

	// INNER CLASSES

	class PlayEngine implements Runnable {
		private Thread thread;
		private Object lock = new Object();

		/** true if playback is interrupted (in close) */
		boolean interrupted = false;
		boolean isPumping = false;

		private DataPump dataPump = new DataPump();


		PlayEngine() {
			// nearly MAX_PRIORITY
			int priority = Thread.NORM_PRIORITY
			+ ((Thread.MAX_PRIORITY - Thread.NORM_PRIORITY) * 3) / 4;
			thread = JSSecurityManager.createThread(this,
					"Java Sound Sequencer", // name
					false,                  // daemon
					priority,               // priority
					true);                  // doStart
		}

		DataPump getDataPump() {
			return dataPump;
		}

		synchronized void setSequence(MidiSequence seq) {
			dataPump.setSequence(seq);
		}


		/** start thread and pump. Requires up-to-date tempoCache */
		synchronized void start() {
			// mark the sequencer running
			running = true;

			if (!dataPump.hasCachedTempo()) {
				long tickPos = getTickPosition();
				dataPump.setTempoMPQ(tempoCache.getTempoMPQAt(tickPos));
			}
			dataPump.checkPointMillis = 0; // means restarted
			dataPump.clearNoteOnCache();
			dataPump.needReindex = true;

			// notify the thread
			synchronized(lock) {
				lock.notifyAll();
			}

			if (Printer.debug) Printer.debug(" ->Started MIDI play thread");

		}

		// waits until stopped
		synchronized void stop() {
			playThreadImplStop();
			long t = System.nanoTime() / 1000000l;
			while (isPumping) {
				synchronized(lock) {
					try {
						lock.wait(2000);
					} catch (InterruptedException ie) {
						// ignore
					}
				}
				// don't wait for more than 2 seconds
				if ((System.nanoTime()/1000000l) - t > 1900) {
					if (Printer.err) Printer.err("Waited more than 2 seconds in RealTimeSequencer.PlayThread.stop()!");
					//break;
				}
			}
		}

		void playThreadImplStop() {
			// mark the sequencer running
			running = false;
			synchronized(lock) {
				lock.notifyAll();
			}
		}

		void close() {
			Thread oldThread = null;
			synchronized (this) {
				// dispose of thread
				interrupted = true;
				oldThread = thread;
				thread = null;
			}
			if (oldThread != null) {
				// wake up the thread if it's in wait()
				synchronized(lock) {
					lock.notifyAll();
				}
			}
			// wait for the thread to terminate itself,
			// but max. 2 seconds. Must not be synchronized!
			if (oldThread != null) {
				try {
					oldThread.join(2000);
				} catch (InterruptedException ie) {}
			}
		}


		/**
		 * Main process loop driving the media flow.
		 *
		 * Make sure to NOT synchronize on RealTimeSequencer
		 * anywhere here (even implicit). That is a sure deadlock!
		 */
		public void run() {

			while (!interrupted) {
				boolean EOM = false;
				boolean wasRunning = running;
				isPumping = !interrupted && running;
				while (!EOM && !interrupted && running) {
					EOM = dataPump.pump();

					try {
						Thread.sleep(1);
					} catch (InterruptedException ie) {
						// ignore
					}
				}
				if (Printer.debug) {
					Printer.debug("Exited main pump loop because: ");
					if (EOM) Printer.debug(" -> EOM is reached");
					if (!running) Printer.debug(" -> running was set to false");
					if (interrupted) Printer.debug(" -> interrupted was set to true");
				}

				playThreadImplStop();
				if (wasRunning) {
					dataPump.notesOff(true);
				}
				if (EOM) {
					dataPump.setTickPos(sequence.getTickLength());

					// send EOT event (mis-used for end of media)
					/*					MetaMessage message = new MetaMessage();
					try{
						message.setMessage(MidiUtils.META_END_OF_TRACK_TYPE, new byte[0], 0);
					} catch(InvalidMidiDataException e1) {}
					sendMetaEvents(message); */
				}
				synchronized (lock) {
					isPumping = false;
					// wake up a waiting stop() method
					lock.notifyAll();
					while (!running && !interrupted) {
						try {
							lock.wait();
						} catch (Exception ex) {}
					}
				}
			} // end of while(!EOM && !interrupted && running)
			if (Printer.debug) Printer.debug("end of play thread");
		}
	}


	/**
	 * class that does the actual dispatching of events,
	 * used to be in native in MMAPI
	 */
	private class DataPump {
		private float currTempo;         // MPQ tempo
		private float tempoFactor;       // 1.0 is default
		private float inverseTempoFactor;// = 1.0 / tempoFactor
		private long ignoreTempoEventAt; // ignore next META tempo during playback at this tick pos only
		private int resolution;
		private float divisionType;
		private long checkPointMillis;   // microseconds at checkoint
		private long checkPointTick;     // ticks at checkpoint
		private List<SequencerTrack> seqTracks = new ArrayList<SequencerTrack>();
		private long lastTick;
		private boolean needReindex = false;
		private int soloCount;		// ST

		DataPump() {
			init();
		}

		synchronized void init() {
			ignoreTempoEventAt = -1;
			tempoFactor = 1.0f;
			inverseTempoFactor = 1.0f;
			soloCount = 0;
		}

		synchronized void setTickPos(long tickPos) {
			long oldLastTick = tickPos;
			lastTick = tickPos;
			if (running) {
				notesOff(false);
			}
			if (running || tickPos > 0) {
				// will also reindex
				chaseEvents(oldLastTick, tickPos);
			} else {
				needReindex = true;
			}
			if (!hasCachedTempo()) {
				setTempoMPQ(getTempoCache().getTempoMPQAt(lastTick, currTempo));
				// treat this as if it is a real time tempo change
				ignoreTempoEventAt = -1;
			}
			// trigger re-configuration
			checkPointMillis = 0;
		}

		long getTickPos() {
			return lastTick;
		}

		// hasCachedTempo is only valid if it is the current position
		boolean hasCachedTempo() {
			if (ignoreTempoEventAt != lastTick) {
				ignoreTempoEventAt = -1;
			}
			return ignoreTempoEventAt >= 0;
		}

		// this method is also used internally in the pump!
		synchronized void setTempoMPQ(float tempoMPQ) {
			if (tempoMPQ > 0 && tempoMPQ != currTempo) {
				ignoreTempoEventAt = lastTick;
				this.currTempo = tempoMPQ;
				// re-calculate check point
				checkPointMillis = 0;
			}
		}

		float getTempoMPQ() {
			return currTempo;
		}

		synchronized void setTempoFactor(float factor) {
			if (factor > 0 && factor != this.tempoFactor) {
				tempoFactor = factor;
				inverseTempoFactor = 1.0f / factor;
				// re-calculate check point
				checkPointMillis = 0;
			}
		}

		float getTempoFactor() {
			return tempoFactor;
		}

		// ST replacement for muteSoloChanged()
		synchronized void muteSoloChanged(int soloChange) {
			soloCount += soloChange;
			updateTrackEnables(soloCount != 0); // pass hasSolo
		}


		synchronized void setSequence(MidiSequence seq) {
			if (seq == null) {
				init();
				return;
			}
			seqTracks.clear(); // !!!
			removeAllMidiOutputs(); // !!!
			removeAllMidiInputs(); // !!!
			// !!! !!! need to follow sequence track add/remove !!! !!!
			for ( int i = 0; i < seq.getMidiTrackCount(); i++ ) {
				seqTracks.add(new DefaultSequencerTrack(seq.getMidiTrack(i)));
			}
//			muteSoloChanged(0);
			soloCount = 0;
			resolution = seq.getResolution();
			divisionType = seq.getDivisionType();
			// trigger re-initialization
			checkPointMillis = 0;
			needReindex = true;
		}

		synchronized void syncTracks() {
			// if track added
			// if running?
			// chaseEvents for added tracks
			// chaseEvents(0, lastTick, true, tempArray);
			// notesOff for deleted tracks
			// notesOff(false);
		}
		
		void clearNoteOnCache() {
			for ( SequencerTrack t : seqTracks ) {
				t.clearNoteOnCache();
			}
		}

		void notesOff(boolean doControllers) {
			for ( SequencerTrack t : seqTracks ) {
				t.notesOff(doControllers);
			}
		}


		List<TrackControls> getTrackControls() {
			return Collections.<TrackControls>unmodifiableList(seqTracks);
		}
		
		// ST replacement for applyDisabledTracks(...)
		/**
		 * Runtime application of mute/solo
		 * Called from muteSoloChanged() which is synchronised so we are called
		 * inbetween pump()s.
		 */
		private void updateTrackEnables(boolean hasSolo) {
			byte[][] tempArray = new byte[128][16]; // !!! TODO
			for ( SequencerTrack t : seqTracks ) {
				t.updateEnable(hasSolo, tempArray);
			}
			tempArray = null;
		}
		
		/** chase controllers and program for all tracks */
		synchronized void chaseEvents(long startTick, long endTick) {
			if (DEBUG_PUMP) Printer.println(">> chaseEvents from tick "+startTick+".."+(endTick-1));
			byte[][] tempArray = new byte[128][16];
			for (int t = 0; t < seqTracks.size(); t++) {
				SequencerTrack st = seqTracks.get(t);
				if ( st.isEnabled() ) {
					// if track is not disabled, chase the events for it
					st.chaseEvents(startTick, endTick, true, tempArray);
				}
			}
			if (DEBUG_PUMP) Printer.println("<< chaseEvents");
		}


		// playback related methods (pumping)

		private long getCurrentTimeMillis() {
			return System.nanoTime() / 1000000l;
			//return perf.highResCounter() * 1000 / perfFreq;
		}

		private long millis2tick(long millis) {
			if (divisionType != Sequence.PPQ) {
				double dTick = ((((double) millis) * tempoFactor)
						* ((double) divisionType)
						* ((double) resolution))
						/ ((double) 1000);
				return (long) dTick;
			}
			return MidiUtils.microsec2ticks(millis * 1000,
					currTempo * inverseTempoFactor,
					resolution);
		}

		@SuppressWarnings("unused")
		private long tick2millis(long tick) {
			if (divisionType != Sequence.PPQ) {
				double dMillis = ((((double) tick) * 1000) /
						(tempoFactor * ((double) divisionType) * ((double) resolution)));
				return (long) dMillis;
			}
			return MidiUtils.ticks2microsec(tick,
					currTempo * inverseTempoFactor,
					resolution) / 1000;
		}

		/** 
		 * the main pump method
		 * @return true if end of sequence is reached
		 */
		synchronized boolean pump() {
			long currMillis;
			long targetTick = lastTick;
			boolean changesPending = false;
			boolean EOM = false;

			currMillis = getCurrentTimeMillis();
			int finishedTracks = 0;
			do {
				changesPending = false;

				// need to re-find indexes in all tracks?
				if (needReindex) {
					if (DEBUG_PUMP) Printer.println("Need to re-index at "+currMillis+" millis. TargetTick="+targetTick);
					for ( SequencerTrack t : seqTracks ) {
						t.reindex(targetTick);
//						if (DEBUG_PUMP_ALL) Printer.println("  Setting trackReadPos["+t+"]="+trackReadPos[t]);
					}
					needReindex = false;
					checkPointMillis = 0;
				}

				// get target tick from current time in millis
				if (checkPointMillis == 0) {
					// new check point
					currMillis = getCurrentTimeMillis();
					checkPointMillis = currMillis;
					targetTick = lastTick;
					checkPointTick = targetTick;
					if (DEBUG_PUMP) Printer.println("New checkpoint to "+currMillis+" millis. "
							+"TargetTick="+targetTick
							+" new tempo="+MidiUtils.convertTempo(currTempo)+"bpm");
				} else {
					// calculate current tick based on current time in milliseconds
					targetTick = checkPointTick + millis2tick(currMillis - checkPointMillis);
					if (DEBUG_PUMP_ALL) Printer.println("targetTick = "+targetTick+" at "+currMillis+" millis");
					lastTick = targetTick;
				}

				finishedTracks = 0;

				for (int t = 0; t < seqTracks.size(); t++) {
					SequencerTrack seqTrack = seqTracks.get(t);
					try {
//						boolean disabled = trackDisabled[t];
						changesPending = seqTrack.pump(targetTick, t == 0);
						if (seqTrack.isFinished()) {
							finishedTracks++;
						}
					} catch(Exception e) {
						if (Printer.debug) Printer.debug("Exception in Sequencer pump!");
						if (Printer.debug) e.printStackTrace();
						if (e instanceof ArrayIndexOutOfBoundsException) {
							needReindex = true;
							changesPending = true;
						}
					}
					if (changesPending) {
						break;
					}
				}
				EOM = (finishedTracks == seqTracks.size());
			} while (changesPending);

			return EOM;
		}

		public abstract class AbstractSequencerTrack implements SequencerTrack 
		{
			protected DefaultMidiOutput outPort;
			private int[] noteOnCache = new int[128]; // bit-mask of notes that are currently on
			protected boolean finished = false;
			protected boolean mute = false;
			protected boolean solo = false;

			/**
			 * Meta event listeners
			 */
			private ArrayList<MetaEventListener> metaEventListeners = new ArrayList<MetaEventListener>();


			/** Must be called once before pump() */
			protected void createPorts(String name) {
				if ( outPort == null ) {
					outPort = new DefaultMidiOutput("Sequencer: "+name);
					addMidiOutput(outPort);
				}
			}

			protected void removePorts() {
				removeMidiOutput(outPort);
			}
			
			/* (non-Javadoc)
			 * @see uk.org.toot.midi.sequencer.SequencerTrack#isFinished()
			 */
			public boolean isFinished() {
				return finished;
			}

			/* (non-Javadoc)
			 * @see uk.org.toot.midi.sequencer.SequencerTrack#clearNoteOnCache()
			 */
			public void clearNoteOnCache() {
				for (int i = 0; i < 128; i++) {
					noteOnCache[i] = 0;
				}
			}

			/* (non-Javadoc)
			 * @see uk.org.toot.midi.sequencer.SequencerTrack#notesOff(boolean)
			 */
			public void notesOff(boolean doControllers) {
				int done = 0;
				for (int ch=0; ch<16; ch++) {
					int channelMask = (1<<ch);
					for (int i=0; i<128; i++) {
						if ((noteOnCache[i] & channelMask) != 0) {
							noteOnCache[i] ^= channelMask;
							// send note on with velocity 0
							sendMessage((ShortMessage.NOTE_ON | ch) | (i<<8), -1);
							done++;
						}
					}
					/* all notes off */
					sendMessage((ShortMessage.CONTROL_CHANGE | ch) | (123<<8), -1);
					/* sustain off */
					sendMessage((ShortMessage.CONTROL_CHANGE | ch) | (64<<8), -1);
					if (doControllers) {
						/* reset all controllers */
						sendMessage((ShortMessage.CONTROL_CHANGE | ch) | (121<<8), -1);
						done++;
					}
				}
				if (DEBUG_PUMP) Printer.println("  noteOff: sent "+done+" messages.");
			}

			protected void sendMessage(MidiMessage msg, long timestamp) {
				outPort.transport(msg, timestamp);
				int msgStatus = msg.getStatus();
				switch (msgStatus & 0xF0) {
				case ShortMessage.NOTE_OFF: {
					// note off - clear the bit in the noteOnCache array
					int note = ((ShortMessage) msg).getData1() & 0x7F;
					noteOnCache[note] &= (0xFFFF ^ (1<<(msgStatus & 0x0F)));
					break;
				}

				case ShortMessage.NOTE_ON: {
					// note on
					ShortMessage smsg = (ShortMessage) msg;
					int note = smsg.getData1() & 0x7F;
					int vel = smsg.getData2() & 0x7F;
					if (vel > 0) {
						// if velocity > 0 set the bit in the noteOnCache array
						noteOnCache[note] |= 1<<(msgStatus & 0x0F);
					} else {
						// if velocity = 0 clear the bit in the noteOnCache array
						noteOnCache[note] &= (0xFFFF ^ (1<<(msgStatus & 0x0F)));
					}
					break;
				}

				}
			}

			protected void sendMessage(int m, long timestamp) {
				try {
					sendMessage(new FastShortMessage(m), timestamp);
				} catch ( InvalidMidiDataException imde ) {

				}
			}

			/**
			 *  @return true if changes are pending, always false for this implementation 
			 */
			protected boolean dispatchMessage(boolean masterTrack, MidiEvent event) {
				boolean changesPending = false;
				MidiMessage message = event.getMessage();
				int msgStatus = message.getStatus();
				int msgLen = message.getLength();
				if (msgStatus == MetaMessage.META && msgLen >= 2) {
					// a meta message. Do not send it to the device.
					// 0xFF with length=1 is a MIDI realtime message
					// which shouldn't be in a Sequence, but we play it
					// nonetheless.

					// see if this is a tempo message. Only on track 0.
					if (masterTrack) {
						int newTempo = MidiUtils.getTempoMPQ(message);
						if (newTempo > 0) {
							if (event.getTick() != ignoreTempoEventAt) {
								setTempoMPQ(newTempo); // sets ignoreTempoEventAt!
								changesPending = true;
							}
							// next loop, do not ignore anymore tempo events.
							ignoreTempoEventAt = -1;
						}
					}
					// send to listeners
					sendMetaEvents(message);

				} else {
					// not meta, send to device
					sendMessage(message, -1);

				}
				return changesPending;
			}

			/* (non-Javadoc)
			 * @see uk.org.toot.midi.sequencer.SequencerTrack#chaseEvents(long, long, boolean, byte[][])
			 */
			public void chaseEvents(long startTick, long endTick, boolean doReindex, byte[][] tempArray) {
			}

			/* (non-Javadoc)
			 * @see uk.org.toot.midi.sequencer.SequencerTrack#reindex()
			 */
			public abstract void reindex(long tick);

			/* (non-Javadoc)
			 * @see uk.org.toot.midi.sequencer.SequencerTrack#pump(long, boolean, boolean)
			 */
			public abstract boolean pump(long targetTick, boolean masterTrack);

			public boolean addMetaEventListener(MetaEventListener listener) {
				synchronized(metaEventListeners) {
					if (! metaEventListeners.contains(listener)) {

						metaEventListeners.add(listener);
					}
					return true;
				}
			}


			public void removeMetaEventListener(MetaEventListener listener) {
				synchronized(metaEventListeners) {
					int index = metaEventListeners.indexOf(listener);
					if (index >= 0) {
						metaEventListeners.remove(index);
					}
				}
			}

			/**
			 * Send midi player events.
			 * must not be synchronized on "this"
			 */
			protected void sendMetaEvents(MidiMessage message) {
				if (metaEventListeners.size() == 0) return;

				if (Printer.debug) Printer.debug("sending a meta event");
				eventDispatcher.sendAudioEvents(message, metaEventListeners);
			}

			public boolean isMute() {
				return mute;
			}
			
			public void setMute(boolean mute) {
				if ( this.mute != mute ) {
					this.mute = mute;
					muteSoloChanged(0);
				}
			}
			
			public boolean isSolo() {
				return solo;
			}
			
			public void setSolo(boolean solo) {
				if ( this.solo != solo ) {
					this.solo = solo;
					muteSoloChanged(solo ? 1 : -1);
				}
			}
		}

		/**
		 * A DefaultSequencerTrack handles the sequencing of a Sequence Track.
		 * @author st
		 */
		private class DefaultSequencerTrack extends AbstractSequencerTrack
		{
			private MidiTrack midiTrack;
			private Track track;
			private int trackReadPos;        // read index
			private RecordingInput inPort;
			private boolean enable = true;
			private boolean record = false;
			private PropertyChangeListener listener;

			DefaultSequencerTrack(MidiTrack t) {
				midiTrack = t;
				track = t.getTrack();
				// TODO follow track name changes
				createPorts(t.getTrackName());
				listener = new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent arg0) {
						// TODO move to renamePorts() and override
						outPort.setName("Sequencer: "+midiTrack.getTrackName());
						inPort.setName("Sequencer: "+midiTrack.getTrackName());
						// notify device because port name changes don't by themselves !!!
						setChanged();
						notifyObservers();
					}
					
				};
				midiTrack.getPropertyChangeSupport().
					addPropertyChangeListener("trackName", listener);
			}

			public void close() {
				midiTrack.getPropertyChangeSupport().
					removePropertyChangeListener("trackName", listener);
				// TODO remove ports, connections
			}
			
			public MidiTrack getMidiTrack() {
				return midiTrack;
			}
			
			/** Must be called once before pump() */
			protected void createPorts(String name) {
				super.createPorts(name);
				if ( inPort == null ) {
					inPort = new RecordingInput("Sequencer: "+name);
					addMidiInput(inPort);
				}
			}

			protected void removePorts() {
				removeMidiInput(inPort);
				super.removePorts();
			}
			
			public boolean isEnabled() {
				return enable;
			}
			
			public void updateEnable(boolean hasSolo, byte[][] tempArray) {
				boolean newEnable = hasSolo ? solo : !mute;
				if ( running ) {
					if ( enable && !newEnable ) {
						// case that a track gets muted: need to
						// send appropriate note off events to prevent
						// hanging notes
						notesOff(false);
					} else if ( !enable && newEnable ) {
						// case that a track was muted and is now unmuted
						// need to chase events and re-index this track
						chaseEvents(0, lastTick, true, tempArray);
					}
				}
				enable = newEnable;
			}
			
			/* (non-Javadoc)
			 * @see uk.org.toot.midi.sequencer.SequencerTrack#chaseEvents(long, long, boolean, byte[][])
			 */
			public void chaseEvents(
					long startTick,
					long endTick,
					boolean doReindex,
					byte[][] tempArray) {
				if (startTick > endTick) {
					// start from the beginning
					startTick = 0;
				}
				byte[] progs = new byte[16];
				// init temp array with impossible values
				for (int ch = 0; ch < 16; ch++) {
					progs[ch] = -1;
					for (int co = 0; co < 128; co++) {
						tempArray[co][ch] = -1;
					}
				}
				int size = track.size();
				try {
					for (int i = 0; i < size; i++) {
						MidiEvent event = track.get(i);
						if (event.getTick() >= endTick) {
							if (doReindex) {
								trackReadPos = (i > 0)?(i-1):0;
								if (DEBUG_PUMP) Printer.println("  chaseEvents: setting trackReadPos = "+trackReadPos);
							}
							break;
						}
						MidiMessage msg = event.getMessage();
						int status = msg.getStatus();
						int len = msg.getLength();
						if (len == 3 && ((status & 0xF0) == ShortMessage.CONTROL_CHANGE)) {
							if (msg instanceof ShortMessage) {
								ShortMessage smsg = (ShortMessage) msg;
								tempArray[smsg.getData1() & 0x7F][status & 0x0F] = (byte) smsg.getData2();
							} else {
								byte[] data = msg.getMessage();
								tempArray[data[1] & 0x7F][status & 0x0F] = data[2];
							}
						}
						if (len == 2 && ((status & 0xF0) == ShortMessage.PROGRAM_CHANGE)) {
							if (msg instanceof ShortMessage) {
								ShortMessage smsg = (ShortMessage) msg;
								progs[status & 0x0F] = (byte) smsg.getData1();
							} else {
								byte[] data = msg.getMessage();
								progs[status & 0x0F] = data[1];
							}
						}
					}
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					// this happens when messages are removed
					// from the track while this method executes
				}
				int numControllersSent = 0;
				// now send out the aggregated controllers and program changes
				for (int ch = 0; ch < 16; ch++) {
					for (int co = 0; co < 128; co++) {
						byte controllerValue = tempArray[co][ch];
						if (controllerValue >= 0) {
							int packedMsg = (ShortMessage.CONTROL_CHANGE | ch) | (co<<8) | (controllerValue<<16);
							sendMessage(packedMsg, -1);
							numControllersSent++;
						}
					}
					// send program change *after* controllers, to
					// correctly initialize banks
					if (progs[ch] >= 0) {
						sendMessage((ShortMessage.PROGRAM_CHANGE | ch) | (progs[ch]<<8), -1);
					}
					if (progs[ch] >= 0 || startTick == 0 || endTick == 0) {
						// reset pitch bend on this channel (E0 00 40)
						sendMessage((ShortMessage.PITCH_BEND | ch) | (0x40 << 16), -1);
						// reset sustain pedal on this channel
						sendMessage((ShortMessage.CONTROL_CHANGE | ch) | (64 << 8), -1);
					}
				}
				if (DEBUG_PUMP) Printer.println("  chaseEvents : sent "+numControllersSent+" controllers."); // TODO
			}

			/* (non-Javadoc)
			 * @see uk.org.toot.midi.sequencer.SequencerTrack#reindex(long)
			 */
			public void reindex(long tick) {
				trackReadPos = MidiUtils.tick2index(track, tick);
			}

			/* (non-Javadoc)
			 * @see uk.org.toot.midi.sequencer.SequencerTrack#pump(long, boolean)
			 */
			public boolean pump(long targetTick, boolean masterTrack) {
				MidiEvent currEvent;
				boolean changesPending = false;
				int readPos = trackReadPos;
				int size = track.size();
				// play all events that are due until targetTick
				while (!changesPending && (readPos < size)
						&& (currEvent = track.get(readPos)).getTick() <= targetTick) {

					if ((readPos == size -1) &&  MidiUtils.isMetaEndOfTrack(currEvent.getMessage())) {
						// do not send out this message. Finished with this track
						readPos = size;
						break;
					}
					// TODO: some kind of heuristics if the MIDI messages have changed
					// significantly (i.e. deleted or inserted a bunch of messages)
					// since last time. Would need to set needReindex = true then
					readPos++;
					// only play this event if the track is enabled,
					// or if it is a tempo message on track 0
					// Note: cannot put this check outside
					//       this inner loop in order to detect end of file
					if (enable ||
							((masterTrack) && (MidiUtils.isMetaTempo(currEvent.getMessage())))) {
						changesPending = dispatchMessage(masterTrack, currEvent);
					}
				}
				trackReadPos = readPos;
				if (readPos >= size) {
					finished = true;
				}

				return changesPending;
			} 
			
			public void recordEnable() {
				record = true;
			}


			public void recordDisable() {
				record = true;
			}


			private class RecordingInput implements MidiInput
			{
				private String name;
				
				public RecordingInput(String name) {
					this.name = name;
				}
				
				public void setName(String name) {
					this.name = name;
				}
				
				public String getName() {
					return name;
				}

				public void transport(MidiMessage message, long timeStamp) {
					if ( record ) {
						long tickPos = 0;

						// convert timeStamp to ticks
						if (timeStamp < 0) {
							tickPos = getTickPosition();
						} else {
							synchronized(tempoCache) {
								tickPos = MidiUtils.microsecond2tick(sequence, timeStamp, tempoCache);
							}
						}

						// do not record real-time events
						// see 5048381: NullPointerException when saving a MIDI sequence
						if (message.getLength() > 1) {
							// create a copy of this message
							if (message instanceof ShortMessage) {
								message = new FastShortMessage((ShortMessage) message);
							} else {
								message = (MidiMessage) message.clone();
							}

							// create new MidiEvent
							MidiEvent me = new MidiEvent(message, tickPos);
							track.add(me);
						}
					}
				}				

				public String toString() { return name; }

			} // class RecordingInput
		} // class DefaultSeqencerTrack

	} // class DataPump

}
