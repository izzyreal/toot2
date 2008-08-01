package uk.org.toot.synth.oscillator;

import static uk.org.toot.localisation.Localisation.getString;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.EnumControl;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;

public class MultiWaveOscillatorControls extends CompoundControl implements MultiWaveOscillatorVariables 
{
	public final static int WAVE = 0; // TODO move to OscillatorControlIds.java
	public final static int WIDTH = 1;
	public final static int DETUNE = 2;
	public final static int ENV_DEPTH = 3;
	public final static int LFO_DEPTH = 4;
	public final static int OCTAVE = 5;
	
	private FloatControl envDepthControl;
	private FloatControl detuneControl;
	private EnumControl waveControl;
	private FloatControl widthControl;
	private FloatControl widthLFODepthControl;
	private EnumControl octaveControl;
	private int idOffset = 0;
	private MultiWave multiWave;
	private float width;
	private float envDepth;
	private float detuneFactor;
	private float widthLFODepth;
	private int octave;

	private boolean master;
	
	public MultiWaveOscillatorControls(int instanceIndex, String name, int idOffset, boolean master) {
		this(OscillatorIds.MULTI_WAVE_OSCILLATOR_ID, instanceIndex, name, idOffset, master);
	}

	public MultiWaveOscillatorControls(int id, int instanceIndex, String name, final int idOffset, boolean master) {
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
				case WAVE:		multiWave = deriveMultiWave(); 			break;
				case ENV_DEPTH: envDepth = deriveEnvDepth(); 			break;
				case DETUNE:	detuneFactor = deriveDetuneFactor(); 	break;
				case WIDTH:		width = deriveWidth();					break;
				case LFO_DEPTH:	widthLFODepth = deriveWidthLFODepth();	break;
				case OCTAVE:	octave = deriveOctave();				break;
				}
			}
		});
	}
	
	private void createControls() {
		if ( !master ) {
			add(envDepthControl = createEnvelopeDepthControl());
			add(detuneControl = createDetuneControl());
		}
		ControlColumn cc = new ControlColumn() {
			public float getAlignmentY() { return 0.20f; }
		};
		cc.add(waveControl = createWaveControl());
		if ( !master ) {
			cc.add(octaveControl = createOctaveControl());
		}
		add(cc);
		add(widthControl = createWidthControl());
		add(widthLFODepthControl = createWidthLFODepthControl());
	}

	protected FloatControl createEnvelopeDepthControl() {
        ControlLaw law = new LinearLaw(0f, 4f, "");
        FloatControl control = new FloatControl(ENV_DEPTH+idOffset, getString("Envelope"), law, 0.01f, 0f);
        control.setInsertColor(Color.LIGHT_GRAY);
        return control;				
	}

	protected FloatControl createDetuneControl() {
        ControlLaw law = new LinearLaw(0.99f, 1.01f, "");
        FloatControl control = new FloatControl(DETUNE+idOffset, getString("Detune"), law, 0.0001f, 1f);
        control.setInsertColor(Color.MAGENTA);
        return control;						
	}
	
	protected EnumControl createWaveControl() {
		return new EnumControl(WAVE+idOffset, "Wave", "Square") {
			public List getValues() {
				return MultiWaves.getNames();
			}
//		    public boolean isWidthLimited() { return false; }
		};
	}

	
	protected FloatControl createWidthControl() {
        ControlLaw law = new LinearLaw(0.01f, 0.99f, "");
        FloatControl control = new FloatControl(WIDTH+idOffset, getString("Width"), law, 0.01f, 0.5f){
            private final String[] presetNames = { "50%" };

            public String[] getPresetNames() {
                return presetNames;
            }

            public void applyPreset(String presetName) {
                if ( presetName.equals(getString("50%")) ) {
                    setValue(0.5f);
                }
            }        	

        };
        control.setInsertColor(Color.WHITE);
        return control;				
	}

	protected FloatControl createWidthLFODepthControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(LFO_DEPTH+idOffset, getString("LFO"), law, 0.01f, 0f);
        control.setInsertColor(Color.LIGHT_GRAY);
        return control;				
	}

	private static String[] octaveArray = { "-2", "-1", "0", "1", "2" };
	private static List<String> octaveList = Arrays.asList(octaveArray);
	
	protected EnumControl createOctaveControl() {
		return new EnumControl(OCTAVE+idOffset, "Octave", "0") {
			public List getValues() {
				return octaveList;
			}
		};
	}
		
	private void deriveSampleRateIndependentVariables() {
		envDepth = deriveEnvDepth();
		detuneFactor = deriveDetuneFactor();
		multiWave = deriveMultiWave();
		width = deriveWidth();
		widthLFODepth = deriveWidthLFODepth();
		octave = deriveOctave();
	}

	private void deriveSampleRateDependentVariables() {
	}

	protected MultiWave deriveMultiWave() {
		String name = (String)waveControl.getValue();
		return MultiWaves.get(name); // TODO takes a long time on Swing thread
	}
	
	protected float deriveWidth() {
		if ( widthControl == null ) return 0.5f; // !!!
		return widthControl.getValue();
	}
	
	protected float deriveEnvDepth() {
		if ( envDepthControl == null ) return 0f;
		return envDepthControl.getValue();
	}
	
	protected float deriveDetuneFactor() {
		if ( detuneControl == null ) return 1f;
		return detuneControl.getValue();
	}
	
	protected float deriveWidthLFODepth() {
		return widthLFODepthControl.getValue();
	}
	
	protected int deriveOctave() {
		if ( master ) return 0;
		return Integer.parseInt((String)octaveControl.getValue());
	}
	
	public MultiWave getMultiWave() {
		return multiWave;
	}

	public float getWidth() {
		return width;
	}
	
	public float getEnvelopeDepth() {
		return envDepth;
	}
	
	public float getSyncThreshold() {
		return Integer.parseInt((String)octaveControl.getValue());
	}

	public float getDetuneFactor() {
		return detuneFactor;
	}
	
	public float getWidthLFODepth() {
		return widthLFODepth;
	}
	
	public int getOctave() {
		return octave;
	}
	
	public boolean isMaster() {
		return master;
	}
}
