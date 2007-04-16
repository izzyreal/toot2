// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.core;

import java.util.List;
import uk.org.toot.service.*;
import uk.org.toot.control.CompoundControlChain;
import uk.org.toot.control.CompoundControl;

/**
 * An AudioControlsChain extends CompoundControlChain to provide
 * information regarding audio control services which may be plugged in.
 */
public class AudioControlsChain extends CompoundControlChain
{
    private String sourceLabel;

    public AudioControlsChain(int id, String name) {
        super(id, name);
    }

    public AudioControlsChain(int id, int index, String name) {
        super(id, index, name);
    }

    public void setSourceLabel(String label) {
//        System.out.println(getName()+": "+label);
        sourceLabel = label;
        setChanged();
        notifyObservers();
    }

    public String getSourceLabel() {
        return sourceLabel;
    }

    protected CompoundControl createControl(String name) {
        return AudioServices.createControls(name);
    }

	protected void checkInstanceIndex(int index) {
        if ( index < 0 )
            throw new IllegalArgumentException(getName()+" instance "+index+" < 0!");
        if ( index > 127 )
            throw new IllegalArgumentException(getName()+" instance "+index+" > 127!");
    }

    // intended for use by UIs
    // to create a popup menu tree by category from descriptors
	public List<ServiceDescriptor> descriptors() {
        final List<ServiceDescriptor> descriptors =
            new java.util.ArrayList<ServiceDescriptor>();
        AudioServices.accept(
            new ServiceVisitor() {
            	public void visitDescriptor(ServiceDescriptor d) {
                	descriptors.add(d);
            	}
        	}, AudioControls.class
        );
        return descriptors;
    }

/*    public void notifyParent(uk.org.toot.control.Control control) {
        Thread thread = Thread.currentThread();
    	if ( thread.getPriority() > 7 ) {
        	thread.dumpStackTrace();
    	}
        super.notifyParent(control);
    } */
}
