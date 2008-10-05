package uk.org.toot.synth.modules.filter;

import static uk.org.toot.localisation.Localisation.getString;
import static uk.org.toot.synth.modules.filter.FilterControlIds.*;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LogLaw;
import uk.org.toot.control.LinearLaw;

public class FilterControls extends CompoundControl 
	implements FilterVariables
{
	private FloatControl frequencyControl;
	private FloatControl resonanceControl;
	private FloatControl envelopeDepthControl;
	private FloatControl velocityTrackControl;
	private FloatControl keyTrackControl;
	
	private float frequency, resonance, envelopeDepth, velocityTrack, keyTrack;
	
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
		case FREQUENCY: frequency = deriveFrequency(); break;
		case RESONANCE: resonance = deriveResonance(); break;
		case ENV_DEPTH: envelopeDepth = deriveEnvelopeDepth(); break;
		case VEL_TRACK: velocityTrack = deriveVelocityTrack() ; break;
		case KEY_TRACK: keyTrack = deriveKeyTrack(); break;
		}		
	}
	
	protected void createControls() {
		add(envelopeDepthControl = createEnvelopeDepthControl());
		add(frequencyControl = createFrequencyControl());
		add(resonanceControl = createResonanceControl());
		add(velocityTrackControl = createVelocityTrackControl());
		add(keyTrackControl = createKeyTrackControl());
	}

	protected void deriveSampleRateIndependentVariables() {
		resonance = deriveResonance();
		envelopeDepth = deriveEnvelopeDepth();
		velocityTrack = deriveVelocityTrack();
		keyTrack = deriveKeyTrack();
	}

	protected float deriveEnvelopeDepth() {
		return envelopeDepthControl.getValue();		
	}

	protected float deriveVelocityTrack() {
		return velocityTrackControl.getValue();
	}
	
	protected float deriveKeyTrack() {
		return keyTrackControl.getValue();
	}
	
	protected float deriveResonance() {
		return resonanceControl.getValue();
	}

	protected void deriveSampleRateDependentVariables() {
		frequency = deriveFrequency();
	}

	protected float deriveFrequency() {
		return frequencyControl.getValue() * 2 / sampleRate;
	}

	protected FloatControl createFrequencyControl() {
        ControlLaw law = new LogLaw(20, 20000, "Hz");
        FloatControl control = new FloatControl(FREQUENCY+idOffset, getString("Frequency"), law, 1f, 1000f);
        control.setInsertColor(Color.yellow);
        return control;		
	}

	protected FloatControl createResonanceControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(RESONANCE+idOffset, getString("Resonance"), law, 0.01f, 0.25f);
        control.setInsertColor(Color.orange);
        return control;				
	}

	protected FloatControl createEnvelopeDepthControl() {
        ControlLaw law = new LinearLaw(-1f, 1f, "");
        FloatControl control = new FloatControl(ENV_DEPTH+idOffset, getString("Envelope"), law, 0.01f, 0.5f) {
            private final String[] presetNames = { getString("Off") };

            public String[] getPresetNames() {
                return presetNames;
            }

            public void applyPreset(String presetName) {
                if ( presetName.equals(getString("Off")) ) {
                    setValue(0f);
                }
            }        	
        };
        control.setInsertColor(Color.LIGHT_GRAY);
        return control;				
	}

	protected FloatControl createVelocityTrackControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(VEL_TRACK+idOffset, getString("Velocity"), law, 0.01f, 0.5f);
        control.setInsertColor(Color.BLUE);
        return control;				
	}

	protected FloatControl createKeyTrackControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(KEY_TRACK+idOffset, getString("Key"), law, 0.01f, 1f);
        control.setInsertColor(Color.GREEN.darker());
        return control;				
	}

	public float getFrequency() {
		return frequency;
	}

	public float getResonance() {
		return resonance;
	}

	public float getEvelopeDepth() {
		return envelopeDepth;
	}

	public float getVelocityTrack() {
		return velocityTrack;
	}
	
	public float getKeyTrack() {
		return keyTrack;
	}
	
	public void setSampleRate(int rate) {
		if ( sampleRate != rate ) {
			sampleRate = rate;
			deriveSampleRateDependentVariables();
		}
	}

}
