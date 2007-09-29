// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

import uk.org.toot.music.tonality.Key;
import uk.org.toot.music.tonality.Scales;

/** An experimental class to model the 'guitar' player. */
public class Player implements Key.Provider 
{
    private Key key = new Key("C", Scales.getInitialScale());
    private int velocity = 100; // how loud the plucks/bows are

    public int getVelocity() { return velocity; }

    public void setVelocity(int vel) {
        velocity = vel;
    }

    public Key getKey() { return key; }
}
