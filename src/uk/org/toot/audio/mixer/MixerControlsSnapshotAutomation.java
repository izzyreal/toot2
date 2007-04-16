// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.mixer;

/**
 * The contract for snapshot automation of mixer controls.
 * recall() and store() are suitable for user snapshots.
 * It is intended that as well as user snapshots a project will have a
 * default snapshot. configure() is intended for use with this default
 * project snapshot and should be called prior to recall() in this case.
 * configure() should modify the mixer control structure to suit the snapshot.
 *
 * When you've created a concrete instance connect it to the mixer controls
 * by calling mixerControls.setSnapshotAutomation() and passing it the
 * concrete instance of this interface.
 */
public interface MixerControlsSnapshotAutomation
{
    /**
     * Modify the mixer controls structure to suit the named snapshot.
     * Not inteneded for use by user snapshots, just project snapshots.
     * Add missing Channel and Group strip controls that are required
     * by the named snapshot.
     * Delete, move and insert module controls in strip controls as necessary
     * to achieve the required mixer controls structure.
     * Intended to be called prior to recall() for project snapshots.
     */
	void configure(String name);

    /**
     * Recall a mixer controls snapshot.
     * Recall control values for modules that exist in strips that exist.
     * Does not modify the mixer structure, use configure() to do that.
     */
    void recall(String name);

    /**
     * Store a mixer controls snapshot.
     * Store all control values for all modules in all strips, i.e. pretty
     * much everything.
     */
    void store(String name);

    String[] list();
}
