package uk.org.toot.midi.sequencer;

public interface TrackControls 
{
	public boolean isMute();
	
	public void setMute(boolean mute);
	
	public boolean isSolo();
	
	public void setSolo(boolean solo);
}
