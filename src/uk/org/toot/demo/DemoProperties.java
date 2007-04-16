// Copyright (C) 2006 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package uk.org.toot.demo;

import java.util.Properties;
import java.io.*;

/**
 * A Properties class
 */
public class DemoProperties extends Properties
{
    public DemoProperties(File path) {
        try {
	        load(
                new BufferedInputStream(
                	new FileInputStream(
                    	new File(path, "demo.properties")
                    )
                )
            );
        } catch ( IOException ioe ) {
            // ok if no defauit properties file
        }
    }

    public String getProperty(String key) {
        // first see if there's a default demo property
        String defaultProperty = super.getProperty(key);
        if ( defaultProperty == null ) {
	        // if there isn't a default just use system property
            return System.getProperty(key);
        }
        // if there is a default use it with system property
        return System.getProperty(key, defaultProperty);
    }

    public String getProperty(String key, String def) {
        String property = getProperty(key);
        return property == null ? def : property;
    }

}
