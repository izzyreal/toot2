package uk.org.toot.pitch;

import java.util.List;

public class Keys 
{
    /**
     * Return a List of Keys with the specified notes
     */
    static public List<Key> withNotes(int[] keynotes)
    {
        List<Key> match = new java.util.ArrayList<Key>() ;
        
        // iterate Scales
        for ( Scale scale : Scales.getScales() ) {
        	// iterate PitchClasses
        	for ( int pc = 0; pc < 12; pc++ ) {
        		Key key = new Key(pc, scale);
    			if ( key.contains(keynotes) )
                    match.add(key) ;
        	}

        }

        return match ;
    }
    
}
