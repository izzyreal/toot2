package uk.org.toot.synth.synths.plucked;

import static uk.org.toot.synth.id.TootSynthControlsId.SIX_STRING_GUITAR_PLUCKED_SYNTH;

public class SixStringGuitarControls extends PluckedSynthControls
{
	public final static int ID = SIX_STRING_GUITAR_PLUCKED_SYNTH;
	public final static String NAME = "6 String Guitar";
	
	public SixStringGuitarControls() {
		super(ID, NAME, 6);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getLowestFrequency(int string) {
		switch ( string ) {
		case 0: return 82.4f;
		case 1: return 110.0f;
		case 2: return 146.8f;
		case 3: return 196.0f;
		case 4: return 246.9f;
		case 5: return 329.6f;
		}
		return 0;
	}

}
