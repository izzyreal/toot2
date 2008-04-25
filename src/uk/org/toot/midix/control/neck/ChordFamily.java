// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

import java.util.Hashtable;
import java.util.Set;
import java.util.ArrayList;

abstract public class ChordFamily {
    private Hashtable < String, ChordShape > types = new Hashtable < String, ChordShape > ();
    private int rootString = -1;
    private static ArrayList < ChordFamily > families = new ArrayList < ChordFamily > ();
    private String name;

    static {
        families.add(new CShapeBarre());
        families.add(new AShapeBarre());
        families.add(new GShapeBarre());
        families.add(new EShapeBarre());
        //        families.add(new OpenChords());
    }

    public static ChordFamily named(String name) {
        for (ChordFamily f : families) {
            if (name.equals(f.getName())) return f;
        }
        return families.get(0); // Guitars are better than nothing ;)
    }

    public ChordFamily(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public ChordShape create(String type) {
        ChordShape shape = new ChordShape();
        types.put(type, shape);
        return shape;
    }

    public ChordShape get(String type) {
        ChordShape shape = types.get(type);
        //        if ( shape == null ) shape = types.get("maj"); // major if in doubt ;)
        return shape;
    }

    protected void checkAll() {
        for (String type : keySet()) {
            checkShape(type);
        }
    }

    abstract protected void checkShape(String type);

    abstract public ChordFamily other();

    public int getRootString() { return rootString; }

    protected void setRootString(int n) { rootString = n; }

    //    public Enumeration<String> types() { return types.keys(); }
    public Set < String > keySet() { return types.keySet(); }
}
