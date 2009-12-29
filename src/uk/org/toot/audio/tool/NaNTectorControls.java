package uk.org.toot.audio.tool;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;

public class NaNTectorControls extends AudioControls
{
	private final static int NAN_PERCENT_ID = 1;
	private final static float ALPHA = 0.99f;
	private final static ControlLaw percentLaw = new LinearLaw(0, 100, "%");
	
	private float nanAverage = 0f;
	
	public NaNTectorControls() {
		super(ToolIds.NAN_TECTOR_ID, "NaN?");
		add(new NaNIndicator());
	}
	
    @Override
	public boolean canBypass() { return true; }
    
    public void setNaNFactor(float factor) {
    	nanAverage = (ALPHA * nanAverage) + (1f - ALPHA) * factor;
    }
    
    private class NaNIndicator extends FloatControl
    {
		public NaNIndicator() {
			super(NAN_PERCENT_ID, "NaN", percentLaw, 0.1f, 0f);
			indicator = true;
		}
    	
	    @Override
		public float getValue() {
			return nanAverage * 100;
		}
    }
}
