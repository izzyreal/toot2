// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.audio.id;

/**
 * ProviderId defines known provider IDs and should be used to ensure
 * services from different providers have their own id-spaces which are portable
 * between installations on different machines.
 * Unknown provider IDs will function but cannot be guaranteed to be
 * poertable.
 */
public interface ProviderId
{
    // these constant values MUST NEVER CHANGE, only add new constants
    // to ensure persistent id use remains valid
    // best practice is to import static the single constant you need
    static final int USE_PARENT_PROVIDER_ID = 0; // !!! !!! USE CONSTANT !!! !!!
    static final int TOOT_PROVIDER_ID = 1;
    static final int FRINIKA_PROVIDER_ID = 2;
}
