package uk.org.toot.midi.sequencer;

import javax.sound.midi.MetaEventListener;

public interface SequencerTrack extends TrackControls 
{
	public void close();
	
	/**
	 * Return whether no more messages can be pumped because track contents
	 * known a priori are completely finished. Just not having anything to pump
	 * for now does not mean it has finished if more pumping may arise, perhaps
	 * due to just-in-time composition.
	 * @return true if pumping has completely finished
	 */
	public boolean isFinished();
	
	/**
	 * @return whether mute/solo cause track to be enabled
	 */
	public boolean isEnabled();

	public void clearNoteOnCache();

	/**
	 * Update the enabled state based on mute/solo and hasSolo
	 * @param hasSolo
	 * @param tempArray
	 */
	public void updateEnable(boolean hasSolo, byte[][] tempArray);
	
	/**
	 * send note off for notes that are on
	 */
	public void notesOff(boolean doControllers);

	/** 
	 * go through all events from startTick to endTick
	 * chase the controller state and program change state
	 * and then set the end-states at once.
	 *
	 * needs to be called in synchronized state
	 * @param tempArray an byte[128][16] to hold controller messages
	 */
	public void chaseEvents(long startTick, long endTick, boolean doReindex,
			byte[][] tempArray);

	/**
	 * Called when there has been a discontinuity in sequencer time due
	 * to setting a sequence, starting it, changing its position.
	 * Also called on recoverable pumping errors.
	 * @param tick the tick to continue from
	 */
	public void reindex(long tick);

	/**
	 * Pump all messages occuring from the previous to the current targetTick,
	 * exclusive of the previous and inclusive of the current targetTick.
	 * We are passed disabled because we need to carry on reading but not sending
	 * events when disabled. So that we are in the correct position when we are 
	 * reenabled and so that we can detect finishing.
	 * We are passed masterTrack so that we can decode tempo changes if appropriate.
	 * @param targetTick the tick to pump until
	 * @param masterTrack whether this sequencer track is the master track
	 * @return true if changes are pending
	 */
	public boolean pump(long targetTick, boolean masterTrack);

	public boolean addMetaEventListener(MetaEventListener listener);
	public void removeMetaEventListener(MetaEventListener listener);
}