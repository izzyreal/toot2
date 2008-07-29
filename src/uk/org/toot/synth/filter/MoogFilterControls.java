package uk.org.toot.synth.filter;

public class MoogFilterControls extends FilterControls
{
	public MoogFilterControls(int instanceIndex, String name, final int idOffset) {
		super(FilterIds.MOOG_LPF_ID, instanceIndex, name, idOffset);
	}
	
	protected float deriveResonance() {
		return super.deriveResonance() * 4;
	}
}
