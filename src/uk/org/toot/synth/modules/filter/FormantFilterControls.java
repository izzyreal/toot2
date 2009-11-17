package uk.org.toot.synth.modules.filter;

import static uk.org.toot.misc.Localisation.getString;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.control.LogLaw;

import static uk.org.toot.synth.modules.filter.FilterIds.FORMANT_FILTER_ID;

public class FormantFilterControls extends CompoundControl 
	implements FormantFilterVariables
{
	private final static int RESONANCE = 0;
	private final static int FREQSHIFT = 1;
	private final static int FREQUENCY = 2; // ..4..6..8 etc.
	private final static int LEVEL     = 3; // ..5..7..9 etc.
	
	private FloatControl frequencyControl[];
	private FloatControl levelControl[];
	private FloatControl frequencyShiftControl;
	private FloatControl resonanceControl;
	
	private float[] frequency;
	private float[] level;
	private float frequencyShift;
	private float resonance;
	
	protected int idOffset = 0;
	
	private int sampleRate = 44100;
	private int nBands = 4;
	
	public FormantFilterControls(int instanceIndex, String name, final int idOffset) {
		super(FORMANT_FILTER_ID, instanceIndex, name);
		this.idOffset = idOffset;
		frequencyControl = new FloatControl[nBands];
		levelControl = new FloatControl[nBands];
		frequency = new float[nBands];
		level = new float[nBands];
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
		case RESONANCE: resonance = deriveResonance(); break;
		case FREQSHIFT: frequencyShift = deriveFrequencyShift(); break;
		default:
			int n = (id / 2) - 1;
			if ( (id & 1) == 0 ) {
				frequency[n] = deriveFrequency(n);
			} else {
				level[n] = deriveLevel(n);
			}
		}		
	}
	
	protected void createControls() {
		ControlColumn col;
		for ( int i = 0; i < nBands; i++ ) {
			col = new ControlColumn();
			frequencyControl[i] = createFrequencyControl(i);
			col.add(frequencyControl[i]);
			levelControl[i] = createLevelControl(i);
			col.add(levelControl[i]);
			add(col);
		}
		col = new ControlColumn();
		col.add(frequencyShiftControl = createFrequencyShiftControl());
		col.add(resonanceControl = createResonanceControl());
		add(col);
	}

	protected void deriveSampleRateIndependentVariables() {
		resonance = deriveResonance();
		frequencyShift = deriveFrequencyShift();
		for ( int i = 0; i < nBands; i++ ) {
			level[i] = deriveLevel(i);
		}
	}

	protected void deriveSampleRateDependentVariables() {
		for ( int i = 0; i < nBands; i++ ) {
			frequency[i] = deriveFrequency(i);
		}
	}

	protected float deriveFrequency(int n) {
		return 2 * frequencyControl[n].getValue() / sampleRate;
	}
	
	protected float deriveLevel(int n) {
		return levelControl[n].getValue();
	}
	
	protected float deriveResonance() {
		return resonanceControl.getValue();
	}

	protected float deriveFrequencyShift() {
		return frequencyShiftControl.getValue();
	}

	protected FloatControl createFrequencyControl(int n) {
        ControlLaw law = new LogLaw(100, 5000, "Hz");
        FloatControl control = new FloatControl(n+n+FREQUENCY+idOffset, getString("Frequency"), law, 1f, 250 * (int)Math.pow(2, n));
        control.setInsertColor(Color.yellow);
        return control;		
	}

	protected FloatControl createLevelControl(int n) {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(n+n+LEVEL+idOffset, getString("Level"), law, 0.01f, 1f);
        control.setInsertColor(Color.BLACK);
        return control;				
	}

	protected FloatControl createResonanceControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(RESONANCE+idOffset, getString("Resonance"), law, 0.01f, 0.25f);
        control.setInsertColor(Color.orange);
        return control;				
	}

	protected FloatControl createFrequencyShiftControl() {
        ControlLaw law = new LogLaw(0.25f, 4f, "");
        FloatControl control = new FloatControl(FREQSHIFT+idOffset, getString("Shift"), law, 0.1f, 1f);
        control.setInsertColor(Color.yellow);
        return control;		
	}

	public void setSampleRate(int rate) {
		if ( sampleRate != rate ) {
			sampleRate = rate;
			deriveSampleRateDependentVariables();
		}
	}

	public float getFreqencyShift() {
		return frequencyShift;
	}

	public float getFrequency(int n) {
		return frequency[n];
	}

	public float getLevel(int n) {
		return level[n];
	}

	public float getResonance() {
		return resonance;
	}

	public int size() {
		return nBands;
	}

}
