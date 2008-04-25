// Copyright (C) 2005 - 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.midix.control.neck;

import java.util.List;

public interface Tunings 
{
    List<String> getTunings();

    StringTuning createTuning(String tuning);
}
