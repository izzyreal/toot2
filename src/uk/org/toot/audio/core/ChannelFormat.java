// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.core;

import static uk.org.toot.localisation.Localisation.*;

/**
 * ChannelFormat provides information about AudioProcess channels.
 *
 * You can find out whether a channel index is left, right, center, front, rear
 * or a low frequency extension (LFE) which is sufficient to describe mono,
 * stereo, quad and 5.1, those formats being predefined as MONO, STEREO, QUAD
 * and FIVE_1.
 *
 * You can also find out which channel indices are left, right, center, front,
 * rear and LFE.
 *
 * You can get the localised name for a channel index and for the format.
 *
 * You can also mix a format with less channels to a format (upmixing).
 *
 * Odd formats like 1.1, 2.1, 3, 3.1 and 4.1 could be provided but are not
 * believed to be significant enough to implement.
 */
public abstract class ChannelFormat
{
    /**
     * Return the number of channels in this format.
     */
    public abstract int getCount();

    public abstract int getCenter(); 	// index or -1 if not present
    public abstract int getLFE();	 	// index or -1 if not present
    public abstract int[] getLeft();	// do not return null
    public abstract int[] getRight();	// do not return null
	public abstract String getName();

    // following methods need only return valid results for valid channel numbers
    public abstract boolean isCenter(int chan);
    public abstract boolean isLeft(int chan);
    public abstract boolean isRight(int chan);
    public abstract boolean isFront(int chan);
    public abstract boolean isRear(int chan);
    public abstract boolean isLFE(int chan);
    public abstract String getName(int chan);

    // experimental !!! !!! !!!
    // valid for destBuffer MONO/STEREO/QUAD
    // override for other formats (or refactor for extension)
	public int mix(AudioBuffer destBuffer, AudioBuffer sourceBuffer, float[] gain) {
        boolean doMix = destBuffer != sourceBuffer;
        int snc = sourceBuffer.getChannelCount();
        int dnc = destBuffer.getChannelCount();
      	int ns = destBuffer.getSampleCount();
        float g;
        float[] in;
        float[] out;
        for ( int i = 0; i < dnc; i++ ) {
            g = gain[i];
            in = sourceBuffer.getChannel(i % snc); // OK for 1, 2, 4, 8
	       	out = destBuffer.getChannel(i);
			if ( doMix ) { // one branch per channel
		        for ( int s = 0; s < ns; s++ ) {
	   	        	out[s] += in[s] * g;
       	        }
            } else {
		        for ( int s = 0; s < ns; s++ ) {
	   		        out[s] = in[s] * g;
       	        }
            }
        }
       	int ret = 1;
		if ( !doMix ) ret |= 2;
    	return ret;
    }

    /**
     * @label MONO 
     * @link aggregationByValue
     * @supplierCardinality 1
     */
    public final static ChannelFormat MONO = new ChannelFormat() {
        private int[] empty = new int[0];
        public int getCount() { return 1; }
        public boolean isCenter(int chan) { return true; }
        public boolean isLeft(int chan) { return false; }
        public boolean isRight(int chan) { return false; }
        public boolean isFront(int chan) { return true; }
        public boolean isRear(int chan) { return false; }
		public boolean isLFE(int chan) { return false; }
        public int getCenter() { return 0; }
        public int getLFE() { return -1; }
        public int[] getLeft() { return empty; }
        public int[] getRight() { return empty; }
		public String getName() { return getString("Mono"); }
        public String getName(int chan) { return getString("Centre"); }
    };

    /**
     * @label STEREO 
     * @link aggregationByValue
     * @supplierCardinality 1
     */
    public final static ChannelFormat STEREO = new ChannelFormat() {
        private int[] left = { 0 };
        private int[] right = { 1 };
        public int getCount() { return 2; }
        public boolean isCenter(int chan) { return false; }
        public boolean isLeft(int chan) { return chan == 0; }
        public boolean isRight(int chan) { return chan == 1; }
        public boolean isFront(int chan) { return true; }
        public boolean isRear(int chan) { return false; }
		public boolean isLFE(int chan) { return false; }
        public int getCenter() { return -1; }
        public int getLFE() { return -1; }
        public int[] getLeft() { return left; }
        public int[] getRight() { return right; }
		public String getName() { return getString("Stereo"); }
        public String getName(int chan) {
            switch ( chan ) {
            case 0: return getString("Left");
            case 1: return getString("Right");
            default: return "illegal channel";
            }
        }
    };

    /**
     * @label QUAD
     * @link aggregationByValue
     * @supplierCardinality 1
     */
    public final static ChannelFormat QUAD = new ChannelFormat() {
        private int[] left = { 0, 2 };
        private int[] right = { 1, 3 };
        public int getCount() { return 4; }
        public boolean isCenter(int chan) { return false; }
        public boolean isLeft(int chan) { return (chan & 1) == 0; }
        public boolean isRight(int chan) { return (chan & 1) == 1; }
        public boolean isFront(int chan) { return chan < 2; }
        public boolean isRear(int chan) { return chan >= 2; }
		public boolean isLFE(int chan) { return false; }
        public int getCenter() { return -1; }
        public int getLFE() { return -1; }
        public int[] getLeft() { return left; }
        public int[] getRight() { return right; }
		public String getName() { return getString("Quad"); }
        public String getName(int chan) {
            switch ( chan ) {
            case 0: return getString("Front.Left");
            case 1: return getString("Front.Right");
            case 2: return getString("Rear.Left");
            case 3: return getString("Rear Right");
            default: return "illegal channel";
            }
        }

    };

    /**
     * @label 5.1
     * @link aggregationByValue
     * @supplierCardinality 1
     */
    public final static ChannelFormat FIVE_1 = new ChannelFormat() {
        private int[] left = { 0, 2 };
        private int[] right = { 1, 3 };
        public int getCount() { return 6; }
        public boolean isCenter(int chan) { return chan == getCenter(); }
        public boolean isLeft(int chan) { return chan < 4 && (chan & 1) == 0; }
        public boolean isRight(int chan) { return chan < 4 && (chan & 1) == 1; }
        public boolean isFront(int chan) { return chan < 2 || chan == 4; }
        public boolean isRear(int chan) { return chan >= 2 && chan < 4; }
		public boolean isLFE(int chan) { return chan == getLFE(); }
        public int getCenter() { return 4; }
        public int getLFE() { return 5; }
        public int[] getLeft() { return left; }
        public int[] getRight() { return right; }
		public String getName() { return "5.1"; }
        public String getName(int chan) {
            switch ( chan ) {
            case 0: return getString("Front.Left");
            case 1: return getString("Front.Right");
            case 2: return getString("Rear.Left");
            case 3: return getString("Rear.Right");
            case 4: return getString("Centre");
            case 5: return getString("LFE");
            default: return "illegal channel";
            }
        }

		public int mix(AudioBuffer destBuffer, AudioBuffer sourceBuffer, float[] gain) {
            throw new IllegalArgumentException("5.1 mix not implemented!");
            // and probaby can't be implemented by float gain[]
            // center?
            // LFE? all channels?
        }
    };
}
