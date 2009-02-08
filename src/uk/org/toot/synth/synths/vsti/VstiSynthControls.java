package uk.org.toot.synth.synths.vsti;

import java.io.File;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

import uk.org.toot.misc.VstHost;
import uk.org.toot.service.ServiceDescriptor;
import uk.org.toot.synth.SynthControls;

import static uk.org.toot.synth.id.TootSynthControlsId.VSTI_SYNTH_ID;

public class VstiSynthControls extends SynthControls implements VstHost
{
	public final static int ID = VSTI_SYNTH_ID;

	private JVstHost2 vsti;
	
	public VstiSynthControls(ServiceDescriptor d) throws Exception {
		super(ID, d.getName());
		// buffer size is large for bad plugins that only set it ONCE
		vsti = JVstHost2.newInstance(new File(d.getDescription()), 44100, 4410);
	}
	
	// causes plugins to show menu
	@Override
	public boolean isPluginParent() { 
		return true; 
	}
	
	// prevents load/save presets menu items
	@Override
	public boolean hasPresets() {
		return false;
	}
	
	public JVstHost2 getVst() {
		return vsti;
	}
}
