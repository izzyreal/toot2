package uk.org.toot.music.performance;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Track;

import uk.org.toot.midi.message.ChannelMsg;
import uk.org.toot.music.Note;
import uk.org.toot.music.composition.Timing;

/**
 * A Performer performs on an Instrument.
 * It can swing (eigth note) timing.
 * Currently it can only render to a MIDI file, not perform live.
 * @author st
 *
 */
public class Performer 
{
	private String name;
	private Instrument instrument;
	
	private float swingRatio = 1f; // default no swing

	public Performer(String name, Instrument instrument) {
		this.name = name;
		this.instrument = instrument;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Render a bar of notes as MIDI to the specified Track from the
	 * specified start tick with the specified ticks per bar.
	 * @param notes the notes to render
	 * @param track the MIDI Track to render to
	 * @param startTick the tick at the start of the bar
	 * @param ticksPerBar the number of ticks per bar
	 */
	public void renderBar(int[] notes, Track track, 
			long startTick, int ticksPerBar)
		throws InvalidMidiDataException	{
		final int channel = getInstrument().getChannel();
		MidiMessage msg;
		for ( int i = 0; i < notes.length; i++) {
			int note = notes[i];
			float timeOn = swing(Note.getTime(note));
			int pitch = Note.getPitch(note);
			int level = Note.getLevel(note);
			long onTick = (int)(ticksPerBar * timeOn / Timing.COUNT);
			msg = ChannelMsg.createChannel(
					ChannelMsg.NOTE_ON, channel, pitch, level);
			track.add(new MidiEvent(msg, startTick + onTick));
			// note off
			msg = ChannelMsg.createChannel(
					ChannelMsg.NOTE_OFF, channel, pitch, 0);
			float timeOff = swing(Note.getTime(note)+Note.getDuration(note));
			long offTick = (int)(ticksPerBar * timeOff / Timing.COUNT);
			track.add(new MidiEvent(msg, startTick + offTick));
		}			
	}

	/**
	 * @return the ratio of the first eigth note to the second eigth note
	 * when a quarter note is divided into two with a swing or shuffle rhythm
	 */
	public float getSwingRatio() {
		return swingRatio;
	}

	/**
	 * Set the ratio of the first eigth note to the second eigth note
	 * when a quarter note is divied into two with a swing or shuffle rhythm
	 * @param ratio the ratio of the first eigth note to the second eigth note
	 */
	public void setSwingRatio(float ratio) {
		swingRatio = ratio;
	}

	/**
	 * Swing the timing of a sixty-fourth note timing such that when a
	 * quarter note is divided into two the second eigth note is delayed
	 * relative to its nominal position and all other sixty-fourth note
	 * timings are smoothly varied accordingly.
	 * @param time the timing index of a sixty-fourth note in a bar
	 * @return the swung timing
	 */
	public float swing(int time) {
		float swingFactor = swingRatio - 1;
		double angle = -Math.PI/2 + 2*Math.PI*(time%16)/16;
		return (float)(time + 4 * swingFactor * (Math.sin(angle)+1)/2);
	}

	/**
	 * Print a list of swing conversions for all sixty-fourth note
	 * timing indices.
	 */
	public void checkSwing() {
		for ( int i = 0; i < Timing.COUNT; i++) {
			System.out.println(i+" => "+swing(i));
		}
	}

	/**
	 * @return the instrument
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * @param instrument the instrument to set
	 */
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

}
