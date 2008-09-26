// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.control;

import java.util.List;
import java.util.Collections;
import uk.org.toot.service.ServiceDescriptor;

/**
 * An editable chain of CompoundControls,
 * suitable for use as an audio mixer strip or audio multi-fx unit.
 */
public class CompoundControlChain extends CompoundControl
{
    public CompoundControlChain(int id, String name) {
        super(id, name);
    }

    public CompoundControlChain(int id, int index, String name) {
        super(id, index, name);
    }

    public void add(CompoundControl control) { // protected in superclass
        if ( find(control.getName()) != null ) disambiguate(control);
        super.add(control);
    }

    // !!! added for automation purposes - experimental
    public void add(int index, CompoundControl control) {
        if ( controls == null ) return;
        controls.add(index, control);
        control.parent = this;
    }

    public void insert(String insertName, String insertBeforeName) {
        CompoundControl controlToInsert = createControl(insertName);
        if ( controlToInsert == null ) {
            System.err.println(getName()+": insert failed to create "+insertName);
        	return;
        }
        if ( find(insertName) != null ) disambiguate(controlToInsert);
        int insertionIndex = controls.size(); // init for insert at end
        if ( insertBeforeName != null ) {
	        CompoundControl controlToInsertBefore = (CompoundControl)find(insertBeforeName);
    	    if ( controlToInsertBefore == null ) {
                System.err.println(getName()+": insert "+insertName+", "+insertBeforeName+" not found to insert before");
    			return;
    		}
            if ( !controlToInsertBefore.canBeInsertedBefore() ) {
                System.err.println(getName()+": insert "+insertName+" before "+insertBeforeName+" not allowed");
                return;
            }
//        	System.out.println(getControlPath()+" Inserting "+controlToInsert.getName()+" before "+insertBeforeName);
        	insertionIndex = controls.indexOf(controlToInsertBefore);
        }
        controls.add(insertionIndex, controlToInsert);
        controlToInsert.parent = this; // !!! superclass responsibility?
        setChanged();
        // observer insert something equivalent to controlToInsert which is now at insertionIndex
        notifyObservers(
            new ChainMutation(ChainMutation.INSERT, insertionIndex));
    }

    protected void disambiguate(CompoundControl c) {
        String original = c.getName();
        int index = 2; // we start at two since this must be the second
        String str;
        do {
	        str = original+" #"+index;
            index++;
        } while ( find(str) != null ) ;
        c.setName(str);
        c.instanceIndex = --index;
//        assert index < MAX_INSTANCES;
//   	    System.out.println("Need to Disambiguate "+original+" to "+str);
    }

    // override for domain specific stuff
    protected CompoundControl createControl(String name) {
        return null;
    }

    // override for domain specific stuff
	public List<ServiceDescriptor> descriptors() {
        return Collections.emptyList();
    }

    public void delete(String deleteName) {
        Control controlToDelete = find(deleteName);
        if ( controlToDelete == null ) return;
//        System.out.println(getControlPath()+" Deleting "+deleteName);
        delete(controls.indexOf(controlToDelete));
	}

    // provided to allow faulty processes to remove themselves and their controls
    // but not good because it notifies observers on wrong thread
    public void delete(int indexToDelete) {
        controls.remove(indexToDelete);
        setChanged();
	    notifyObservers(
            new ChainMutation(ChainMutation.DELETE, indexToDelete));
    }

    public void move(String moveName, String moveBeforeName) {
		Control controlToMove = find(moveName);
        Control controlToMoveBefore = find(moveBeforeName);
    	if ( controlToMove == null || controlToMoveBefore == null ) return;
//        System.out.println(getControlPath()+" Moving "+moveName+" before "+moveBeforeName);
        int indexToMove = controls.indexOf(controlToMove);
        controls.remove(indexToMove);
        int insertionIndex = controls.indexOf(controlToMoveBefore);
        controls.add(insertionIndex, controlToMove);
        setChanged();
        notifyObservers(
            new ChainMutation(ChainMutation.MOVE, indexToMove, insertionIndex));
    }

    public void setMutating(boolean mutating) {
        setChanged();
        notifyObservers(mutating ? ChainMutation.COMMENCE_INSTANCE :
            						ChainMutation.COMPLETE_INSTANCE);
    }

    // @Override
    public boolean isPluginParent() { return true; }
    
    /**
     * A ChainMutation is used to notify relevant observers to modify their
     * structure to match this CompoundControlChain in a thread-safe manner.
     */
    static public class ChainMutation
    {
        public static final int DELETE = 1;
        public static final int INSERT = 2;
        public static final int MOVE = 3; // equiv delete then insert
        public static final int COMMENCE = 4;
        public static final int COMPLETE = 5;

        public static final ChainMutation COMMENCE_INSTANCE =
            new ChainMutation(COMMENCE);

        public static final ChainMutation COMPLETE_INSTANCE =
            new ChainMutation(COMPLETE);

        private int type;
        private int index0 = -1;
        private int index1 = -1;

        public ChainMutation(int type) {
            if ( type != COMMENCE && type != COMPLETE ) {
                throw new IllegalArgumentException("illegal no indices costructor for this type");
            }
            this.type = type;
        }

        public ChainMutation(int type, int index) {
            this.type = type;
            index0 = index;
        }

        public ChainMutation(int type, int index0, int index1) {
            this(type, index0);
            this.index1 = index1;
        }

        public int getType() { return type; }

        public int getIndex0() { return index0; }

        public int getIndex1() { return index1; }

        public String toString() {
            return typeName()+"("+index0+", "+index1+")";
        }

        private String typeName() {
            switch ( type ) {
            case DELETE: return "Delete";
            case INSERT: return "Insert";
            case MOVE: return "Move";
            case COMMENCE: return "Commence";
            case COMPLETE: return "Complete";
            }
            return "unknown mutation";
        }
    }
}
