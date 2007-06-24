package uk.org.toot.pitch;

//import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class Keys 
{
    /**
     * Return a List of Keys with the specified notes
     */
    static public List<Key> withNotes(String[] keynotes)
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
    
    /**
     * Return a List of Keys with the specified notes
     */
    static public List<Key> withNotes(String keynotes)
    {
        int nargs ;
        // count arguments
        StringTokenizer st = new StringTokenizer(keynotes) ;
        for ( nargs = 0 ; st.hasMoreTokens() ; )
        {
            st.nextToken() ;
            nargs++ ;
        }

        // allocate space for arguments
        String[] notes = new String[nargs] ;

        // extract each argument
        st = new StringTokenizer(keynotes) ;
		for ( int i = 0 ; i < nargs && st.hasMoreTokens() ; i++ )
        {
            notes[i] = st.nextToken() ;
//            System.out.print(notes[i]+" ") ;
        }
//        System.out.println() ;

        return withNotes(notes) ;
    }
}
