// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.control;

import java.util.Observable;

/**
 * The abstract base class for generic concrete Controls.
 * Because the various types of Controls have different purposes and features,
 * all of their functionality is accessed from the subclasses that define each
 * kind of Control.
 * The Controls are concrete so they separate the control state from a process.
 * This eases development of simple processes.
 * 
 * Differences to javax.sound.sampled.Controls:
 * Composite parent association is provided to support Chain of Responsibility
 * pattern through Control hierarchies.
 * Observable to support the Observer pattern.
 * Controls may be hidden. This is intended to inhibit UI display of
 * a Control. e.g. filter Q when NOT Parametric, obviously the filter
 * still has a Q, it's just immutable.
 * Controls may be indicators. This is intended to allow UIs to decide
 * how to represent and manage a Control.
 * Controls do not have a Type inner class.
 */
public abstract class Control extends Observable
{
	private final int id;

    /**
     * The parent of the control.
     * @supplierCardinality 0..1
     * @link aggregation
     */
    CompoundControl parent = null;

    private String name;
	private String annotation;
    private boolean hidden = false; // visible, hidden if true
    protected boolean indicator = false; // mutable, immutable if true
	private boolean adjusting = false; // UI should set for knobs, sliders etc.

    /**
     * Constructs a Control with the specified id and name.
     * @param id the id of the control
     * @param name the name of the control
     */
    protected Control(int id, String name) {
        checkId(id);
        this.id = id;
        this.name = name;
        annotation = name; // default annotation
    }

    protected void checkId(int id) {
        if ( id > 127 )
            throw new IllegalArgumentException(name+" id "+id+" > 127!");
    }

    public void setHidden(boolean h) {
        hidden = h;
    }

    /**
     * Obtains the control's id.
     * @return the control's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtains the control's parent control.
     * @return the control's parent control.
     */
    public CompoundControl getParent() {
        return parent;
    }

    protected void notifyParent(Control obj) {
        setChanged();
        notifyObservers(obj); // problematic
        // we don't broadcast indicators to parent observers
        // they're probably changed frequently, i.e. every 2ms
        // and they are probably polled, i.e. every 200ms
        if ( obj.isIndicator() ) return;
        if ( parent != null ) {
        	parent.notifyParent(obj);
        }
    }

    /**
     * Obtains the control's name.
     * @return the control's name.
     */
    public String getName() {
        return name;
    }

    void setName(String s) {
        name = s;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String a) {
        annotation = a;
    }

    public void setIntValue(int value) {
        System.err.println("Unexpected setIntValue("+value+") called on "+getControlPath());
    }

    public int getIntValue() { return -1; }

    /** override for real value strings where possible **/
    public String getValueString() {
        return "";
    }

    /** a clue to a UI to inhibit display of this control. **/
    public boolean isHidden() {
        return hidden;
    }

    /** a clue to a UI to decide how to represent this control. */
    public boolean isIndicator() {
        return indicator;
    }

    public boolean isAdjusting() {
        return adjusting;
    }

    public void setAdjusting(boolean state) {
        adjusting = state;
        notifyParent(this); // tickle automation etc.
    }

    /**
     * Obtains a String describing the control type and its current state.
     * @return a String representation of the Control.
     */
    public String toString() {
        return getName() + " Control";
    }

    public String getControlPath() {
        return getControlPath(null, "/");
    }

    public String getControlPath(Control from, String sep) {
        if (parent != from) {
            if ( getName().length() > 0 ) { // avoid separator if name is ""
	            return parent.getControlPath(from, sep) + sep + getName();
            } else {
                return parent.getControlPath(from, sep);
            }
        }
        return getName();
    }

/*    public int getDepth() {
        return parent == null ? 0 : parent.getDepth()+1;
    } */

}
