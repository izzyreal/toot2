package uk.org.toot.synth.modules;

import static uk.org.toot.misc.Localisation.getString;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.org.toot.control.BooleanControl;
import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LogLaw;

public class GlideControls extends CompoundControl implements GlideVariables
{
	public final static int ENABLE = 0;
	public final static int TIME = 1;
	
	private FloatControl timeControl;
	private BooleanControl enableControl;
	
	private int glideMillis;
	private boolean glideEnable;
	
	private int idOffset = 0;
	
	public GlideControls(int idOffset) {
		this(0x70, getString("Glide"), idOffset); // TODO 0x70 to somewhere
	}
	
	public GlideControls(int id, String name, final int idOffset) {
		super(id, name);
		this.idOffset = idOffset;
		createControls();
		deriveSampleRateIndependentVariables();
		addObserver(new Observer() {
			public void update(Observable obs, Object obj) {
				Control c = (Control) obj;
				switch ( c.getId()-idOffset ) {
				case ENABLE: glideEnable = deriveEnable(); break;
				case TIME: glideMillis = deriveTime(); break;
				}
			}
		});
	}

	protected void createControls() {
		add(enableControl = createEnableControl());
		add(timeControl = createTimeControl());
	}

	protected void deriveSampleRateIndependentVariables() {
		glideEnable = deriveEnable();
		glideMillis = deriveTime();
	}

	protected boolean deriveEnable() {
		return enableControl.getValue();
	}
	
	protected int deriveTime() {
		return (int)timeControl.getValue();
	}

	protected BooleanControl createEnableControl() {
		BooleanControl control = new BooleanControl(ENABLE+idOffset, "On", true);
		control.setStateColor(true, Color.GREEN);
		return control;
	}
	
	protected FloatControl createTimeControl() {
		ControlLaw law = new LogLaw(10f, 1000f, "ms");
		FloatControl control = new FloatControl(TIME+idOffset, "Time", law, 1f, 100f);
		control.setInsertColor(Color.RED.darker());
		return control;				
	}

	public int getGlideMilliseconds() {
		return glideMillis;
	}

	public boolean isGlideEnabled() {
		return glideEnable;
	}
}
