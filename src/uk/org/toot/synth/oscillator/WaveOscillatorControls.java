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
	public final static int ENV_DEPTH = 2;
	public final static int SUB_LEVEL = 3;
	
	private EnumControl waveControl;
	private FloatControl envDepthControl;
	private FloatControl subLevelControl;
	private int idOffset = 0;
	private Wave wave;
	
	public WaveOscillatorControls(int instanceIndex, String name, int idOffset) {
		this(OscillatorIds.WAVE_OSCILLATOR_ID, instanceIndex, name, idOffset);
	}

	public WaveOscillatorControls(int id, int instanceIndex, String name, final int idOffset) {
		super(id, name);
		this.idOffset = idOffset;
		createControls();
		deriveSampleRateIndependentVariables();
		deriveSampleRateDependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
//				if (c.isIndicator()) return;
				switch (c.getId()-idOffset) {
				case WAVE:	wave = deriveWave(); break;
				}
			}
		});
	}
	
	private void createControls() {
		add(envDepthControl = createEnvelopeDepthControl());
		add(subLevelControl = createSubLevelControl());
		add(waveControl = createWaveControl());
	}

	protected FloatControl createEnvelopeDepthControl() {
        ControlLaw law = new LinearLaw(0f, 4f, "");
        FloatControl control = new FloatControl(ENV_DEPTH+idOffset, getString("Envelope"), law, 0.01f, 0f);
        control.setInsertColor(Color.black);
        return control;				
	}

	protected FloatControl createSubLevelControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(SUB_LEVEL+idOffset, getString("Sub Level"), law, 0.01f, 1f);
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

	private void deriveSampleRateIndependentVariables() {
		wave = deriveWave();		
	}

	private void deriveSampleRateDependentVariables() {
	}

	protected Wave deriveWave() {
		String name = (String)waveControl.getValue();
		return ClassicWaves.create(name);
	}
	
	public Wave getWave() {
		return wave;
	}

	public float getEnvelopeDepth() {
		return envDepthControl.getValue();
	}
	
	public float getSubLevel() {
		return subLevelControl.getValue();
	}
}
