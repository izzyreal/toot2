package uk.org.toot.synth.amplifier;

import static uk.org.toot.localisation.Localisation.getString;
import static uk.org.toot.synth.amplifier.AmplifierControlIds.*;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.synth.SynthControls;

public class AmplifierControls extends SynthControls 
	implements AmplifierVariables
{
	private FloatControl velocityTrackControl;
	
	private float velocityTrack;
	
	private int idOffset = 0;
	
	private int sampleRate = 44100;
	
	public AmplifierControls(int instanceIndex, String name, int idOffset) {
		this(AmplifierIds.AMPLIFIER_ID , instanceIndex, name, idOffset);
	}
	
	public AmplifierControls(int id, int instanceIndex, String name, final int idOffset) {
		super(id, instanceIndex, name);
		this.idOffset = idOffset;
		createControls();
		deriveSampleRateIndependentVariables();
		deriveSampleRateDependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
//				if (c.isIndicator()) return;
				switch (c.getId()-idOffset) {
				case VEL_TRACK: velocityTrack = deriveVelocityTrack() ; break;
				}
			}
		});
	}

	protected void createControls() {
		add(velocityTrackControl = createVelocityTrackControl());
	}

	protected void deriveSampleRateIndependentVariables() {
		velocityTrack = deriveVelocityTrack();
	}

	protected float deriveVelocityTrack() {
		return velocityTrackControl.getValue();
	}

	protected void deriveSampleRateDependentVariables() {
	}

	protected FloatControl createVelocityTrackControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(VEL_TRACK+idOffset, getString("Velocity"), law, 0.01f, 0.5f);
        control.setInsertColor(Color.BLUE);
        return control;				
	}

	public float getVelocityTrack() {
		return velocityTrack;
	}
	
	public void setSampleRate(int rate) {
		if ( sampleRate != rate ) {
			sampleRate = rate;
			deriveSampleRateDependentVariables();
		}
	}

}
