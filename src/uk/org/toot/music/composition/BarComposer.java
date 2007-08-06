// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.music.composition;

import uk.org.toot.tonality.Key;

public interface BarComposer 
{
	/**
	 * Create a bar of notes in the specified Key.
	 * @param key the Key to compose in
	 * @return an int array representing packed notes
	 */
	public abstract int[] composeBar(Key key);
}
