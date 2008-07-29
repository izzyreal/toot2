package uk.org.toot.synth.oscillator;

import uk.org.toot.dsp.FastMath;

public class LFO
{
	private int shape = 0;
    private float modulatorPhase = 0f;
    private float rate;
    private float timeDelta = 1f / 44100;
    private LFOVariables vars;
    
    public LFO() {
    	this(4 + 3 * (float)Math.random()); // random rate 4..7Hz
    }
    
    public LFO(float rate) {
    	this.rate = rate;
    }
    
    public LFO(LFOVariables vars) {
    	this.vars = vars;
    }
    
    // do not call from constructor! called in real-time before getSample(...)
	public void update() {
		float f = vars.getFrequency();
		float spread = vars.getDeviation();
		rate = f - spread/2 + spread * (float)Math.random();
		shape = vars.isSine() ? 0 : 1;
	}

	public float getSample() {
		// algorithm copied from uk.org.toot.audio.delay.ModulatedDelayProcess
        double phaseDelta = timeDelta * rate * 2 * Math.PI;
        modulatorPhase += phaseDelta;
        if ( modulatorPhase > Math.PI ) {
   	        modulatorPhase -= 2 * Math.PI;
       	}
        float mod = (shape == 0) ? FastMath.sin(modulatorPhase) : FastMath.triangle(modulatorPhase);
        // clamp the cheapo algorithm which goes outside range a little
        if ( mod < -1f ) mod = -1f;
        else if ( mod > 1f ) mod = 1f;
        return mod;
	}

	public void setSampleRate(int sampleRate) {
		timeDelta = 1f / sampleRate;
	}


}
