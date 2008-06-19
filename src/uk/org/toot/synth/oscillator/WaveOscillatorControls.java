package uk.org.toot.synth.oscillator;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.EnumControl;

public class WaveOscillatorControls extends CompoundControl implements WaveOscillatorVariables 
{
	public final static int WAVE = 1; // TODO move to OscillatorControlIds.java
	
	private EnumControl waveControl;
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
		add(waveControl = createWaveControl());
	}

	private EnumControl createWaveControl() {
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

}
