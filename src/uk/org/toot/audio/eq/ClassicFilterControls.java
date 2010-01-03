// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.eq;

import java.util.List;
import uk.org.toot.control.*;
import uk.org.toot.audio.core.*;
import uk.org.toot.audio.filter.*;

import static uk.org.toot.misc.Localisation.*;

/**
 * ClassicFilterControls are controls for the type, level, frequency and
 * resonance factor of a classic filter section and are used to control
 * all EQ forms. Particular controls may be hidden if their value is
 * immutable or otherwise not required.
 */
public class ClassicFilterControls extends AudioControls
    implements FilterSpecification
    {
    private Filter.Type type;

    /**
     * @supplierCardinality 1
     * @link aggregationByValue 
     */
    private FloatControl leveldB;

    /**
     * @supplierCardinality 1
     * @link aggregationByValue 
     */
    private FloatControl freq;

    /**
     * @supplierCardinality 1
     * @link aggregationByValue 
     */
    private FloatControl res;

    /**
     * Construct with all specified values.
     */
	public ClassicFilterControls(String name, int id,
        Filter.Type typevalue, boolean typefixed,
        float fmin, float fmax, float fvalue, boolean ffixed,
        ControlLaw qLaw, float qvalue, boolean qfixed,
        ControlLaw levelLaw, float dBvalue, boolean dBfixed
        ) {
        super(0, name); // ??? ???
        type = typevalue;
        add(res = createResonanceControl(id+2, qLaw, qvalue, qfixed));
        add(freq = createFrequencyControl(id+1, fmin, fmax, fvalue, ffixed));
        add(leveldB = createLevelControl(id, levelLaw, dBvalue, dBfixed));
    }

    public boolean isAlwaysVertical() { return true; }

    public Filter.Type getClassicType() {
        return type;
    }

    public int getFrequency() {
        return (int)freq.getValue();
    }

    public void setFrequency(int frequency) {
        freq.setValue(frequency);
    }

    public float getResonance() {
        return res.getValue();
    }

    public void setResonance(float q) {
        res.setValue(q);
    }

    /**
     * Set the level adjustment to be applied to filtered data
     * Values typically range from -.25 to +4.0 or -12 to +12 db.
     * dB = 20 * Math.log10(amplitudeAdj);
     * amplitudeAdj = 10^(dB/20);
     **/
    public void setLeveldB(float dBlevel) {
        leveldB.setValue(dBlevel);
    }

    public float getLeveldB() {
        return leveldB.getValue();
    }

    public float getLevelFactor() {
        // evaluate on another thread when leveldB changes !!!
		return (float)(Math.pow(10.0, getLeveldB()/20.0));
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
