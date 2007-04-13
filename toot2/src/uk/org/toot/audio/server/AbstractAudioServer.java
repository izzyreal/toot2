// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.server;

import uk.org.toot.audio.core.AudioBuffer;

/**
 * AbstractAudioServer implements AudioServer to control the timing of an
 * AudioClient.
 * The buffer size, latency and timing strategy may be varied while running.
 * Note that changing latency may cause inputs to glitch.
 * 
 * @author Steve Taylor
 * @author Peter Johan Salomonsen
 */
abstract public class AbstractAudioServer 
    implements Runnable, ExtendedAudioServer
{
    /**
     * a single client, use Composite pattern for multi client
     * @link aggregation
     * @supplierCardinality 0..1 
     */
    protected AudioClient client;
    protected boolean isRunning = false;
    protected boolean hasStopped = false;

    private static long ONE_MILLION = 1000000;

    private float bufferMilliseconds = 2f;
    private float requestedBufferMilliseconds = bufferMilliseconds; // for syncing

    private float latencyMilliseconds = 70;

    private float actualLatencyMilliseconds = 0;
    private float lowestLatencyMilliseconds = bufferMilliseconds;
    private float maximumJitterMilliseconds = 0;
    private int bufferUnderRuns = 0;
    private int bufferUnderRunThreshold = 0;

    private int outputLatencyFrames = 0;
    private int inputLatencyFrames = 0;
    private long totalTimeNanos;

    private boolean requestResetMetrics = false;

    /**
     * @link aggregation
     * @supplierCardinality 1 
     */
    private AudioTimingStrategy timingStrategy;

    /**
     * @supplierCardinality 0..1 */
    private AudioTimingStrategy requestedTimingStrategy;

    private float load = 0; // normalised load, 1 = 100% of available time
    private float peakLoad = 0;

	private Thread thread;

    /**
     * @link aggregation
     * @supplierCardinality 0..1 
     */
    protected AudioSyncLine syncLine;

    private boolean startASAP = false;

    protected boolean started = false;
    protected int stableCount = 0;
    protected int unstableCount = 0;

    protected int stableThreshold = 1000;
    protected int unstableThreshold = 3;

    public AbstractAudioServer() { //throws Exception {
        totalTimeNanos = (long)(bufferMilliseconds * ONE_MILLION);
        Runtime.getRuntime().addShutdownHook(
            new Thread() {
            	public void run() {
                	stop();
            	}
        	}
    	);
        // estimate buffer underrun threshold for os
        String osName = System.getProperty("os.name");
        if ( osName.contains("Windows") ) {
            // only correct for DirectSound !!!
            bufferUnderRunThreshold = 30;
            unstableThreshold = 100; // 100 consecutire underruns are OK
        }
        requestedTimingStrategy = new SleepTimingStrategy();
    }

    public void setClient(AudioClient client) {
        this.client = client;
        checkStart(); // start may be delayed waiting for a client to be set
    }

    abstract protected void work();

    protected void checkStart() {
        if ( startASAP ) {
			if ( canStart() ) {
            	startImpl();
            } else {
//                System.out.println("AudioServer start still delayed");
            }
        }
    }

    protected boolean canStart() {
        return client != null && syncLine != null;
    }

    public void start() {
        if ( isRunning ) return;
        if ( canStart() ) {
            startImpl();
        } else {
	       	System.out.println("AudioServer start requested but delayed");
            startASAP = true;
        }
    }

    protected void startImpl() {
        started = false;
        startASAP = false;
        stableCount = 0;
        unstableCount = 0;
       	System.out.println("AudioServer starting");
       	thread = new Thread(this, THREAD_NAME);
       	thread.start();
    }

    public void stop() {
        if ( !isRunning ) return;
        stopImpl();
        while (!hasStopped) {
            try {
	            Thread.sleep(10);
            } catch ( InterruptedException ie ) {
            }
        }
    }

    protected void stopImpl() {
       	System.out.println("AudioServer stopping");
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void run() {
        try {
            hasStopped = false;
            isRunning = true;
            long startTimeNanos;
			long endTimeNanos;
            long expiryTimeNanos = System.nanoTime(); // init required for jitter
            long compensationNanos = 0;
            float jitterMillis;
            float lowLatencyMillis;

            while (isRunning) {
                startTimeNanos = System.nanoTime();

                // calculate timing jitter
                jitterMillis = (float)(startTimeNanos - expiryTimeNanos) / ONE_MILLION;
                if ( jitterMillis > maximumJitterMilliseconds ) {
                    maximumJitterMilliseconds = jitterMillis;
                }

                sync(); // e.g. resize buffers if requested
                work();
                endTimeNanos = System.nanoTime();

                // calculate client load
                load = (float)(endTimeNanos - startTimeNanos) / totalTimeNanos;
                if ( load > peakLoad ) {
                    peakLoad = load;
                }

                // calculate actual latency
				outputLatencyFrames = syncLine.getLatencyFrames();
				actualLatencyMilliseconds = 1000 * outputLatencyFrames / getSampleRate();
                lowLatencyMillis = actualLatencyMilliseconds - bufferMilliseconds;
                if ( lowLatencyMillis < bufferUnderRunThreshold ) {
                    if ( started ) {
                    	bufferUnderRuns += 1;
                        unstableCount += 1;
                        checkControl();
                    	stableCount = 0;
                    }
                } else {
                    unstableCount = 0;
                    stableCount +=1;
                    if ( stableCount == stableThreshold ) { // !!! OK and every 49 days !!!
	                    started = true;
                        controlGained();
                    }
                }
                if ( lowLatencyMillis < lowestLatencyMilliseconds ) {
                    lowestLatencyMilliseconds = lowLatencyMillis;
                }
				if ( stableCount == 0 ) continue; // fast control stabilisation

                // calculate the latency control loop
                compensationNanos = (long)(ONE_MILLION * (actualLatencyMilliseconds - latencyMilliseconds));
                expiryTimeNanos = startTimeNanos + totalTimeNanos + compensationNanos;

                // block
                long now = System.nanoTime();
                long sleepNanos = expiryTimeNanos - now;
                // never block for more than 20ms
                if ( sleepNanos > 20000000 ) {
                    sleepNanos = 20000000;
                    expiryTimeNanos = now + sleepNanos;
                }
                if ( sleepNanos > 500000 ) {
                    timingStrategy.block(now, sleepNanos);
                } else {
                    expiryTimeNanos = now;
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        hasStopped = true;
//        System.out.println("Thread stopped");
    }

    protected void sync() {
        if ( bufferMilliseconds != requestedBufferMilliseconds ) {
            bufferMilliseconds = requestedBufferMilliseconds;
            totalTimeNanos = (long)(bufferMilliseconds * 1000000);
            resizeBuffers();
        }
        if ( requestedTimingStrategy != null ) {
             timingStrategy = requestedTimingStrategy;
             thread.setPriority(timingStrategy.getThreadPriority());
             requestedTimingStrategy = null;
        }
        if ( requestResetMetrics ) {
            reset();
            requestResetMetrics = false;
        }
    }

    protected void controlGained() {
        resetMetrics();
    }

    protected void checkControl() {
        if ( unstableCount > unstableThreshold ) {
            System.out.println("AudioServer Control Lost");
            stopImpl();
        }
    }

    public void resetMetrics() {
        requestResetMetrics = true;
    }

    protected void reset() {
        lowestLatencyMilliseconds = actualLatencyMilliseconds;
        maximumJitterMilliseconds = 0;
//        bufferUnderRuns = 0;
        peakLoad = 0;
    }

    public void setLatencyMilliseconds(float ms) {
        latencyMilliseconds = ms;
        // reset other metrics synchronously
        resetMetrics();
    }

    public float getLatencyMilliseconds() {
        return latencyMilliseconds;
    }

    public float getActualLatencyMilliseconds() {
        return actualLatencyMilliseconds;
    }

    /**
     * Because latency is measured just after writing a buffer and represents
     * the maximum latency, the lowest latency has to be compensated by the
     * duration of the buffer.
     * This might not be the best place to do the compensation but it is the
     * cheapest. While bufferMilliseconds is effectively immutable it's ok.
     */
    public float getLowestLatencyMilliseconds() {
        return lowestLatencyMilliseconds;
    }

    public float getMinimumLatencyMilliseconds() {
        return bufferUnderRunThreshold + 5f;
    }

    public float getMaximumJitterMilliseconds() {
        return maximumJitterMilliseconds;
    }

    public int getBufferUnderRuns() {
        return bufferUnderRuns;
    }

    public float getLoad() {
        return load;
    }

    public float getPeakLoad() {
        return peakLoad;
    }

    public float getBufferMilliseconds() {
        return bufferMilliseconds;
    }

    public void setBufferMilliseconds(float ms) {
        requestedBufferMilliseconds = ms;
    }

    public void setTimingStrategy(AudioTimingStrategy strategy) {
        requestedTimingStrategy = strategy;
    }

    protected abstract void resizeBuffers();

    public int getOutputLatencyFrames() {
        return outputLatencyFrames;
    }

    public int getInputLatencyFrames(){
        return inputLatencyFrames;
    }

}
