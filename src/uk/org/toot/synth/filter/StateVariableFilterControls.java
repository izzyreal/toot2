package uk.org.toot.synth.filter;

public class StateVariableFilterControls extends FilterControls
{
	public StateVariableFilterControls(int instanceIndex, String name, final int idOffset) {
		super(FilterIds.STATE_VARIABLE_FILTER_ID, instanceIndex, name, idOffset);
	}

	// damp = MIN(2.0*(1.0 - pow(res, 0.25)), MIN(2.0, 2.0/freq - freq*0.5));
	// stability correction must be done in real-time
	protected float deriveResonance() {
		return (float)(2 * (1f - Math.pow(super.deriveResonance(), 0.25)));
	}

}
