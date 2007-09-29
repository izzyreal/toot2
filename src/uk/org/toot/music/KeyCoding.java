package uk.org.toot.music;

/**
 * A KeyCoding helps represent a key as an int.
 * @author st
 *
 */
public class KeyCoding extends TimedCoding
{
	private final static int ROOT_SHIFT = 12;
	private final static int ROOT_MASK = 0x0f;
	private final static int INTERVAL_SHIFT = 0;
	private final static int INTERVAL_MASK = 0x7ff;

	public static int create(int time, int root, int intervals) {
		int key = create(time);
		key = setRoot(key, root);
		key = setIntervals(key, intervals);
		return key;
	}
	
	/**
	 * Return the root note of the specified key change.
	 * @param key the int which represents the key change
	 * @return the root of the key change.
	 */
	public static int getRoot(int key) {
		return (key >> ROOT_SHIFT) & ROOT_MASK;
	}
	
	public static int setRoot(int key, int root) {
		key &= ~(ROOT_MASK << ROOT_SHIFT);
		key |= (root & ROOT_MASK) << ROOT_SHIFT;
		return key;
	}

	/**
	 * Return the intervals of the specified key change.
	 * @param key the int which represents the key change
	 * @return the root of the key change.
	 */
	public static int getIntervals(int key) {
		return (key >> INTERVAL_SHIFT) & INTERVAL_MASK;
	}
	
	public static int setIntervals(int key, int intervals) {
		key &= ~(INTERVAL_MASK << INTERVAL_SHIFT);
		key |= (intervals & INTERVAL_MASK) << INTERVAL_SHIFT;
		return key;
	}


}
