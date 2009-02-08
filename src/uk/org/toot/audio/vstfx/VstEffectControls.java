package uk.org.toot.audio.vstfx;

import java.awt.Color;
import java.io.File;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.spi.AudioControlServiceDescriptor;
import uk.org.toot.control.BooleanControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.misc.VstHost;

public class VstEffectControls extends AudioControls implements VstHost
{
	private ControlLaw vstLaw = new LinearLaw(0f, 1f, "");

	private JVstHost2 vstfx;
	
	public VstEffectControls(AudioControlServiceDescriptor d) throws Exception {
		super(d.getModuleId(), d.getName());
		// buffer size is large for bad plugins that only set it ONCE
		vstfx = JVstHost2.newInstance(new File(d.getPluginPath()), 44100, 4410);
//		describeParameters();
//		addControls();
	}
	
	public boolean canBypass() { return true; }

	// causes plugins to show Preset menu
	public boolean isPluginParent() { 
		return true; 
	}
	
	public JVstHost2 getVst() {
		return vstfx;
	}
	
	protected void describeParameters() {
		int n = vstfx.numParameters();
		for ( int i = 0; i < n ; i++ ) {
			String name = vstfx.getParameterName(i);
			String label = vstfx.getParameterLabel(i);
			System.out.println(i+" "+name+" "+label);
		}
	}
	
	protected void addControls() {
		int n = vstfx.numParameters();
		int perCol = 3;
		if ( n == 4 ) perCol = 2;
		if ( n > 18 ) perCol = 4;
		ControlColumn col = null;
		for ( int i = 0; i < n ; i++ ) {
			if ( (i % perCol) == 0 ) {
				col = new ControlColumn();
				add(col);
			}
			col.add(createVstControl(i));
		}
	}
	
	protected Control createVstControl(int index) {
		if ( isBoolean(index) ) {
			return new VstBooleanControl(index);
		}
		return new VstFloatControl(index);
	}
	
	protected boolean isBoolean(int index) {
		String d = vstfx.getParameterDisplay(index);
		return d.equalsIgnoreCase("on") | d.equalsIgnoreCase("off");
	}
	
	protected class VstFloatControl extends FloatControl
	{
		private int index;

		public VstFloatControl(int index) {
			super(1+index, vstfx.getParameterName(index), vstLaw, 
				   0.001f, vstfx.getParameter(index));
			this.index = index;
			setInsertColor(guessColor());
		}
		
		private Color guessColor() {
			String label = vstfx.getParameterLabel(index).toLowerCase();
			if ( label.indexOf("hz") >= 0 ) return Color.YELLOW;
			
			String name = vstfx.getParameterName(index).toLowerCase();
			Color darkRed = Color.RED.darker();
			if ( name.indexOf("fre") >= 0 ) return Color.YELLOW;
			if ( name.indexOf("tim") >= 0 ) return darkRed;
			if ( name.indexOf("del") >= 0 ) return darkRed;
			if ( name.indexOf("att") >= 0 ) return darkRed;
			if ( name.indexOf("rel") >= 0 ) return darkRed;
			if ( name.indexOf("reg") >= 0 ) return Color.ORANGE;
			if ( name.indexOf("res") >= 0 ) return Color.ORANGE;
			if ( name.indexOf("fee") >= 0 ) return Color.ORANGE;

			if ( label.indexOf("db") >= 0 ) return Color.BLACK;
			return Color.DARK_GRAY;
		}
		
		@Override
		public float getValue() {
			return vstfx.getParameter(index);
		}
		
		@Override
		public void setValue(float value) {
			vstfx.setParameter(index, value);
			notifyParent(this);
		}
		
		@Override
		public String getValueString() {
			return vstfx.getParameterDisplay(index)+" "+vstfx.getParameterLabel(index);
		}
	}
	
	protected class VstBooleanControl extends BooleanControl
	{
		private int index;

		public VstBooleanControl(int index) {
			super(1+index, vstfx.getParameterName(index), 
					vstfx.getParameter(index) < 0.5f ? false : true);
			this.index = index;
			setStateColor(true, Color.PINK);
		}

		@Override
	    public boolean getValue() {
	        return vstfx.getParameter(index) < 0.5f ? false : true;
	    }
		
		@Override
	    public void setValue(boolean value) {
	        if (value != getValue()) {
	            vstfx.setParameter(index, value ? 1f : 0f);
	            notifyParent(this);
	        }
	    }

		@Override
		public String getValueString() {
			return vstfx.getParameterDisplay(index)+" "+vstfx.getParameterLabel(index);
		}		
	}
}
