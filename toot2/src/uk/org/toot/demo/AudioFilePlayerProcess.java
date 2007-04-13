// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.demo;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Arrays;
import javax.sound.sampled.*; // !!!
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.ChannelFormat;
import org.tritonus.share.TCircularBuffer;

/**
 * A simple (hacky) audio file player process that is NOT sample-accurate.
 * Playing always starts at the beginning of a buffer.
 * Also, files are not played back at their natural sample rate,
 * they are played back at the process sample rate.
 * Glitches may occur.
 */
public class AudioFilePlayerProcess implements AudioProcess
{
    private File file = null;
    protected AudioInputStream ais = null;
    private ProcessFormat format;
    private ChannelFormat channelFormat;
    private byte[] byteBuffer = new byte[2000];
    private boolean loop = false;
    private AudioBuffer.MetaInfo metaInfo;
    private TCircularBuffer circularBuffer;
    private Puller puller = null;

    private boolean useThreads = true;
	private static ThreadGroup threadGroup = null;
    boolean debug = false;

    public AudioFilePlayerProcess() {
	    if ( useThreads ) {
			// circular buffer blocks on read and write, no trigger
    		circularBuffer = new TCircularBuffer(250000, true, true, null);
        	puller = new Puller();
            if ( threadGroup == null ) {
                threadGroup = new ThreadGroup("Toot MultiTrackPlayer");
            }
        }
    }

    protected void openStream() {
        if ( debug ) System.out.print("o");
        try {
	        if ( ais != null ) ais.close();
    	    if ( file == null ) {
        	    ais = null;
                if ( useThreads ) {
                	puller.flush();
                }
	        } else {
			    ais = AudioSystem.getAudioInputStream(file);
	        	// our format has the resolution of the file
   			    // but the sample rate of the audio buffer
	       		format = new ProcessFormat(ais.getFormat()); // !!! !!! !!! !!! new!!!
                switch ( format.getChannels() ) {
                case 1: channelFormat = ChannelFormat.MONO; break;
                case 2: channelFormat = ChannelFormat.STEREO; break;
                default: channelFormat = null; break;
                }
    		    if ( useThreads ) {
                    puller.start();
    			}
    	    }
        } catch (javax.sound.sampled.UnsupportedAudioFileException uafe) {
            uafe.printStackTrace();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
        if ( debug ) System.out.print("O");
    }

    public void setFile(File file, String name) {
        this.file = file;
		close();
        metaInfo = new AudioBuffer.MetaInfo(name);
    }

    public void open() {}

    public void close() {
        try {
	        if ( ais != null ) {
		        if ( debug ) System.out.print("C");
    	        ais.close();
                ais = null;
        	}
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
        if ( useThreads ) {
            puller.flush();
        }
    }

    protected void attachMetaInfo(AudioBuffer buffer) {
        buffer.setMetaInfo(metaInfo); // tags buffer with source label
    }

    public void locate(long microseconds) {
        if ( file == null ) return;
        // we ignore microseconds, we can only locate zero !!!
        if ( debug ) System.out.print("l");
        close();
        openStream();
        if ( debug ) System.out.print("L");
    }

    protected void handleEndOfTrack() {
        if ( loop ) {
            openStream();
        } else {
            close();
        }
    }

    public int processAudio(AudioBuffer buffer) {
        if ( ais == null ) {
            return AUDIO_DISCONNECT;
        }
        attachMetaInfo(buffer);
        buffer.setChannelFormat(channelFormat);
        try {
            // pretend the file is at our sample rate
            format.setSampleRate(buffer.getSampleRate());
	        int nbytes = buffer.getByteArrayBufferSize(format);
            if ( byteBuffer.length < nbytes ) {
                byteBuffer = new byte[nbytes];
            }
            int len;
//            long tstart = System.nanoTime();
			if ( useThreads ) {
   	    		len = circularBuffer.read(byteBuffer, 0, nbytes);
            } else {
    	    	len = ais.read(byteBuffer, 0, nbytes);
            }
/*            if ( debug ) {
	            long tstop = System.nanoTime();
    	        float millis = (tstop - tstart) / 1000000f;
        	    if ( millis > 5 ) {
            	    System.out.println("read blocked "+(int)millis+ "ms");
            	}
            } */
            if ( len < 0 ) {
               	handleEndOfTrack();
	            buffer.makeSilence();
            } else if ( len == nbytes ) {
	        	// convert byte buffer to audio buffer
    	    	buffer.initFromByteArray(byteBuffer, 0, nbytes, format);
            } else {
                // wrong but we only lose the last few ms !!!
               	handleEndOfTrack();
                buffer.makeSilence();
            }
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
        return AUDIO_OK;
    }

    // 24 primes to randomise buffer lengths
/*    private static int[] primes = {
    	2003, 2027, 2053, 2069,
        2087, 2111, 2131, 2153,
        2179, 2203, 2221, 2237,
        2251, 2269,	2287, 2309,
		2333, 2347, 2371, 2389,
		2411, 2437, 2459, 2477
    }; */

        private static int[] primes = {
            4129,      4133,      4139,      4153,
			4157,      4159,      4177,      4201,
			4211,      4217,      4219,      4229,
			4231,      4241,      4243,      4253,
			4259,      4261,      4271,      4273,
			4283,      4289,      4297,      4327};

    protected class Puller implements Runnable
    {
        private Thread thread;
        private byte[] tempBuffer;
        private boolean interrupted = false;

        public Puller() {
            // the temp buffer size is related by prime numbers
            int n = (int)(24 * Math.random());
            // but multiplied by 4 for 16 stereo frame size !!! !!!
            tempBuffer = new byte[4 * primes[n]];
        }

        protected void flush(byte[] toBuffer) {
            if ( circularBuffer.availableRead() > 0 ) {
	            int flushed = 0;
	            while ( circularBuffer.availableRead() > 0 ) {
    	            flushed += circularBuffer.read(toBuffer, 0,
        	            Math.min(circularBuffer.availableRead(), toBuffer.length));
            	}
	            if ( debug ) System.out.print("F"+flushed);
            }
        }

        public void flush() {
            interrupted = true;
            byte[] flushBuffer = new byte[tempBuffer.length];
            // we flush in case the thread is blocked
            flush(flushBuffer);
            // if there's a thread we need to wait for it to stop
            // then we flush again because the thread may have written
            // to the circular buffer
            if ( thread != null && thread.isAlive() ) {
                try {
                    thread.join(50);
                } catch ( InterruptedException ie ) {
                    System.out.println("join timed out");
                }
	            if ( debug ) System.out.print("J");
                flush(flushBuffer);
            }
        }

        public void start() {
           	thread = new Thread(threadGroup, this);
           	thread.setPriority(Thread.MAX_PRIORITY-2);
            if ( debug ) System.out.print("S");
           	thread.start();
        }

        public void run() {
            interrupted = false;
            int len = 1;
            if ( debug ) System.out.print("\nr");
            while ( ais != null && len >= 0 && !interrupted ) {
                try {
                    // file read may block for a little time
	    	    	len = ais.read(tempBuffer, 0, tempBuffer.length);
                    // blocks here when stopped when circular buffer is full
                    if ( len > 0 ) {
   	            		circularBuffer.write(tempBuffer, 0, len);
                    }
                    if ( debug && interrupted ) System.out.print("i"+len);
                } catch ( IOException ioe ) {
                    ioe.printStackTrace();
                }
            }
            // write some silence to allow meters to decay
            Arrays.fill(tempBuffer, (byte)0);
			for ( int i = 0; i < 100; i++ ) { // should calc for a time !!! !!!
                circularBuffer.write(tempBuffer);
            }
            circularBuffer.close();
            interrupted = false;
            if ( debug ) System.out.print("R"+circularBuffer.availableRead());
        }
    }

    // just for mutating sample rate !!!
    static protected class ProcessFormat extends AudioFormat
    {
        public ProcessFormat(AudioFormat f) {
            super(f.getSampleRate(),
                f.getSampleSizeInBits(),
                f.getChannels(),
                true,
                f.isBigEndian()
                );
        }

        public void setSampleRate(float rate) {
            sampleRate = rate;
        }
    }
}
