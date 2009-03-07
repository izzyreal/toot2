package uk.org.toot.misc;

import java.nio.ByteBuffer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

import uk.org.toot.control.CompoundControl;
import static uk.org.toot.midi.message.SysexMsg.SYSTEM_EXCLUSIVE;

/**
 * Midi Persistence for VST plugins
 * @author st
 *
 */
public class VstMidiPersistence
{
	// prevent instantiation
	private VstMidiPersistence() {		
	}
	
	/**
	 * Store the CompoundControls to the Track
	 * @param controls the CompoundControl to store
	 * @param t the Track to store the CompoundControl to
	 */
	public static void store(CompoundControl controls, Track t) {
		if ( controls instanceof VstHost ) {
			JVstHost2 vst = ((VstHost)controls).getVst();
			byte[] data = null;
			int n;
//			String str;
			if ( vst.acceptsProgramsAsChunks() ) {
				data = vst.getProgramChunk();
				n = data.length;
//				str = "byte VST Chunk";
			} else { // enumerate parameters
				n = vst.numParameters();
				ByteBuffer bb = ByteBuffer.allocate(n * 4); // !
				for ( int i = 0; i < n; i++ ) {
					bb.putFloat(vst.getParameter(i));
				}
				data = bb.array();
//				str = "VST parameters";
			}
			SysexMessage sysex = new SysexMessage();
			try {
				sysex.setMessage(SYSTEM_EXCLUSIVE, data, data.length);
				t.add(new MidiEvent(sysex, 0));
//				System.out.println("stored "+n+" "+str+" from "+controls.getName());
			} catch ( InvalidMidiDataException imde ) {
				imde.printStackTrace();
			}
		} else {
			System.out.println(controls.getName()+" isn't a VstHost, can't be stored");
		}

	}

	/**
	 * Recall the CompoundControl from a position in the Track
	 * @param controls the CompoundControl to recall
	 * @param t the Track to recall the CompoundControl from
	 * @param pos the position in the Track to recall from
	 * @return the position in the Track after the recall
	 */
	public static int recall(CompoundControl controls, Track t, int pos) {
		if ( controls instanceof VstHost ) {
			JVstHost2 vst = ((VstHost)controls).getVst();
			MidiMessage m = t.get(pos).getMessage();
			if ( m instanceof SysexMessage) {
				SysexMessage sysex = (SysexMessage)m;
				byte[] data = sysex.getData();
				if ( vst.acceptsProgramsAsChunks() ) {
					vst.setProgramChunk(data);
//					System.out.println("recalled "+data.length+" byte VST Chunk to "+
//							controls.getName());
				} else { // parameters
					ByteBuffer bb = ByteBuffer.wrap(data);
					int nparams = vst.numParameters();
					for ( int i = 0; i < nparams; i++ ) {
						vst.setParameter(i, bb.getFloat());
					}
//					System.out.println("recalled "+nparams+" VST parameters to "+
//							controls.getName());
				}
				pos++;
			}
		} else {
			System.out.println(controls.getName()+" isn't a VstHost, can't be stored");
		}
		return pos;
	}
}
