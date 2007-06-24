package uk.org.toot.pitch;

import java.util.Iterator;
import java.util.List;

/**
 * The ordered list of available Scales
 * @author st
 */
public class Scales 
{
    /**
     * @supplierCardinality 1..*
     * @label scales 
     * @associates <{uk.org.toot.pitch.Scale}>
     */
    private static List<Scale> scales = new java.util.ArrayList<Scale>();

    private static void checkScales() {
    	if ( scales.isEmpty() ) {
    		ConventionalScales.init();
    	}
    }
    
    static public List<Scale> getScales() {
    	checkScales();
        return scales;
    }

    static public void add(Scale scale) {
    	scales.add(scale);
    }
    
    static public List<String> getScaleNames() {
    	checkScales();
        List<String> scaleNames = new java.util.ArrayList<String>();
        Iterator iterator = scales.iterator();
        while ( iterator.hasNext() ) {
            Scale scale = (Scale)iterator.next();
			scaleNames.add(scale.getName());
        }
        return scaleNames;
    }

    static public Scale getInitialScale() {
    	checkScales();
    	return scales.get(0);
    }
    
    static public Scale getScale(String scaleName) {
    	checkScales();
        Iterator iterator = scales.iterator();
        while ( iterator.hasNext() ) {
            Scale scale = (Scale)iterator.next();
            if ( scale.getName().indexOf(scaleName) >= 0 ) {
                return scale;
            }
        }
        return getInitialScale(); // if in doubt, use the first scale
    }

}
