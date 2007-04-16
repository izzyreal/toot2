// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.dynamics;

import java.util.Observer;
import uk.org.toot.audio.filter.Filter;
import uk.org.toot.audio.filter.FilterSpecification;

class CrossoverSection
    implements FilterSpecification
{
    /**
     * @supplierCardinality 1
     * @link aggregation 
     */
    private CrossoverControl control;
    private Filter.Type type;

    public CrossoverSection(CrossoverControl control, Filter.Type type) {
        this.control = control;
        this.type = type;
    }

    public int getFrequency() { return control.getFrequency(); }

	public Filter.Type getClassicType() { return type; }

    public float getResonance() { return 0.707f; }

    public float getLeveldB() { return 0f; }

    public float getLevelFactor() { return 1.0f; }

    public void addObserver(Observer o) { control.addObserver(o); }

    public void deleteObserver(Observer o) { control.deleteObserver(o); }
}


