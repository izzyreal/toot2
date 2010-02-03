// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.delay;

import java.util.List;
import java.awt.Color;
import uk.org.toot.control.*;
import static uk.org.toot.misc.Localisation.*;

public class ModulatedDelayControls extends AbstractDelayControls
    implements ModulatedDelayProcess.Variables
{
	private final static float HALF_ROOT_2 = 0.707f;
	
    private static final ControlLaw DELAY_LAW = new LogLaw(0.1f, 25f, "ms");
    private static final ControlLaw RATE_LAW = new LogLaw(0.02f, 7f, "Hz");

    private FloatControl dryControl;
    private FloatControl wetControl;
    private FloatControl delayControl;
    private BooleanControl tapeControl;
    private FloatControl rateControl;
    private ShapeControl shapeControl;
    private FloatControl depthControl;
    private BooleanControl phaseControl;
    private BooleanControl linkControl;


    private static final int DRY_ID = 0;
    private static final int DELAY_ID = 1;
//    private static final int TAPE_ID = 2;
    private static final int RATE_ID = 3;
    private static final int SHAPE_ID = 4;
    private static final int DEPTH_ID = 5;
    private static final int LINK_ID = 6;
    protected static final int PHASE_ID = 7;

    public ModulatedDelayControls() {
        this(DelayIds.MODULATED_DELAY_ID, getString("Modulated.Delay"));
    }

    public ModulatedDelayControls(int id, String name) {
        super(id, name);
        add(createControlColumn1());

        // shape, rate
        shapeControl = new ShapeControl(SHAPE_ID);
        rateControl = new FloatControl(RATE_ID, getString("Rate"), RATE_LAW, 0.01f, 0.2f);
        // link (internal), depth
        linkControl = new BooleanControl(LINK_ID, getString("Link"), false);
        linkControl.setStateColor(true, Color.LIGHT_GRAY);
        depthControl = new FloatControl(DEPTH_ID, getString("Depth"), LinearLaw.UNITY, 0.01f, 0.5f);
   		depthControl.setInsertColor(Color.lightGray);

        ControlColumn g2 = new ControlColumn();
        g2.add(shapeControl);
		g2.add(rateControl);
//        g2.add(linkControl);
        g2.add(depthControl);
        add(g2);

        // invert, feedback
        // invert, mix
        ControlColumn g3 = new ControlColumn();
        g3.add(createFeedbackInvertControl());
        g3.add(createFeedbackControl());
        wetControl = new FloatControl(MIX_ID, getString("Wet"), LinearLaw.UNITY, 0.1f, HALF_ROOT_2);
        wetControl.setInsertColor(Color.white);
        g3.add(wetControl);
        add(g3);
    }

    protected ControlColumn createControlColumn1() {
    	phaseControl = new BooleanControl(PHASE_ID, "PQ", false);
    	phaseControl.setStateColor(true, Color.YELLOW);
        // delay
        delayControl = new FloatControl(DELAY_ID, getString("Delay"), DELAY_LAW, 0.1f, 2f);
        dryControl = new FloatControl(DRY_ID, getString("Dry"), LinearLaw.UNITY, 0.1f, HALF_ROOT_2);
        dryControl.setInsertColor(Color.DARK_GRAY);

        ControlColumn g1 = new ControlColumn();
        g1.add(phaseControl);
        g1.add(tapeControl);
        g1.add(delayControl);
        g1.add(dryControl);
        return g1;
    }

    public float getMaxDelayMilliseconds() { return 60f; }

    public float getDelayMilliseconds() { return delayControl.getValue(); }

    public float getRate() {
        return rateControl.getValue();
    }

    public float getDepth() { return depthControl.getValue(); }

    public float getDry() { return dryControl.getValue(); }
    
    public float getWet() { return wetControl.getValue(); }
    
    // 0 SIN, 1 TRI
    public int getLFOShape() { return shapeControl.getIntValue(); }

    public boolean isPhaseQuadrature() { return phaseControl.getValue(); }
    
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
        
        public boolean hasLabel() { return true; }
    }
}
