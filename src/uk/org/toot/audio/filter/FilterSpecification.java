// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.filter;

import java.util.Observer;

public interface FilterSpecification
{
    Filter.Type getClassicType();
    int getFrequency();
    float getResonance();
    float getLeveldB();
    float getLevelFactor(); // derived from LeveldB !!!

    // partial declaration of Observable, which subclasses should generally extend
    void addObserver(Observer observer);
    void deleteObserver(Observer observer);
}


