// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.audioui.serverui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uk.org.toot.swing.SpringUtilities;
import uk.org.toot.audio.server.*;
import uk.org.toot.swing.DisposablePanel;
import java.lang.management.*;
import java.util.List;
import java.util.Date;
import java.text.DateFormat;

/**
 * An AudioServerPanel provides a UI for an AudioServer which allows control
 * of internal buffer time and latency time and monitors actual latency.
 * The panel polls the values at periodic intervals.
 */
public class AudioServerPanel extends DisposablePanel implements ActionListener
{
    private ExtendedAudioServer server;
    private int periodMilliseconds = 2000;

    private JSpinner bufferMillis;
    private JSpinner latencyMillis;
    private JLabel actualLatencyMillis, lowestLatencyMillis, maxJitterMillis, bufferUnderRuns;
    private JLabel loadTimePercent, peakLoadTimePercent;
    private JLabel cpuPercent, userPercent, blockedPercent, waitedPercent;
    private JLabel gc1Count, gc1Millis, gc2Count, gc2Millis;

    private List<JLabel> outputLatencyLabels = new java.util.ArrayList<JLabel>();
    private List<JLabel> inputLatencyLabels = new java.util.ArrayList<JLabel>();

    private long prevNanos = 0;
    private long prevCpuNanos = 0;
    private long prevUserNanos = 0;
    private long prevBlockedMillis = 0;
    private long prevWaitedMillis = 0;

    private boolean eachIOlatency = true;
    private boolean gcAccounting = false; // broken

    private int underRunCount = 0;

    private Timer timer;

    private DateFormat shortTime;

    private static long id = -1;
    private static ThreadMXBean mxbean;
    private static boolean hasCpuTime = false;
    private static boolean hasContentionMonitoring = false;
/*
    static {
        // set up ThreadMXBean and derived variables
        mxbean = ManagementFactory.getThreadMXBean();
        if ( mxbean.isThreadCpuTimeSupported() ) {
            if ( !mxbean.isThreadCpuTimeEnabled() ) {
                mxbean.setThreadCpuTimeEnabled(true);
            }
            hasCpuTime = true;
        }

        if ( mxbean.isThreadContentionMonitoringSupported() ) {
            if ( !mxbean.isThreadContentionMonitoringEnabled() ) {
                 mxbean.setThreadContentionMonitoringEnabled(true);
            }
            hasContentionMonitoring = true;
        }
    }
*/

    public AudioServerPanel(final ExtendedAudioServer server) {
        this.server = server;
        shortTime = DateFormat.getTimeInstance(DateFormat.SHORT);
        setLayout(new BorderLayout());
        add(buildManagementPanel(), BorderLayout.WEST);
//        add(buildLogPanel, BorderLayout.CENTER);
  		timer = new Timer(periodMilliseconds, this);
        timer.start();
    }

    protected void dispose() {
        timer.stop();
        removeAll();
        mxbean = null;
        server = null;
    }

  	public void actionPerformed(ActionEvent evt) {
        if ( server == null ) return;
        if ( !isShowing() ) return;
  		actualLatencyMillis.setText(dpString(server.getActualLatencyMilliseconds(), 1));
  		lowestLatencyMillis.setText(dpString(server.getLowestLatencyMilliseconds(), 1));
        maxJitterMillis.setText(dpString(server.getMaximumJitterMilliseconds(), 1));
        int underRuns = server.getBufferUnderRuns();
        bufferUnderRuns.setText(String.valueOf(underRuns));
		loadTimePercent.setText(String.valueOf(Math.round(100 * server.getLoad())));
        peakLoadTimePercent.setText(String.valueOf(Math.round(100 * server.getPeakLoad())));

      	if ( underRuns != underRunCount ) {
			String time = shortTime.format(new Date());
        	System.err.println(time+" UnderRun "+underRuns+
                ", L="+dpString(server.getLowestLatencyMilliseconds(), 1)+
                "ms, J="+dpString(server.getMaximumJitterMilliseconds(), 1)+
                "ms, T="+String.valueOf(Math.round(100 * server.getPeakLoad()))+"%");
        	underRunCount = underRuns;
            // reset the metrics !!! !!!
            server.resetMetrics();
      	}

        if ( eachIOlatency ) {
        	for ( int i = 0; i < outputLatencyLabels.size(); i++ ) {
            	float latencyMillis = 1000 * server.getOutputs().get(i).getLatencyFrames() / server.getSampleRate();
	            outputLatencyLabels.get(i).setText(dpString(latencyMillis, 1));
    		}

	        for ( int i = 0; i < inputLatencyLabels.size(); i++ ) {
    	        float latencyMillis = 1000 * server.getInputs().get(i).getLatencyFrames() / server.getSampleRate();
        	    inputLatencyLabels.get(i).setText(dpString(latencyMillis, 1));
        	}
        }

        if ( mxbean == null ) return;

        if ( id < 0 ) {
        	id = getThreadId(AudioServer.THREAD_NAME);
        }

        if ( id < 0 ) return;

        // calculate elapsed time
        long nanos = System.nanoTime();
        long elapsed = nanos - prevNanos;
        prevNanos = nanos;

        if ( hasCpuTime ) {
	        // load average
    	    long cpuNanos = mxbean.getThreadCpuTime(id);
        	cpuPercent.setText(String.valueOf(Math.round(100 * (cpuNanos - prevCpuNanos) / elapsed)));
        	prevCpuNanos = cpuNanos;

	        // user average
    	    long userNanos = mxbean.getThreadUserTime(id);
        	userPercent.setText(String.valueOf(Math.round(100 * (userNanos - prevUserNanos) / elapsed)));
	        prevUserNanos = userNanos;
        }

        if ( hasContentionMonitoring ) {
	        // derive thread information
    	    ThreadInfo info = mxbean.getThreadInfo(id);
        	if ( info == null ) return;

	        // convert elapsed from nanos to millis
			elapsed /= 1000000;

        	// blocked average
	        long blockedMillis = info.getBlockedTime();
    	    blockedPercent.setText(String.valueOf(Math.round(100 * (blockedMillis - prevBlockedMillis) / elapsed)));
        	prevBlockedMillis = blockedMillis;

	        // waited average
    	    long waitedMillis = info.getWaitedTime();
        	waitedPercent.setText(String.valueOf(Math.round(100 * (waitedMillis - prevWaitedMillis) / elapsed)));
	        prevWaitedMillis = waitedMillis;
        }
        if ( gcAccounting ) {
	        // garbage collection
    	    List<GarbageCollectorMXBean> gcbeans = ManagementFactory.getGarbageCollectorMXBeans();
        	int ngc = gcbeans.size();
	        GarbageCollectorMXBean gcbean;
    	    if ( ngc > 0 ) {
    	        gcbean = gcbeans.get(0);
	            gc1Count.setText(String.valueOf(gcbean.getCollectionCount()));
            	gc1Millis.setText(String.valueOf(gcbean.getCollectionTime()));
        	}
        	if ( ngc > 1 ) {
            	gcbean = gcbeans.get(1);
	            gc1Count.setText(String.valueOf(gcbean.getCollectionCount()));
    	        gc1Millis.setText(String.valueOf(gcbean.getCollectionTime()));
        	}
        }
	}

    protected String dpString(float ms, int dp) {
        return String.format("%1$."+dp+"f", ms);
    }

	protected static long getThreadId(String threadName) {
        long[] ids = mxbean.getAllThreadIds();
        for ( int i = 0; i < ids.length; i++ ) {
            if ( mxbean.getThreadInfo(ids[i]).getThreadName().equals(threadName) ) {
                return ids[i];
            }
        }
        return -1;
    }

    protected void addRow(JPanel p, String label, JComponent comp, String units) {
   		JLabel l = new JLabel(label+" :", JLabel.TRAILING);
	    p.add(l);
	    l.setLabelFor(comp);
	    p.add(comp);
        l = new JLabel(units, JLabel.LEADING);
	    p.add(l);
	    l.setLabelFor(comp);
    }

    protected JPanel buildManagementPanel() {
		// Create and populate the panel.
		JPanel p = new JPanel(new SpringLayout());
        int nrows = 0;
        addRow(p, "Sample Rate", new JLabel(String.valueOf((int)server.getSampleRate()), JLabel.CENTER), "Hz");
        addRow(p, "Sample Size", new JLabel(String.valueOf(server.getSampleSizeInBits()), JLabel.CENTER), "bits");
        nrows += 2;
        final SpinnerNumberModel bufferModel =
            new SpinnerNumberModel((int)server.getBufferMilliseconds(), 1, 10, 1);
        bufferMillis = new MilliSpinner(bufferModel);
        bufferMillis.addChangeListener(
            new ChangeListener() {
            	public void stateChanged(ChangeEvent e) {
                	server.setBufferMilliseconds((float)bufferModel.getNumber().intValue());
            	}
        	}
        );
        final SpinnerNumberModel latencyModel =
            new SpinnerNumberModel((int)server.getLatencyMilliseconds(),
            	(int)server.getMinimumLatencyMilliseconds(), 250, 1);
        latencyMillis = new MilliSpinner(latencyModel);
        latencyMillis.addChangeListener(
            new ChangeListener() {
            	public void stateChanged(ChangeEvent e) {
                	server.setLatencyMilliseconds((float)latencyModel.getNumber().intValue());
            	}
        	}
        );
        actualLatencyMillis = new JLabel("n/a", JLabel.CENTER);
        lowestLatencyMillis = new JLabel("n/a", JLabel.CENTER);
        maxJitterMillis = new JLabel("n/a", JLabel.CENTER);
		bufferUnderRuns = new  JLabel("n/a", JLabel.CENTER);
        loadTimePercent = new JLabel("n/a", JLabel.CENTER);
        peakLoadTimePercent = new JLabel("n/a", JLabel.CENTER);

        addRow(p, "Internal Buffer", bufferMillis, "ms");
        addRow(p, "Requested Latency", latencyMillis, "ms");
        addRow(p, "Actual Latency", actualLatencyMillis, "ms");
        addRow(p, "Lowest Latency", lowestLatencyMillis, "ms");
        addRow(p, "Maximum Jitter", maxJitterMillis, "ms");
        addRow(p, "Buffer UnderRuns", bufferUnderRuns, "");
        addRow(p, "Time Load", loadTimePercent, "%");
        addRow(p, "Peak Time Load", peakLoadTimePercent, "%");
        nrows += 8;

        if ( eachIOlatency ) {
	        for ( int i = 0; i < server.getOutputs().size(); i++ ) {
    	        JLabel outputLatency = new JLabel("n/a", JLabel.CENTER);
        	    outputLatencyLabels.add(outputLatency);
        		addRow(p, server.getOutputs().get(i).getName()+" Latency", outputLatency, "ms");
	            nrows += 1;
    	    }

        	for ( int i = 0; i < server.getInputs().size(); i++ ) {
            	JLabel inputLatency = new JLabel("n/a", JLabel.CENTER);
	            inputLatencyLabels.add(inputLatency);
    	    	addRow(p, server.getInputs().get(i).getName()+" Latency", inputLatency, "ms");
        	    nrows += 1;
        	}
        }

        if ( hasCpuTime ) {
	        cpuPercent = new JLabel("n/a", JLabel.CENTER);
    	    userPercent = new JLabel("n/a", JLabel.CENTER);
        	addRow(p, "Thread CPU", cpuPercent, "%");
        	addRow(p, "Thread User", userPercent, "%");
            nrows += 2;
        }
        if ( hasContentionMonitoring ) {
	        blockedPercent = new JLabel("n/a", JLabel.CENTER);
    	    waitedPercent = new JLabel("n/a", JLabel.CENTER);
	        addRow(p, "Thread Blocked", blockedPercent, "%");
    	    addRow(p, "Thread Waited", waitedPercent, "%");
            nrows += 2;
        }

        if ( gcAccounting ) {
	        gc1Count = new JLabel("n/a", JLabel.CENTER);
    	    gc1Millis = new JLabel("n/a", JLabel.CENTER);
        	gc2Count = new JLabel("n/a", JLabel.CENTER);
	        gc2Millis = new JLabel("n/a", JLabel.CENTER);
    	    addRow(p, "GC [1] Count", gc1Count, "");
        	addRow(p, "GC [1] Time", gc1Millis, "ms");
	        addRow(p, "GC [2] Count", gc2Count, "");
    	    addRow(p, "GC [2] Time", gc2Millis, "ms");
        	nrows += 4;
        }

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(p,
                                nrows, 3, 		// rows, cols
                                6, 6,       // initX, initY
                                6, 6);      // xPad, yPad
        return p;
    }

    private static Dimension spinnerSize = new Dimension(50, 24);

    static protected class MilliSpinner extends JSpinner
    {
        public MilliSpinner(SpinnerModel model) {
            super(model);
        }

        public Dimension getMaximumSize() {
            return spinnerSize;
        }

        public Dimension getPreferredSize() {
            return spinnerSize;
        }
    }
}
