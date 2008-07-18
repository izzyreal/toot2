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

public class WaveOscillatorControls extends CompoundControl implements WaveOscillatorVariables 
{
	public final static int WAVE = 1; // TODO move to OscillatorControlIds.java
	public final static int LEVEL = 2;
	public final static int ENV_DEPTH = 3;
	public final static int DETUNE = 4;
	
	private EnumControl waveControl;
	private FloatControl levelControl;
	private FloatControl envDepthControl;
	private FloatControl detuneControl;
	private int idOffset = 0;
	private Wave wave;
	private float level;
	private float envDepth;
	private float detuneFactor;

	private boolean master;
	
	public WaveOscillatorControls(int instanceIndex, String name, int idOffset, boolean master) {
		this(OscillatorIds.WAVE_OSCILLATOR_ID, instanceIndex, name, idOffset, master);
	}

	public WaveOscillatorControls(int id, int instanceIndex, String name, final int idOffset, boolean master) {
		super(id, name);
		this.idOffset = idOffset;
		this.master = master;
		createControls();
		deriveSampleRateIndependentVariables();
		deriveSampleRateDependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
//				if (c.isIndicator()) return;
				switch (c.getId()-idOffset) {
				case WAVE:		wave = deriveWave(); 					break;
				case LEVEL: 	level = deriveLevel(); 					break;
				case ENV_DEPTH: envDepth = deriveEnvDepth(); 			break;
				case DETUNE:	detuneFactor = deriveDetuneFactor(); 	break;
				}
			}
		});
	}
	
	private void createControls() {
		if ( !master ) {
			add(envDepthControl = createEnvelopeDepthControl());
			add(detuneControl = createDetuneControl());
		}
		add(waveControl = createWaveControl());
		add(levelControl = createLevelControl());
	}

	protected FloatControl createEnvelopeDepthControl() {
        ControlLaw law = new LinearLaw(0f, 4f, "");
        FloatControl control = new FloatControl(ENV_DEPTH+idOffset, getString("Envelope"), law, 0.01f, 0f);
        control.setInsertColor(Color.black);
        return control;				
	}

	protected FloatControl createLevelControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(LEVEL+idOffset, getString("Level"), law, 0.01f, 1f);
        control.setInsertColor(Color.black);
        return control;				
	}

	protected EnumControl createWaveControl() {
		return new EnumControl(WAVE+idOffset, "Wave", "Square") {
			public List getValues() {
				return ClassicWaves.getNames();
			}
//		    public boolean isWidthLimited() { return false; }
		};
	}

	protected FloatControl createDetuneControl() {
        ControlLaw law = new LinearLaw(0.99f, 1.01f, "");
        FloatControl control = new FloatControl(DETUNE+idOffset, getString("Detune"), law, 0.0001f, 1f);
        control.setInsertColor(Color.BLUE);
        return control;				
		
	}
	
	private void deriveSampleRateIndependentVariables() {
		wave = deriveWave();
		level = deriveLevel();
		envDepth = deriveEnvDepth();
		detuneFactor = deriveDetuneFactor();
	}

	private void deriveSampleRateDependentVariables() {
	}

	protected Wave deriveWave() {
		String name = (String)waveControl.getValue();
		return ClassicWaves.create(name);
	}
	
	protected float deriveLevel() {
		if ( levelControl == null ) return 1f;
		return levelControl.getValue();
	}
	
	protected float deriveEnvDepth() {
		if ( envDepthControl == null ) return 0f;
		return envDepthControl.getValue();
	}
	
	protected float deriveDetuneFactor() {
		if ( detuneControl == null ) return 1f;
		return detuneControl.getValue();
	}
	
	public Wave getWave() {
		return wave;
	}

	public float getLevel() {
		return level;
	}

	public float getEnvelopeDepth() {
		return envDepth;
	}
	
	public float getSyncThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getDetuneFactor() {
		return detuneFactor;
	}
	
	public boolean isMaster() {
		return master;
	}
}
