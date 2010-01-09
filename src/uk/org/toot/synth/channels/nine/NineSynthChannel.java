// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.channels.nine;

import uk.org.toot.synth.PolyphonicSynthChannel;
import uk.org.toot.synth.modules.envelope.ASREnvelopeGenerator;
import uk.org.toot.synth.modules.envelope.ASREnvelopeVariables;
import uk.org.toot.synth.modules.oscillator.HammondOscillator;
import uk.org.toot.synth.modules.oscillator.HammondOscillatorVariables;

/**
 * A model of a Hammond Drawbar organ.
 * Called nine because, well, draw is slang for cannabis and a bar is 9 oz.
 * And typically there just happens to be 9 drawbars :)
 * @author st
 */
public class NineSynthChannel extends PolyphonicSynthChannel
{
	private HammondOscillatorVariables oscVars;
	private ASREnvelopeVariables envVars;
	
	public NineSynthChannel(NineSynthControls controls) {
		super(controls.getName());
		oscVars = controls.getHammondVariables();
		envVars = new ASREnvelopeVariables() {
			public float getAttackCoeff() {	return 0.1f; }

			public float getReleaseCoeff() { return 0.01f; }

			public boolean getSustain() { return true; }

			public void setSampleRate(int rate) {}			
		};
	}

	@Override
	protected Voice createVoice(int pitch, int velocity, int sampleRate) {
		return new NineVoice(pitch, velocity);
	}

	public class NineVoice extends AbstractVoice
	{
		private HammondOscillator osc;
		private ASREnvelopeGenerator env;

		public NineVoice(int pitch, int velocity) {
			super(pitch, velocity);
			float wn = frequency * 2 * (float)Math.PI / sampleRate;
			osc = new HammondOscillator(wn, oscVars.getLevels());
			env = new ASREnvelopeGenerator(envVars);
		}

		public void setSampleRate(int sr) {
			// can't change sample rate dynamically !!!
		}

		@Override
		protected float getSample() {
			return osc.getSample() * 0.1f * env.getEnvelope(release);
		}

		@Override
		protected boolean isComplete() {
			return env.isComplete();
		}
	}
}
