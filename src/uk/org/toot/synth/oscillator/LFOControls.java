package uk.org.toot.synth.oscillator;

import static uk.org.toot.localisation.Localisation.getString;

import java.awt.Color;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.EnumControl;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.control.LogLaw;

public class LFOControls extends CompoundControl implements LFOVariables
{
	public final static int FREQUENCY = 0;
	public final static int DEVIATION = 1;
	public final static int SHAPE = 2;

	private FloatControl frequencyControl;
	private FloatControl deviationControl;
	private EnumControl shapeControl;
	
	protected int idOffset;
	
	private float frequency, deviation;
	private boolean sine;

	protected LFOConfig config;
	
	protected LFOControls(int instanceIndex, String name, int idOffset, LFOConfig cfg) {
		this(OscillatorIds.LFO_ID, instanceIndex, name, idOffset, cfg);
	}

	public LFOControls(int id, int instanceIndex, String name, final int idOffset, LFOConfig cfg) {
		super(id, name);
		this.idOffset = idOffset;
		config = cfg;
		createControls();
		deriveSampleRateIndependentVariables();
		deriveSampleRateDependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
				deriveControl(c.getId()-idOffset);
			}
		});
		
	}

	protected void deriveControl(int id) {
		switch ( id ) {
		case FREQUENCY:	frequency = deriveFrequency(); 	break;
		case DEVIATION: deviation = deriveDeviation(); 	break;
		case SHAPE: 	sine = deriveShape();	 		break;
		}		
	}
	
	protected void createControls() {
		add(shapeControl = createShapeControl());
		add(frequencyControl = createFrequencyControl());
		if ( config.deviation > 0 ) {
			add(deviationControl = createDeviationControl());
		}
	}

	protected EnumControl createShapeControl() {
		final List<String> shapeNames = new java.util.ArrayList<String>();
		shapeNames.add("Sine");
		shapeNames.add("Triangle");
		return new EnumControl(SHAPE+idOffset, "Shape", "Sine") {
			public List getValues() {
				return shapeNames;
			}
		};
	}

	protected FloatControl createFrequencyControl() {
        ControlLaw law = new LogLaw(config.rateMin, config.rateMax, "Hz");
        FloatControl control = 
        	new FloatControl(FREQUENCY+idOffset, getString("Frequency"), law, 0.1f, config.rate);
        control.setInsertColor(Color.YELLOW);
        return control;				
	}

	
	protected FloatControl createDeviationControl() {
        ControlLaw law = new LinearLaw(0f, config.deviationMax, "Hz");
        FloatControl control = 
        	new FloatControl(FREQUENCY+idOffset, getString("Deviation"), law, 0.1f, config.deviation);
        control.setInsertColor(Color.YELLOW.darker());
        return control;				
	}

	protected void deriveSampleRateIndependentVariables() {
		frequency = deriveFrequency();
		deviation = deriveDeviation();
		sine = deriveShape();
	}
	
	protected void deriveSampleRateDependentVariables() {
	}
	
	protected float deriveFrequency() {
		return frequencyControl.getValue();
	}
	
	protected float deriveDeviation() {
		if ( deviationControl == null ) return 0f;
		return deviationControl.getValue();
	}

	protected boolean deriveShape() {
		return shapeControl.getValue().equals("Sine");
	}
	
	public float getFrequency() {
		return frequency;
	}

	public float getDeviation() {
		return deviation;
	}

	public boolean isSine() {
		return sine;
	}

}
