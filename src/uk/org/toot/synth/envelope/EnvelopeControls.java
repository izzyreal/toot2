package uk.org.toot.synth.envelope;

import static uk.org.toot.localisation.Localisation.getString;
import static uk.org.toot.synth.envelope.EnvelopeControlIds.*;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.control.LogLaw;

public class EnvelopeControls extends CompoundControl
	implements EnvelopeVariables
{
	private FloatControl delayControl;
	private FloatControl attackControl;
	private FloatControl holdControl;
	private FloatControl decayControl;
	private FloatControl sustainControl;
	private FloatControl releaseControl;
	
	private int sampleRate = 44100;

	private int delay = 0, hold; // in samples
	private float attack, decay, sustain, release; // 0.. coefficients

	private int idOffset = 0;
	
	// mutiplies the max attack, decay and release times
	private float timeMultiplier;
	
	public EnvelopeControls(int instanceIndex, String name, int idOffset) {
		this(instanceIndex, name, idOffset, 1f);
	}
		
	public EnvelopeControls(int instanceIndex, String name, int idOffset, float timeMultiplier) {
		this(EnvelopeIds.DAHDSR_ENVELOPE_ID, instanceIndex, name, idOffset, timeMultiplier);
	}
		
	public EnvelopeControls(int id, int instanceIndex, String name, final int idOffset, float timeMultiplier) {
		super(id, name);
		this.idOffset = idOffset;
		this.timeMultiplier = timeMultiplier;
		createControls();
		deriveSampleRateIndependentVariables();
		deriveSampleRateDependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
//				if (c.isIndicator()) return;
				switch (c.getId()-idOffset) {
				case DELAY:	delay = deriveDelay(); break;
				case ATTACK: attack = deriveAttack(); break;
				case HOLD: hold = deriveHold(); break;
				case DECAY: decay = deriveDecay(); break;
				case SUSTAIN: sustain = deriveSustain(); break;
				case RELEASE: release = deriveRelease(); break;
				}
			}
		});
	}
	
	protected boolean hasDelay() {
		return true;
	}
	
	protected void createControls() {
		float m = timeMultiplier;
		if ( hasDelay() ) {
			add(delayControl = createDelayControl(0f, 1000f, 0f));		// ms
		}
		add(attackControl = createAttackControl(0.1f, 1000f*m, 1f)); 	// ms
		add(holdControl = createHoldControl(0, 1000, 10)); 			// ms
		add(decayControl = createDecayControl(10f, 10000f*m, 100f));	// (ms)
		add(sustainControl = createSustainControl());
		add(releaseControl = createReleaseControl(2, 2000*m, 200));	// ms
	}

    protected void deriveSampleRateIndependentVariables() {
    	sustain = deriveSustain();
    }

    protected void deriveSampleRateDependentVariables() {
   		delay = deriveDelay();
    	attack = deriveAttack();
    	hold = deriveHold();
    	decay = deriveDecay();
    	release = deriveRelease();
    }
    
	protected float deriveSustain() {
		return sustainControl.getValue(); // 0..1		
	}

    private static float LOG_0_01 = (float)Math.log(0.01);
    // http://www.physics.uoguelph.ca/tutorials/exp/Q.exp.html
	// http://www.musicdsp.org/showArchiveComment.php?ArchiveID=136
    // return k per sample for 99% in specified milliseconds
    protected float deriveTimeFactor(float milliseconds) {
    	float ns = milliseconds * sampleRate / 1000;
        float k = LOG_0_01 / ns ; // k, per sample
        return (float)(1f -Math.exp(k));
    }

	protected int deriveDelay() {
		if ( !hasDelay() ) return 0;
		return (int)(delayControl.getValue() * sampleRate / 1000);		
	}

    protected float deriveAttack() {
        return deriveTimeFactor(attackControl.getValue());
    }

    protected int deriveHold() {
        return (int)(holdControl.getValue() * sampleRate / 1000);
    }

	protected float deriveDecay() {
		return deriveTimeFactor(decayControl.getValue());
	}

    protected float  deriveRelease() {
        return deriveTimeFactor(releaseControl.getValue());
    }

	protected FloatControl createDelayControl(float min, float max, float init) {
        ControlLaw law = new LinearLaw(min, max, "ms");
        FloatControl delayControl = new FloatControl(DELAY+idOffset, getString("Delay"), law, 1f, init);
        delayControl.setInsertColor(Color.red.darker());
        return delayControl;
	}

    protected FloatControl createAttackControl(float min, float max, float init) {
        ControlLaw law = new LogLaw(min, max, "ms");
        FloatControl attackControl = new FloatControl(ATTACK+idOffset, getString("Attack"), law, 0.1f, init);
        attackControl.setInsertColor(Color.red.darker());
        return attackControl;
    }

    protected FloatControl createHoldControl(float min, float max, float init) {
        ControlLaw law = new LinearLaw(min, max, "ms");
        FloatControl holdControl = new FloatControl(HOLD+idOffset, getString("Hold"), law, 1f, init);
        holdControl.setInsertColor(Color.red.darker());
        return holdControl;
    }

	protected FloatControl createDecayControl(float min, float max, float init) {
        ControlLaw law = new LogLaw(min, max, "ms");
        FloatControl decayControl = new FloatControl(DECAY+idOffset, getString("Decay"), law, 1f, init);
        decayControl.setInsertColor(Color.red.darker());
        return decayControl;
	}

	protected FloatControl createSustainControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl sustainControl = new FloatControl(SUSTAIN+idOffset, getString("Sustain"), law, 0.01f, 0.5f);
        sustainControl.setInsertColor(Color.lightGray);
        return sustainControl;
	}

    protected FloatControl createReleaseControl(float min, float max, float init) {
        ControlLaw law = new LogLaw(min, max, "ms");
        FloatControl releaseControl = new FloatControl(RELEASE+idOffset, getString("Release"), law, 1f, init);
        releaseControl.setInsertColor(Color.red.darker());
        return releaseControl;
    }

	public int getDelayCount() {
		return delay;
	}

	public float getAttackCoeff() {
		return attack;
	}

	public int getHoldCount() {
		return hold;
	}

	public float getDecayCoeff() {
		return decay;
	}

	public float getSustainLevel() {
		return sustain;
	}

	public float getReleaseCoeff() {
		return release;
	}

	public void setSampleRate(int rate) {
		if ( sampleRate != rate ) {
			sampleRate = rate;
			deriveSampleRateDependentVariables();
		}
	}

}
