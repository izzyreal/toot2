/* Copyright Steve Taylor 2006 */

package uk.org.toot.audio.mixer;

import java.util.Observable;
import java.util.Observer;
import uk.org.toot.control.EnumControl;

/**
 * MainMixProcess adds dynamic routing capability to MixProcess.
 * i.e main bus routing to groups or master strip.
 */
public class MainMixProcess extends MixProcess
{
    /**
     * @supplierCardinality 0..1 
     * @label next
     */
    protected AudioMixerStrip nextRoutedStrip = null;
    private Observer routeObserver;
    private EnumControl routeControl;

    public MainMixProcess(AudioMixerStrip strip, MainMixVariables vars, final AudioMixer mixer) {
        super(strip, vars);
        routeControl = vars.getRouteControl(); // !!! !!!
        if ( routeControl != null ) {
            // does getStrip() on a user thread
	        routeObserver = new Observer() {
    	        public void update(Observable obs, Object obj) {
       				nextRoutedStrip = mixer.getStrip(routeControl.getValueString());
       	    	}
	       	};
	        routedStrip = mixer.getStrip(routeControl.getValueString());
        }
	}

    protected AudioMixerStrip getRoutedStrip() {
   		// cope with observed async routed strip changes
   		if ( routeControl != null && nextRoutedStrip != null ) {
       		routedStrip = nextRoutedStrip;
            nextRoutedStrip = null;
   		}
        return super.getRoutedStrip();
    }

    public void open() {
        super.open();
        if ( routeControl != null && routeObserver != null ) {
            routeControl.addObserver(routeObserver);
        }
    }

    public void close() {
        if ( routeControl != null && routeObserver != null ) {
            routeControl.deleteObserver(routeObserver);
        }
        super.close();
    }
}
