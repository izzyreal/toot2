// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.sound.midi.*;
import uk.org.toot.midi.message.*;
import uk.org.toot.tonality.*;

/**
 * An AutomatedBand is a composition of AutomatedComposer.
 * It can render a Sequence and a MIDI FIle.
 * @author st
 *
 */
public class AutomatedBand 
{
    /**
     * @link aggregation
     * @supplierCardinality 1..* 
     */
    /*#BarComposer lnkBarComposer;*/
	private List<BarComposer> composers;
	
	private List<Key> keyList;
	private int keyIndex = -1;
	
	private boolean rendering = false;
	private Key key = new Key();
	private float modulationDensity = 0.5f;
	private float cycleOfFifthsDensity = 1f;
	private float keyReturnProbability = 0.5f;
	private int maxKeys = 5;
//	private float scaleDensity = 0.1f;
	private float swingRatio = 1.3f;
//	private List<String> scaleNames;
	
	public AutomatedBand() {
		composers = new java.util.ArrayList<BarComposer>();
		keyList = new java.util.ArrayList<Key>();
//		scaleNames = Scales.getScaleNames();
	}
	
	public void addComposer(BarComposer composer) {
		if ( rendering ) {
			throw new IllegalStateException("Can't add composers while rendering");
		}
		composers.add(composer);
	}
	
	public void removeComposer(BarComposer composer) {
		if ( rendering ) {
			throw new IllegalStateException("Can't remove composers while rendering");
		}
		composers.remove(composer);
	}
	
	public boolean isRendering() {
		return rendering;
	}
	
	public void renderFile(int nbars, int ppq, File file) 
		throws IOException, InvalidMidiDataException {
		Sequence sequence = renderSequence(nbars, ppq);
		MidiSystem.write(sequence, 1, file);
	}
	
	public Sequence renderSequence(int nbars, int ppq) 
		throws InvalidMidiDataException {
		rendering = true;
		Sequence sequence = null;
		sequence = new Sequence(Sequence.PPQ, ppq);
		sequence.createTrack(); // master track
		// create a Track for each composer
		List<Track> tracks = new java.util.ArrayList<Track>();
		for ( BarComposer composer : composers) {
			Track t = sequence.createTrack();
			MidiMessage msg = 
				MetaMsg.createMeta(MetaMsg.TRACK_NAME, composer.getName());
			t.add(new MidiEvent(msg, 0));
			msg = ChannelMsg.createChannel(ChannelMsg.PROGRAM_CHANGE, 
					composer.getChannel(), composer.getProgram());
			t.add(new MidiEvent(msg, 0));
			tracks.add(t);
		}
//		composers.get(0).checkSwing();
		long barTick = 0;
		// for each bar
		for ( int bar = 0; bar < nbars; bar++) {
			barTick += 4 * ppq;
			changeKey();
			// for each composer/track
			for ( int c = 0; c < composers.size(); c++) {
				BarComposer composer = composers.get(c);
				Track track = tracks.get(c);
				composer.renderBar(composer.composeBar(key), track, barTick, 4 * ppq);
			}
		}

		rendering = false;
		return sequence;
	}
	
	protected void changeKey() {
		if ( Math.random() > modulationDensity ) return;
		if ( (Math.random() < keyReturnProbability && keyList.size() > 1) ||
				keyList.size() >= maxKeys ) {
			if ( keyIndex == 0 ) {
				keyIndex += 1;
			} else if ( keyIndex == keyList.size()-1 ) {
				keyIndex -= 1;
			} else {
				keyIndex += Math.random() > 0.5f ? 1 : -1;
			}
			key = keyList.get(keyIndex);
			System.out.println(key+" reused from "+keyIndex);
			return;
		}
/*		if ( Math.random() < scaleDensity ) {
			key.setScale(Scales.getScale(scaleNames.get((int)(Math.random()*4))));
		} else */
		// !!! weak indices, what about 8 and more notes scales
		if ( Math.random() < cycleOfFifthsDensity ) {
			key = new Key(Math.random() < 0.5 ? key.getNote(4-1) : key.getNote(5-1));
		} else {
			key = new Key(Math.random() < 0.5 ? key.getNote(7-1) : key.getNote(1-1));
		}
		if ( keyList.contains(key) ) {
			keyIndex = keyList.indexOf(key);
			return;
		}
		keyList.add(key);
		keyIndex = keyList.size()-1;
		System.out.println(key+" added at "+keyIndex);
	}

	/**
	 * @return the cycleOfFifthsDensity
	 */
	public float getCycleOfFifthsDensity() {
		return cycleOfFifthsDensity;
	}

	/**
	 * @param cycleOfFifthsDensity the cycleOfFifthsDensity to set
	 */
	public void setCycleOfFifthsDensity(float cycleOfFifthsDensity) {
		this.cycleOfFifthsDensity = cycleOfFifthsDensity;
	}

	/**
	 * @return the modulationDensity
	 */
	public float getModulationDensity() {
		return modulationDensity;
	}

	/**
	 * @param modulationDensity the modulationDensity to set
	 */
	public void setModulationDensity(float modulationDensity) {
		this.modulationDensity = modulationDensity;
	}

	/**
	 * @return the swingRatio
	 */
	public float getSwingRatio() {
		return swingRatio;
	}

	/**
	 * @param swingRatio the swingRatio to set
	 */
	public void setSwingRatio(float swingRatio) {
		this.swingRatio = swingRatio;
	}

	/**
	 * @return the keyReturnProbability
	 */
	public float getKeyReturnProbability() {
		return keyReturnProbability;
	}

	/**
	 * @param keyReturnProbability the keyReturnProbability to set
	 */
	public void setKeyReturnProbability(float keyReturnProbability) {
		this.keyReturnProbability = keyReturnProbability;
	}

	/**
	 * @return the maxKeys
	 */
	public int getMaxKeys() {
		return maxKeys;
	}

	/**
	 * @param maxKeys the maxKeys to set
	 */
	public void setMaxKeys(int maxKeys) {
		this.maxKeys = maxKeys;
	}
}
