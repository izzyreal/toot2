package uk.org.toot;

import junit.framework.TestCase;
import uk.org.toot.audio.core.AudioControls;
import uk.org.toot.audio.core.AudioServices;
import uk.org.toot.service.ServiceDescriptor;
import uk.org.toot.service.ServiceVisitor;
import uk.org.toot.synth.SynthChannelControls;
import uk.org.toot.synth.SynthChannelServices;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.SynthServices;

/**
 * Run this to see which services are available
 */

public class ServiceOverview extends TestCase {

    private ServiceVisitor serviceVisitor = new ServiceVisitor() {
        public void visitDescriptor(ServiceDescriptor d) {
            System.out.println(d.getName());
        }
    };

    public void testSynthChannelServices() {
        System.out.println("\nSynthChannelServices:");
        SynthChannelServices.accept(serviceVisitor, SynthChannelControls.class);
    }

    public void testSynthServices() {
        System.out.println("\nSynthServices:");
        SynthServices.accept(serviceVisitor, SynthControls.class);
    }

    public void testAudioServices() {
        System.out.println("\nAudioServices:");
        AudioServices.accept(serviceVisitor, AudioControls.class);
    }

}
