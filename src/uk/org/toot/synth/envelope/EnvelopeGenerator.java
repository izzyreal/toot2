package uk.org.toot.synth.envelope;

/**
 * A DAHDSR Envelope Generator.
 * Uses efficient exponential difference equations for good sounding segments.
 * @author st
 *
 */
public class EnvelopeGenerator 
{
	public enum State { DELAY, ATTACK, HOLD, DECAY, SUSTAIN, RELEASE, COMPLETE };
	
	private State state = State.DELAY;
	
	private int delayCount = 0;
	private float attackCoeff = 0.5f;
	private int holdCount = 0;
	private float decayCoeff = 0.0001f;
	private float sustainLevel = 0.25f;
	private float releaseCoeff = 0.001f;
	
	private float envelope = 0f;

	public EnvelopeGenerator(EnvelopeVariables vars) {
		if ( vars != null ) {
		delayCount = vars.getDelayCount();
		attackCoeff = vars.getAttackCoeff();
		holdCount = vars.getHoldCount();
		decayCoeff = vars.getDecayCoeff();
		sustainLevel = vars.getSustainLevel();
		releaseCoeff = vars.getReleaseCoeff();
//		System.out.println("a="+attackCoeff+", d="+decayCoeff+", s="+sustainLevel+", r="+releaseCoeff);
		}
	}
	
	public float getEnvelope(boolean release) {
		if ( release && state != State.COMPLETE ) state = State.RELEASE; // !!!
		switch ( state ) {
		case DELAY:
			if ( --delayCount >= 0 ) break;
			state = State.ATTACK;
//			System.out.print('A');
			// intentional fall through
		case ATTACK:
			envelope += attackCoeff * (1f - envelope);
			if ( envelope > 0.99f ) {
//				System.out.print('H');
				state = State.HOLD;
			}
			break;
		case HOLD:
			if ( --holdCount >= 0 ) break; 
			state = State.DECAY;
//			System.out.print('D');
			// intentional fall through
		case DECAY:
			envelope -= decayCoeff * envelope;
			if ( envelope <= sustainLevel + 0.001f ) {
				state = State.SUSTAIN;
//				System.out.print('S');
			}
			break;
		case SUSTAIN:
			break;
		case RELEASE:
			envelope -= releaseCoeff * envelope;
			if ( envelope < 0.01f ) { // -40dB cuttof for testing
//				System.out.print('C');
				envelope = 0f;
				state = State.COMPLETE;
			}
			break;
		case COMPLETE:
			break;
		}
		return envelope;
	}
	
	public boolean isComplete() {
		return state == State.COMPLETE;
	}
}
