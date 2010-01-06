// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.synth.channels.total;

import uk.org.toot.synth.PolyphonicSynthChannel;
import uk.org.toot.synth.modules.amplifier.AmplifierVariables;
import uk.org.toot.synth.modules.envelope.EnvelopeGenerator;
import uk.org.toot.synth.modules.envelope.EnvelopeVariables;
import uk.org.toot.synth.modules.oscillator.DSFOscillator;
import uk.org.toot.synth.modules.oscillator.DSFOscillatorVariables;

/**
 * @author st
 */
public class TotalSynthChannel extends PolyphonicSynthChannel
{
	private DSFOscillatorVariables oscVars;
	private EnvelopeVariables envAVars;
	private AmplifierVariables ampVars;
	
	public TotalSynthChannel(TotalSynthControls controls) {
		super(controls.getName());
		oscVars = controls.getOscillatorVariables();
		envAVars = controls.getEnvelopeVariables(0);
		ampVars = controls.getAmplifierVariables();
	}

	protected void setSampleRate(int rate) {
		super.setSampleRate(rate);
		envAVars.setSampleRate(rate);
	}
	
	@Override
	protected Voice createVoice(int pitch, int velocity, int sampleRate) {
		return new TotalVoice(pitch, velocity);
	}

	public class TotalVoice extends AbstractVoice
	{
		private DSFOscillator osc;
		private EnvelopeGenerator envelopeA;
		
		public TotalVoice(int pitch, int velocity) {
			super(pitch, velocity);
			float wn = (float)(frequency * 2 * Math.PI / sampleRate);
			float ratio = (float)oscVars.getRatioNumerator() / oscVars.getRatioDenominator();
			float wp = wn * ratio;
			float np = oscVars.getPartialCount();
			float a  = oscVars.getPartialRolloffFactor();
			osc = new DSFOscillator(wn, wp, np, a);
			envelopeA = new EnvelopeGenerator(envAVars);
			envelopeA.trigger();
		}


		public void setSampleRate(int sr) {
			// can't change sample rate dynamically !!!
		}
		
		@Override
		protected float getSample() {
			return osc.getSample() * 0.1f * envelopeA.getEnvelope(release);
		}

		@Override
		protected boolean isComplete() {
			return envelopeA.isComplete();
		}
	}
}
