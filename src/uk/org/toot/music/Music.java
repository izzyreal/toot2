package uk.org.toot.music;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Music is the set of related Sections and Compositions.
 * The Sections are used in various forms to create the Compositions.
 * The Sections might be Intro, Verse, Chorus, Bridge and Outro.
 * The Compositions might be standard form or extended form, where the
 * extended form would typically have more repetitions of Sections than
 * the standard form and would typically be significantly longer duration.
 * Effectively the Compositions are different 'edits' of the same music.
 * @author st
 *
 */
public class Music implements java.io.Serializable
{
	private List<String> voices;
	private HashSet<Section> sections;
	private HashSet<Composition> compositions;
	
	public Music() {
		voices = new java.util.ArrayList<String>();
		sections = new HashSet<Section>();
		compositions = new HashSet<Composition>();
	}
	
	public Section createSection(String name, int nbars) {
		Section section = new Section(name, nbars);
		sections.add(section);
		return section;
	}
	
	public void deleteSection(Section section) {
		sections.remove(section);
	}
	
	public Composition createComposition(String name) {
		Composition composition = new Composition(name);
		compositions.add(composition);
		return composition;
	}
	
	public void deleteComposition(Composition composition) {
		compositions.remove(composition);
	}
	
	public Iterator<Section> sectionIterator() {
		return sections.iterator();
	}
	
	public Iterator<Composition> compositionIterator() {
		return compositions.iterator();
	}

	public void addVoice(String voice) {
		if ( voices.contains(voice) ) return;
		voices.add(voice);
	}
	
	public void list() {
		System.out.println(voices.size()+" Voices");
		for ( int v = 0; v < voices.size(); v++ ) {
			System.out.println(" "+voices.get(v));
		}
		System.out.println(sections.size()+" Sections");
		Iterator<Section> sectionIterator = sectionIterator();
		while ( sectionIterator.hasNext()) {
			Section section = sectionIterator.next();
			System.out.println(" "+section.getName()+", "+section.getBarCount()+" bars");
		}
		System.out.println(compositions.size()+" Compositions");
		Iterator<Composition> compositionIterator = compositionIterator();
		while ( compositionIterator.hasNext()) {
			Composition composition = compositionIterator.next();
			System.out.println(composition.getName());
			sectionIterator = composition.iterator();
			while ( sectionIterator.hasNext()) {
				Section section = sectionIterator.next();
				System.out.println(" "+section.getName()+", "+section.getBarCount()+" bars");
			}
		}
	}
	
	/**
	 * A Section is a list of bars of notes for each voice.
	 * @author st
	 *
	 */
	public class Section implements java.io.Serializable
	{
		private String name;
		private Hashtable<String, int[][]> voices;
		private int[][] keyChanges;
		private int barCount;
		
		protected Section(String name, int barCount) {
			this.name = name;
			this.barCount = barCount;
			voices = new Hashtable<String, int[][]>();
		}
		
		public int getBarCount() {
			return barCount;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		public void setNotes(String voice, int bar, int[] barNotes) {
			addVoice(voice);
			int[][] notes = voices.get(voice);
			if ( notes == null ) notes = new int[barCount][];
			notes[bar] = barNotes;
		}
		
		public int[] getNotes(String voice, int bar) {
			int[][] notes = voices.get(voice);
			if ( notes == null ) return null;
			return notes[bar];
		}

		public Enumeration<String> getVoices() {
			return voices.keys();
		}

		public void setKeyChanges(int bar, int[] barKeys) {
			if ( keyChanges == null ) keyChanges = new int[barCount][];
			keyChanges[bar] = barKeys;
		}
	}

	/**
	 * A Composition is a named List of Sections in performance order.
	 * @author st
	 *
	 */
	public class Composition implements java.io.Serializable 
	{
		private String name;
		private List<Section> sections;
		
		protected Composition(String name) {
			this.name = name;
			sections = new java.util.ArrayList<Section>();
		}
		
		public boolean add(Section section) {
			return sections.add(section);
		}
		
		public void add(int index, Section section) {
			sections.add(index, section);
		}
		
		public Section get(int index) {
			return sections.get(index);
		}
		
		public Section remove(int index) {
			return sections.remove(index);
		}
		
		public int size() {
			return sections.size();
		}
		
		public Iterator<Section> iterator() {
			return sections.iterator();
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
	}

}
