// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.eq;

import java.awt.Color;
import java.util.List;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.filter.ToneStackDesigner;
import uk.org.toot.audio.filter.ToneStackSection;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.EnumControl;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;

import static uk.org.toot.audio.eq.EQIds.TONE_STACK_EQ_ID;

public class ToneStackEQControls extends AudioControls 
	implements ToneStackEQProcess.Variables
{
	private static final int BASS = 0;
	private static final int MIDDLE = 1;
	private static final int TREBLE = 2;
    private static final int TYPE = 3;  // which components
	
	private static final ControlLaw TEN_LAW = new LinearLaw(0, 10f, "");

	private FloatControl bassControl, midControl, trebleControl;
    private EnumControl typeControl;
	
	private float b = 0.5f, m = 0.5f, t = 0.5f;
	private float fs = 44100f;
	private boolean changed = false;

	private ToneStackDesigner stack = new ToneStackDesigner();
	private ToneStackSection.Coefficients coeffs;
	
	public ToneStackEQControls() {
		super(TONE_STACK_EQ_ID, "ToneStack");
		ControlColumn col = new ControlColumn();
        col.add(typeControl = new TypeControl("Type"));
		col.add(trebleControl = createControl(TREBLE, "Treble"));
		col.add(midControl = createControl(MIDDLE, "Mid"));
		col.add(bassControl = createControl(BASS, "Bass"));
		add(col);
        stack.setComponents((ToneStackDesigner.Components)typeControl.getValue());
	}
	
	protected FloatControl createControl(int id, String name) {
		FloatControl control = new FloatControl(id, name, TEN_LAW, 0.01f, 0f);
		control.setInsertColor(Color.WHITE);
		return control;
	}
	
    @Override
    protected void derive(Control c) {
    	switch ( c.getId() ) {
    	case BASS: b = taper(bassControl.getValue()/10, 2.3f); break;
    	case MIDDLE: m = midControl.getValue() / 10; break;
    	case TREBLE: t = taper(trebleControl.getValue()/10, 2.3f); break;
        case TYPE: stack.setComponents((ToneStackDesigner.Components)typeControl.getValue()); break;
    	default: return;
    	}
    	coeffs = design();
    	changed = true;
    }
    
    // a power of 2.3 to 2.4 gives best match to log pot taper
    // 0.5 should return about 0.18
    // slope at zero is low compared to real pot taper
    protected float taper(float val, float power) {
        return (float)Math.pow(val, power);
    }
    
    protected ToneStackSection.Coefficients design() {
    	return stack.design(b, m, t, fs);    	
    }
    
    public ToneStackSection.Coefficients setSampleRate(float rate) {
    	fs = rate;
    	changed = false; // race condition !!!
    	return design();
    }
    
    public boolean hasChanged() {
    	return changed;
    }
    
    public ToneStackSection.Coefficients getCoefficients() {
    	changed = false; // race condition !!!
    	return coeffs;
    }
    
    protected static class TypeControl extends EnumControl
    {
        private static List<ToneStackDesigner.Components> values =
            new java.util.ArrayList<ToneStackDesigner.Components>();
        
        static {
            values.add(new ToneStackDesigner.Fender59BassmanComponents());
            values.add(new ToneStackDesigner.FenderComponents());
            values.add(new ToneStackDesigner.MarshallComponents());
        }
        
        public TypeControl(String name) {
            super(TYPE, name, values.get(0));
        }

        @Override
        public List getValues() {
            return values;
        }
        
    }
}
