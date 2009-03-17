package uk.org.toot.audio.delay;

import java.util.List;

import uk.org.toot.control.EnumControl;

import static uk.org.toot.localisation.Localisation.*;
import static uk.org.toot.audio.delay.DelayIds.TEMPO_DELAY_ID;

public class TempoDelayControls extends AbstractDelayControls
	implements TempoDelayVariables
{
	private float delayFactor = 1f;
	
	public TempoDelayControls() {
		super(TEMPO_DELAY_ID, getString("BPM.Delay"));
		ControlColumn col = new ControlColumn();
		col.add(new DelayFactorControl());
		col.add(createFeedbackControl());
		col.add(createMixControl());
		add(col);
	}

	public float getDelayFactor() {
		return delayFactor;
	}

	public float getMaxDelayMilliseconds() {
		return 2000; // !!!
	}

	private static List<NamedFactor> factors = new java.util.ArrayList<NamedFactor>();
	private static NamedFactor defaultFactor = new NamedFactor(1f, "1/4");

	static {
		factors.add(new NamedFactor(0.25f, "1/16"));
		factors.add(new NamedFactor(0.5f/3, "1/8T"));
		factors.add(new NamedFactor(0.5f, "1/8"));
		factors.add(new NamedFactor(1f/3, "1/4T"));
		factors.add(defaultFactor);
		factors.add(new NamedFactor(2f/3, "1/2T"));
		factors.add(new NamedFactor(2f, "1/2"));
	};
	
	protected class DelayFactorControl extends EnumControl
	{
		
		public DelayFactorControl() {
			super(DELAY_FACTOR_ID, getString("Delay"), defaultFactor);
		}

		@Override
		public List getValues() {
			return factors;
		}
		
		@Override
		public void setValue(Object value) {
			super.setValue(value);
			delayFactor = ((NamedFactor)value).getFactor();
		}
		
		@Override
		public boolean hasLabel() { return true; }
	}

	protected static class NamedFactor
	{
		private float factor;
		private String name;
		
		public NamedFactor(float factor, String name) {
			this.factor = factor;
			this.name = name;
		}
		
		public float getFactor() { return factor; }
		
		public String toString() { return name; }
	}
	
}
