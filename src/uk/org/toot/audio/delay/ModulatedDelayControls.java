// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import java.util.List;
import java.awt.Color;
import uk.org.toot.control.*;
import static uk.org.toot.localisation.Localisation.*;

public class ModulatedDelayControls extends AbstractDelayControls
    implements ModulatedDelayVariables
{
    private static final ControlLaw delayLaw = new LogLaw(0.1f, 25f, "ms");
    private static final ControlLaw rateLaw = new LogLaw(0.02f, 2f, "Hz");
// !!! abstract?
//    private static final ControlLaw filterFreqLaw = new LogLaw(100f, 10000f, "Hz");
    private FloatControl delayControl;
    private BooleanControl tapeControl;
    private FloatControl rateControl;
    private ShapeControl shapeControl;
    private FloatControl depthControl;
    private BooleanControl linkControl;
//    private FloatControl filterFrequencyControl;
//    private FilterTypeControl filterTypeControl;

    private static final int DELAY_ID = 1;
    private static final int TAPE_ID = 2;
    private static final int RATE_ID = 3;
    private static final int SHAPE_ID = 4;
    private static final int DEPTH_ID = 5;
    private static final int LINK_ID = 6;
    protected static final int PHASE_ID = 7; // !!!

    public ModulatedDelayControls() {
        this(DelayIds.MODULATED_DELAY_ID, getString("Modulated.Delay"));
    }

    public ModulatedDelayControls(int id, String name) {
        super(id, name);
        add(createControlColumn1());

        // shape, rate
        shapeControl = new ShapeControl(SHAPE_ID);
        rateControl = new FloatControl(RATE_ID, getString("Rate"), rateLaw, 0.01f, 0.2f);
        rateControl.setInsertColor(Color.magenta.darker());
        // link (internal), depth
        linkControl = new BooleanControl(LINK_ID, getString("Link"), false);
        linkControl.setStateColor(true, Color.LIGHT_GRAY);
        depthControl = new FloatControl(DEPTH_ID, getString("Depth"), UNITY_LIN_LAW, 0.01f, 0.5f);
   		depthControl.setInsertColor(Color.lightGray);

        ControlColumn g2 = new ControlColumn();
        g2.add(shapeControl);
		g2.add(rateControl);
//        g2.add(linkControl);
        g2.add(depthControl);
        add(g2);

        // type, frequency
//		filterTypeControl = new FilterTypeControl();
//        filterFrequencyControl = new FloatControl(new Type("Frequency"), filterFreqLaw, 1f, 1000f);
//        filterFrequencyControl.setInsertColor(Color.yellow);
//        add(new ControlGroup(filterTypeControl, filterFrequencyControl));

        // invert, feedback
        // invert, mix
        add(createCommonControlColumn(true)); // no inverts
    }

    protected ControlColumn createControlColumn1() {
        // tape, delay
        tapeControl = new BooleanControl(TAPE_ID, getString("Tape"), false);
        tapeControl.setStateColor(true, Color.pink);
        delayControl = new FloatControl(DELAY_ID, getString("Delay"), delayLaw, 0.1f, 2f);
        delayControl.setInsertColor(Color.red.darker());

        ControlColumn g1 = new ControlColumn();
        g1.add(tapeControl);
        g1.add(delayControl);
        return g1;
    }

    public float getMaxDelayMilliseconds() { return 60f; }

    public float getDelayMilliseconds() { return delayControl.getValue(); }

    public float getRate() {
        // tape mode effectively full wave rectifies the effect of the
        // modulation in the frequency domain and consequently sounds
        // as if it's modulated at twice the rate, so then we divide by 2
        return isTape() ? rateControl.getValue()/2 : rateControl.getValue();
    }

    public float getDepth() { return depthControl.getValue(); }

    public float getFilterFrequency(){ return 0; }

    public boolean isTape() { return tapeControl.getValue(); }

    // 0 SIN, 1 TRI
    public int getLFOShape() { return shapeControl.getIntValue(); }

    public int getFilterType(){ return 0; }

    public boolean canBypass() { return true; }

    public static class ShapeControl extends EnumControl
    {
        private static List<Object> values;

        static {
            values = new java.util.ArrayList<Object>();
            values.add("Sin");
            values.add("Tri");
        }

        public ShapeControl(int id) {
            super(id, "Shape", values.get(0));
        }

        public List<Object> getValues() { return values; }
    }

/*
    public static class FilterTypeControl extends EnumControl
    {
        private static List<Object> values;

        static {
            values = new java.util.ArrayList<Object>();
            values.add("HP");
            values.add("LP");
        }

        public FilterTypeControl() {
            super(new Type("Type"), values.get(0));
        }

        public List<Object> getValues() { return values; }
    } */
}
