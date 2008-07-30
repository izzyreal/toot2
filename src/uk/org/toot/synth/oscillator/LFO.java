package uk.org.toot.synth.oscillator;

import uk.org.toot.dsp.FastMath;

public class LFO
{
	private int shape = 0;
    private float modulatorPhase = 0f;
    private float xDelta;
    private LFOVariables vars;
    private float rateDelta;
    private double phaseDelta;
    
    public LFO(LFOVariables vars) {
    	this.vars = vars;
		float spread = vars.getDeviation();
		rateDelta = spread * (float)Math.random() - spread/2;
    }
    
    // do not call from constructor! called in real-time before getSample(...)
	public void update() {
        phaseDelta = xDelta * (vars.getFrequency() + rateDelta);
		shape = vars.isSine() ? 0 : 1;
	}

	public float getSample() {
        modulatorPhase += phaseDelta;
        if ( modulatorPhase > Math.PI ) {
   	        modulatorPhase -= 2 * Math.PI;
       	}
        return (shape == 0) ? FastMath.sin(modulatorPhase) : FastMath.triangle(modulatorPhase);
	}

	public void setSampleRate(int sampleRate) {
		xDelta = (float)(2 * Math.PI / sampleRate);
	}


}
