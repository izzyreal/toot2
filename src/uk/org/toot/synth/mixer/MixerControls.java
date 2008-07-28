package uk.org.toot.synth.mixer;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.synth.SynthControls;

public class MixerControls extends SynthControls implements MixerVariables
{
	public final static int LEVEL = 1;
	
	private FloatControl[] levelControl;
	private float[] level;
	
	private int idOffset = 0;
	
	private int count;
	
	public MixerControls(int instanceIndex, String name, int idOffset, int count) {
		this(MixerIds.SIMPLE_MIXER_ID , instanceIndex, name, idOffset, count);
	}
	
	public MixerControls(int id, int instanceIndex, String name, final int idOffset, int count) {
		super(id, instanceIndex, name);
		this.idOffset = idOffset;
		this.count = count;
		level = new float[count];
		createControls();
		deriveSampleRateIndependentVariables();
		deriveSampleRateDependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
				int n = c.getId()-idOffset - LEVEL;
				level[n] = deriveLevel(n);
			}
		});
	}

	protected void createControls() {
		levelControl = new FloatControl[count];
		for ( int i = 0; i < count; i++ ) {
			add(levelControl[i] = createLevelControl(i));
		}
	}

	protected void deriveSampleRateIndependentVariables() {
		for ( int i = 0; i < count; i++ ) {
			level[i] = deriveLevel(i);
		}
	}

	protected float deriveLevel(int i) {
		return levelControl[i].getValue();
	}

	protected void deriveSampleRateDependentVariables() {
	}

	protected FloatControl createLevelControl(int i) {
        ControlLaw law = new LinearLaw(0f, 1f, "");
        FloatControl control = new FloatControl(i+LEVEL+idOffset, String.valueOf(i+1), law, 0.01f, i > 0 ? 0f : 1f);
        control.setInsertColor(Color.BLACK);
        return control;				
	}

	public int getCount() {
		return count;
	}
	
	public float getLevel(int n) {
		return level[n];
	}
}
