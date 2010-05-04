// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.eq;

import java.util.List;
import uk.org.toot.control.*;
import uk.org.toot.dsp.filter.FilterShape;
import uk.org.toot.audio.filter.*;

import static uk.org.toot.misc.Localisation.*;

/**
 * ClassicFilterControls are controls for the type, level, frequency and
 * resonance factor of a classic filter section and are used to control
 * all EQ forms. Particular controls may be hidden if their value is
 * immutable or otherwise not required.
 */
public class ClassicFilterControls extends CompoundControl
    implements FilterSpecification
    {
	private final static int LEVEL_ID = 0;
	private final static int FREQ_ID = 1;
	private final static int RES_ID = 2;
	
    private FilterShape shape;

    /**
     * @supplierCardinality 1
     * @link aggregationByValue 
     */
    private FloatControl leveldBControl;

    /**
     * @supplierCardinality 1
     * @link aggregationByValue 
     */
    private FloatControl freqControl;

    /**
     * @supplierCardinality 1
     * @link aggregationByValue 
     */
    private FloatControl resControl;

    private int idOffset;
    
    private int freq;
    private float leveldB, res, levelFactor;
    
    protected float minQ;
    protected float deltaQ;
    
    /**
     * Construct with all specified values.
     */
	public ClassicFilterControls(String name, int idOffset,
        FilterShape shape, boolean typefixed,
        float fmin, float fmax, float fvalue, boolean ffixed,
        ControlLaw qLaw, float qvalue, boolean qfixed,
        ControlLaw levelLaw, float dBvalue, boolean dBfixed
        ) {
        super(0, name); // ??? ???
        this.shape = shape;
        this.idOffset = idOffset;
        float maxLevel = (float)(Math.pow(10.0, levelLaw.getMaximum()/20.0));
        minQ = qLaw.getMinimum();
        deltaQ = (qLaw.getMaximum() - minQ) / (maxLevel - 1);
        add(resControl = createResonanceControl(idOffset+RES_ID, qLaw, qvalue, qfixed));
        add(freqControl = createFrequencyControl(idOffset+FREQ_ID, fmin, fmax, fvalue, ffixed));
        add(leveldBControl = createLevelControl(idOffset+LEVEL_ID, levelLaw, dBvalue, dBfixed));
        derive(resControl);
        derive(freqControl);
        derive(leveldBControl);
    }

	@Override
	protected void derive(Control c) {
		switch ( c.getId() - idOffset ) {
		case LEVEL_ID: 
			leveldB = leveldBControl.getValue();
			levelFactor = (float)(Math.pow(10.0, getLeveldB()/20.0));
            if ( isProportionalQ() ) {
                res = calculateProportionalQ(levelFactor);
            }
			break;
		case FREQ_ID: freq = (int)freqControl.getValue(); break;
		case RES_ID: res = resControl.getValue(); break;
		}
	}
	
    public boolean isAlwaysVertical() { return true; }

    public FilterShape getShape() {
        return shape;
    }

    public int getFrequency() {
        return freq;
    }

    public float getResonance() {
        return res;
    }

   public float getLeveldB() {
        return leveldB;
    }

    public float getLevelFactor() {
		return levelFactor;
    }

    public boolean is4thOrder() { return false; }

    protected boolean isProportionalQ() { return false; }
    
    // 1 .. maxLevel -> minQ .. maxQ
    protected float calculateProportionalQ(float level) {
        if ( level < 1 ) level = 1f / level;
        return minQ + (level - 1) * deltaQ;
    }
    
    /**
     * A TypeControl concretizes EnumControl with filter types.
     */
    static public class TypeControl extends EnumControl
    {
    	public TypeControl(int id, Object value, boolean fixed) {
            super(id, "Type", value);
            setHidden(fixed);
    	}

    	public List getValues() {
        	return null;
    	}
    }

	protected FloatControl createFrequencyControl(int id, float min, float max, float initial, boolean fixed) {
        ControlLaw law = new LogLaw(min, max, "Hz");
        FloatControl freq = new FloatControl(id, getString("Frequency"), law, 1f, initial);
        freq.setHidden(fixed);
        return freq;
    }

	protected FloatControl createLevelControl(int id, ControlLaw law, float initial, boolean fixed) {
        FloatControl lev = new FloatControl(id, getString("Level"), law, 0.1f, initial) {
            private /*static*/ String[] presetNames = { getString("Flat") };
            public boolean isRotary() {
                // nominally a slider for Graphic EQ Controls
                // (UI decides based on context, eg. axis)
                return !(getParent().getParent() instanceof GraphicEQ.Controls);
            }
            public String[] getPresetNames() { return presetNames; }
            public void applyPreset(String name) {
                if ( getString("Flat").equals(name) ) {
                    setValue(0f);
                }
            }
        };
        lev.setHidden(fixed);
        return lev;
    }

	protected FloatControl createResonanceControl(int id, ControlLaw law, float initial, boolean fixed) {
        FloatControl lev = new FloatControl(id, getString("Resonance"), law, 0.1f, initial);
        lev.setHidden(fixed);
        return lev;
    }
	
}
