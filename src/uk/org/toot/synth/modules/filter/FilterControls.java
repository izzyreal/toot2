package uk.org.toot.synth.modules.filter;

import static uk.org.toot.misc.Localisation.getString;
import static uk.org.toot.synth.modules.filter.FilterControlIds.*;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;

public class FilterControls extends CompoundControl 
	implements FilterVariables
{
	private FloatControl cutoffControl;
	private FloatControl resonanceControl;
	
	private float cutoff, resonance;
	
	protected int idOffset = 0;
	
	private int sampleRate = 44100;
	
	public FilterControls(int id, int instanceIndex, String name, final int idOffset) {
		super(id, instanceIndex, name);
		this.idOffset = idOffset;
		createControls();
		deriveSampleRateIndependentVariables();
		deriveSampleRateDependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
//				if (c.isIndicator()) return;
				deriveControl(c.getId()-idOffset);
			}
		});
	}

	protected void deriveControl(int id) {
		switch ( id ) {
		case FREQUENCY: cutoff = deriveCutoff(); break;
		case RESONANCE: resonance = deriveResonance(); break;
		}		
	}
	
	protected void createControls() {
		add(cutoffControl = createCutoffControl());
		add(resonanceControl = createResonanceControl());
	}

	protected void deriveSampleRateIndependentVariables() {
		resonance = deriveResonance();
		cutoff = deriveCutoff();
	}

	protected float deriveResonance() {
		return resonanceControl.getValue();
	}

	protected void deriveSampleRateDependentVariables() {
	}

	protected float deriveCutoff() {
		return cutoffControl.getValue();
	}

	protected FloatControl createCutoffControl() {
        ControlLaw law = new LinearLaw(-48, 96, "semitones");
        FloatControl control = new FloatControl(FREQUENCY+idOffset, getString("Cutoff"), law, 1f, 0f);
        control.setInsertColor(Color.yellow);
        return control;		
	}

	protected FloatControl createResonanceControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(RESONANCE+idOffset, getString("Resonance"), law, 0.01f, 0.25f);
        control.setInsertColor(Color.orange);
        return control;				
	}

	public float getCutoff() {
		return cutoff;
	}

	public float getResonance() {
		return resonance;
	}

	public void setSampleRate(int rate) {
		if ( sampleRate != rate ) {
			sampleRate = rate;
			deriveSampleRateDependentVariables();
		}
	}

}
