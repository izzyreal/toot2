package uk.org.toot.synth.oscillator;

/**
 * This class modulates the amplitude of an LFO with a delay, an attack and a level.
 * It is intended to be used for things such as vibrato where the modulation is
 * introduced slowly, perhaps after a delay, rather than immediately.
 * @author st
 *
 */
public class DelayedLFO extends LFO
{
	private DelayedLFOVariables vars;
	private float level = 1f;
	private float env = 0f;
	private int delay;
	private float attack;
	
	public DelayedLFO(DelayedLFOVariables vars) {
		super(vars);
		this.vars = vars;
	}
	
	public void setSampleRate(int sampleRate) {
		super.setSampleRate(sampleRate);
		delay = (int)(vars.getDelay() * sampleRate);
		attack = vars.getAttack() / sampleRate;
	}

	public void update() {
		super.update();
		level = vars.getLevel();
		if ( env < 0 ) {
			System.out.println("DLFO env "+env);
			env = 0;
		} else if ( env > 2 ) {
			System.out.println("DLFO env "+env);
			env = 1;			
		}
	}
	
	public float getSample() {
		if ( env < 1f ) {
			// either delay or attack
			if ( env == 0 && delay-- > 0 ) {
			} else {
				env += attack;
			}
		}
		float amp = level * env;
		return amp * super.getSample();
	}
}
