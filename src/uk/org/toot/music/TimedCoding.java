package uk.org.toot.music;

/**
 * A TimedCoding is the abstract base class for those musical events which
 * have a time relative to a bar.
 * The time is encoded in the 7 most significant bits of a positive int
 * so time-ordering of such ints is simply achieved by sorting.
 * The time is relative to the start of a bar.
 * The resolution is 127 sixty-fourth notes so 5/4 (80), 6/4 (96) and 7/4 (112) time
 * signatures are possible as are 9/8 through 15/8 (120) and all time signatures which are
 * logically shorter than 4/4 such as 3/4 (48), 7/8 (56) etc.
 * @author st
 *
 * Format 0ttttttt xxxxxxxx xxxxxxxx xxxxxxxx
 * always positive
 * can be sorted by time (t)
 * leaves lower 24 bits for subclass use
 */
public abstract class TimedCoding {

	private static final int TIME_SHIFT = 24;
	private static final int TIME_MASK = 0x7f;

	protected static int create(int time) {
		return setTime(0, time);
	}
	
	/**
	 * Return the time, in sixty-forths, of the specified event.
	 * @param note the int which contains the time of the event
	 * @return the time, in sixty-fourths, of the event
	 */
	public static int getTime(int coded) {
		return (coded >> TIME_SHIFT) & TIME_MASK;
	}

	/**
	 * Encode the time into a coded event.
	 * @param coded the coded event to have the time set
	 * @param time the time to encode into the coded event
	 * @return int - the modified coded event
	 */
	public static int setTime(int coded, int time) {
		coded &= ~(TIME_MASK << TIME_SHIFT);
		coded |= (time & TIME_MASK) << TIME_SHIFT;
		return coded;
	}

}
