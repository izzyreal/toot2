/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.midi.sequence.edit;

public interface Moveable extends Editable
{
    boolean move(long ticks, int semitones);
}
