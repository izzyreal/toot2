package uk.org.toot.synth.modules.mixer;

import static uk.org.toot.misc.Localisation.getString;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;

public class ModulationMixerControls extends CompoundControl implements ModulationMixerVariables
{
	public final static int DEPTH = 0;
	
	private FloatControl[] depthControl;
	private float[] depth;
	
	private int idOffset = 0;
	private boolean bipolar;
	
	private int count;
	
	public ModulationMixerControls(int instanceIndex, String name, int idOffset, String[] labels, boolean bipolar) {
		this(MixerIds.MODULATION_MIXER_ID , instanceIndex, name, idOffset, labels, bipolar);
	}
	
	public ModulationMixerControls(int id, int instanceIndex, String name, final int idOffset, String[] labels, boolean bipolar) {
		super(id, instanceIndex, name);
		this.idOffset = idOffset;
		this.count = labels.length;
		this.bipolar = bipolar;
		depth = new float[count];
		createControls(labels);
		deriveSampleRateIndependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
				int n = c.getId()-idOffset - DEPTH;
				depth[n] = deriveDepth(n);
			}
		});
	}

	protected void createControls(String[] labels) {
		depthControl = new FloatControl[count];
		for ( int i = 0; i < count; i++ ) {
			add(depthControl[i] = createDepthControl(i, labels[i]));
		}
	}

	protected void deriveSampleRateIndependentVariables() {
		for ( int i = 0; i < count; i++ ) {
			depth[i] = deriveDepth(i);
		}
	}

	protected float deriveDepth(int i) {
		return depthControl[i].getValue();
	}

	protected FloatControl createDepthControl(int i, String label) {
		ControlLaw law = new LinearLaw(bipolar ? -1f : 0f, 1f, "");
		FloatControl control = new FloatControl(i+DEPTH+idOffset, label, law, 0.01f, 0f) {
			private final String[] presetNames = { getString("Off") };

			public String[] getPresetNames() {
				return presetNames;
			}

			public void applyPreset(String presetName) {
				if ( presetName.equals(getString("Off")) ) {
					setValue(0f);
				}
			}        	
		};
		control.setInsertColor(Color.DARK_GRAY);
		return control;				
	}

	public int getCount() {
		return count;
	}
	
	public float getDepth(int n) {
		return depth[n];
	}
	
	public float[] getDepths() {
		return depth;
	}
}
