package uk.org.toot.synth.oscillator;

import static uk.org.toot.localisation.Localisation.getString;

import java.awt.Color;

import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.control.LogLaw;

public class DelayedLFOControls extends LFOControls implements DelayedLFOVariables
{
	public final static int DELAY = 5;
	public final static int ATTACK = 6;
	public final static int LEVEL = 7;
	
	private FloatControl delayControl;
	private FloatControl attackControl;
	private FloatControl levelControl;
	
	private float delay, attack, level;
	
	public DelayedLFOControls(int instanceIndex, String name, int idOffset, LFOConfig cfg) {
		super(OscillatorIds.ENHANCED_LFO_ID, instanceIndex, name, idOffset, cfg);
	}

	protected void deriveControl(int id) {
		switch ( id ) {
		case DELAY:		delay = deriveDelay(); 		break;
		case ATTACK: 	attack = deriveAttack(); 	break;
		case LEVEL: 	level = deriveLevel();	 	break;
		default: 		super.deriveControl(id);	break;
		}		
	}
	
	protected void createControls() {
		super.createControls();
		add(delayControl = createDelayControl());
		add(attackControl = createAttackControl());
		if ( config.hasLevel ) {
			add(levelControl = createLevelControl());
		}
	}
	
	protected FloatControl createDelayControl() {
        ControlLaw law = new LogLaw(config.delayMin, config.delayMax, "s");
        FloatControl delayControl = 
        	new FloatControl(DELAY+idOffset, getString("Delay"), law, 0.01f, config.delay);
        delayControl.setInsertColor(Color.red.darker());
        return delayControl;
	}

	protected FloatControl createAttackControl() {
        ControlLaw law = new LogLaw(config.attackMin, config.attackMax, "s");
        FloatControl attackControl = 
        	new FloatControl(ATTACK+idOffset, getString("Attack"), law, 0.01f, config.attack);
        attackControl.setInsertColor(Color.red.darker());
        return attackControl;
	}

	protected FloatControl createLevelControl() {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(LEVEL+idOffset, getString("Level"), law, 0.01f, 0f);
        control.setInsertColor(Color.black);
        return control;				
	}

	protected void deriveSampleRateIndependentVariables() {
		super.deriveSampleRateIndependentVariables();
		level = deriveLevel();
		delay = deriveDelay();
		attack = deriveAttack();
	}
	
	protected float deriveDelay() {
		return delayControl.getValue();
	}

	protected float deriveAttack() {
		return attackControl.getValue();
	}

	protected float deriveLevel() {
		if ( levelControl == null ) return 1f;
		return levelControl.getValue();
	}

	public float getDelay() {
		return delay;
	}

	public float getAttack() {
		return attack;
	}

	public float getLevel() {
		return level;
	}

}
