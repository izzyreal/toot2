/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.midi.sequence;

public class ClickSpec {
    private int note1;
    private int noteN;

    public ClickSpec() {
        this(37);
    }

    public ClickSpec(int note) {
        this(note, note);
    }

    public ClickSpec(int note1, int noteN) {
        this.note1 = note1;
        this.noteN = noteN;
    }

    public String device() { return "Uart"; }

    public int channel(int beat) {
        return 10 - 1;
    }

    public int velocity(int beat) {
        return beat == 0 ? 90 : 50;
    }

    public int note(int beat) {
        return beat == 0 ? note1 : noteN;
    }
}
