package uk.org.toot.synth.modules.filter;

import uk.org.toot.synth.modules.envelope.EnvelopeGenerator;

// http://musicdsp.org/archive.php?classid=3#26
public class MoogFilter2 extends AbstractFilter
{
	private double in1, in2, in3, in4, out1, out2, out3, out4;
	private float res;

	public MoogFilter2(FilterVariables variables, EnvelopeGenerator eg, float freq, float amp) {
		super(variables, eg, freq, amp);
	}
	
	public void update() {
		res  = vars.getResonance();
	}

	public float filter(float sample, boolean release) {
		float f = fc;
		if ( envDepth != 0f ) {
			f += envDepth * cutoffEnv.getEnvelope(release);
		}
		return filter(sample, f, res);
	}

	public float filter(float input, float fc, float res) {
		double f = fc * 1.16;
		double fb = res * (1.0 - 0.15 * f * f);
		input -= out4 * fb;
		input *= 0.35013 * (f*f)*(f*f);
		out1 = input + 0.3 * in1 + (1 - f) * out1; // Pole 1
		in1  = input;
		out2 = out1 + 0.3 * in2 + (1 - f) * out2;  // Pole 2
		in2  = out1;
		out3 = out2 + 0.3 * in3 + (1 - f) * out3;  // Pole 3
		in3  = out2;
		out4 = out3 + 0.3 * in4 + (1 - f) * out4;  // Pole 4
		in4  = out3;
		return (float)out4;	
	}
}
