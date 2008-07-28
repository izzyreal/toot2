package uk.org.toot.synth.oscillator;

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
        float mod = (shape == 0) ? sine(modulatorPhase) : triangle(modulatorPhase);
        // clamp the cheapo algorithm which goes outside range a little
        if ( mod < -1f ) mod = -1f;
        else if ( mod > 1f ) mod = 1f;
        return mod;
	}

	public void setSampleRate(int sampleRate) {
		timeDelta = 1f / sampleRate;
	}

    // http://www.devmaster.net/forums/showthread.php?t=5784
    private static final float S_B = (float)(4 /  Math.PI);
    private static final float S_C = (float)(-4 / (Math.PI*Math.PI));
    // -PI < x < PI
    protected float sine(float x) {
        return S_B * x + S_C * x * Math.abs(x);
    }

    // -PI < x < PI
    // thanks scoofy[AT]inf[DOT]elte[DOT]hu
    // for musicdsp.org pseudo-code improvement
    protected float triangle(float x) {
        x += Math.PI;		// 0 < x < 2*PI
        x /= Math.PI / 2;   // 0 < x < 4
        x -= 1;				// -1 < x < 3
        if ( x > 1 ) x -= 4f;
        return Math.abs(-(Math.abs(x)-2)) - 1;
    }


}
