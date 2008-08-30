package uk.org.toot.synth.modules.filter;

import uk.org.toot.dsp.FastMath;
import uk.org.toot.synth.modules.envelope.EnvelopeGenerator;

public class StateVariableFilter extends AbstractFilter
{
	private float prev = 0f;
	private float res;
	private float mix;
	private boolean bp;
	private float low, high, band, notch;

	public StateVariableFilter(StateVariableFilterVariables variables, EnvelopeGenerator eg, float freq, float amp) {
		super(variables, eg, freq, amp);
	}
	
	public void update() {
		res = vars.getResonance();
		mix = ((StateVariableFilterVariables)vars).getModeMix();
		bp = ((StateVariableFilterVariables)vars).isBandMode();		
	}

	/*
	 * res    = resonance 0 to 1; 
	 * drive  = internal distortion 0 to 0.1
	 * freq   = 2.0*sin(PI*MIN(0.25, fc/(fs*2)));  // the fs*2 is because it's double sampled
	 * damp   = MIN(2.0*(1.0 - pow(res, 0.25)), MIN(2.0, 2.0/freq - freq*0.5)); 
	 */
	public float filter(float sample, boolean release) {
		float f1 = fc;
		if ( envDepth != 0f ) {
			f1 += envDepth * cutoffEnv.getEnvelope(release);
		}
		// the /4 is because it's double sampled
		float fc = 2f * FastMath.sin((float)(Math.PI * Math.min(0.24f, f1/4)));  
		// Thanks to Laurent de Soras for the stability limit
		return filter(sample, fc, Math.min(res, (float)Math.min(1.9f, 2f/fc - fc*0.5)));
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
