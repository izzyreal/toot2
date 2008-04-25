/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.midi.sequence;

import javax.sound.midi.Track;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.InvalidMidiDataException;
import static uk.org.toot.midi.message.MetaMsg.*;

public class BasicTrack
{
    private Track track;

    public BasicTrack(Track track) {
        this.track = track;
    }

    public Track getTrack() { return track; }

    public boolean add(MidiEvent event) {
        return track.add(event);
    }

    public MidiEvent get(int index) {
        return track.get(index);
    }

    public boolean remove(MidiEvent event) {
        return track.remove(event);
    }

    public int size() {
        return track.size();
    }

    public long ticks() {
        return track.ticks();
    }

    protected MidiEvent getFirstMetaEvent(int type) {
        for (int i = 0; i < size() - 1; i++) {
            MidiEvent event = get(i);
            MidiMessage msg = event.getMessage();
            if ( isMeta(msg) ) {
                if (getType(msg) == type) {
                    return event;
                }
            }
        }
        return null;
    }

    protected String getMetaName(int type) {
        MidiEvent event = getFirstMetaEvent(type);
        if (event == null) return null;
        MidiMessage msg = event.getMessage();
        if ( isMeta(msg) ) {
            return getString(msg);
        }
        return null;
    }

    protected String getMetaNameX(int type) {
        String name = getMetaName(type);
        return name == null ? "<none>" : name;
    }

    protected void setMetaName(int type, String name) throws InvalidMidiDataException {
        MidiMessage msg;
        MidiEvent event = getFirstMetaEvent(type);
        if (event == null) {
            msg = createMeta(type, name);
            event = new MidiEvent(msg, 0);
            add(event);
        } else {
            setString(event.getMessage(), name);
        }
    }

    public String getInstrumentName() {
        return getMetaNameX(INSTRUMENT_NAME);
    }

    public String getDeviceName() {
        return getMetaNameX(DEVICE_NAME);
    }

    public void setDeviceName(String name) throws InvalidMidiDataException {
        setMetaName(DEVICE_NAME, name);
    }

    public String getTrackName() {
        return getMetaNameX(TRACK_NAME);
    }

    public void setTrackName(String name) throws InvalidMidiDataException {
        setMetaName(TRACK_NAME, name);
    }

    public String getTrackShortName() {
        String name = getTrackName();
        int index = name.trim().lastIndexOf(' ');
        if (index < 0) {
            return name.substring(0, 2);
        } else {
            return name.substring(0, 1) + name.charAt(++index);
        }
    }
}
