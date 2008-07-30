package uk.org.toot.synth.filter;

import uk.org.toot.dsp.FastMath;

public class StateVariableFilter extends AbstractFilter
{
	private float prev = 0f;
	private float damp;
	private float mix;
	private boolean bp;
	private float low, high, band, notch;

	public StateVariableFilter(StateVariableFilterVariables variables, float freq, float amp) {
		super(variables, freq, amp);
	}
	
	public void update() {
		mix = ((StateVariableFilterVariables)vars).getModeMix();
		bp = ((StateVariableFilterVariables)vars).isBandMode();		
	}

	/*
	 * res    = resonance 0 to 1; 
	 * drive  = internal distortion 0 to 0.1
	 * freq   = 2.0*sin(PI*MIN(0.25, fc/(fs*2)));  // the fs*2 is because it's double sampled
	 * damp   = MIN(2.0*(1.0 - pow(res, 0.25)), MIN(2.0, 2.0/freq - freq*0.5)); 
	 */
	public float filter(float sample, float env) {
		float f1 = fc + envDepth * env;			// 0..1 (1 at Nyquist)
		float fc = 2f * FastMath.sin((float)(Math.PI * Math.min(0.24f, f1/4)));  // the /4 is because it's double sampled
		// Thanks to Laurent de Soras for the stability limit
		damp = Math.min(vars.getResonance(), (float)Math.min(1.9f, 2f/fc - fc*0.5));		
		return filter(sample, fc, damp);
	}

	public float filter(float in, float freq, float damp) {
		float i1 = (prev + in) / 2; // linearly interpolated double sampled
		prev = in;
		notch = i1 - damp * band;
		low   = low + freq * band;								
		high  = notch - low;									
		band  = freq * high + band; // - drive*band*band*band;	
		notch = in - damp * band;
		low   = low + freq * band;								
		high  = notch - low;									
		band  = freq * high + band; // - drive*band*band*band;	
		return bp ? band : (1f-mix)*low + mix*high;					
	}
}
