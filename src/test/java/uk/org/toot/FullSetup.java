package uk.org.toot;

import junit.framework.TestCase;
import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioControlsChain;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.fader.FaderControl;
import uk.org.toot.audio.mixer.AudioMixer;
import uk.org.toot.audio.mixer.MixerControls;
import uk.org.toot.audio.mixer.MixerControlsFactory;
import uk.org.toot.audio.system.MixerConnectedAudioSystem;
import uk.org.toot.control.CompoundControl;
import uk.org.toot.midi.core.DefaultConnectedMidiSystem;
import uk.org.toot.synth.SynthRack;
import uk.org.toot.synth.SynthRackControls;
import uk.org.toot.synth.channels.valor.ValorSynthControls;
import uk.org.toot.synth.synths.multi.MultiMidiSynth;
import uk.org.toot.synth.synths.multi.MultiSynthControls;

import javax.sound.midi.ShortMessage;

public class FullSetup extends TestCase {

    private MixerControls mainMixerControls;
    private MixerConnectedAudioSystem audioSystem;
    private DummyAudioServer audioServer = new DummyAudioServer();

    private DefaultConnectedMidiSystem midiSystem = new DefaultConnectedMidiSystem();
    private MultiMidiSynth multiMidiSynth;

    public void testFullSetup() {
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws Exception {
        mainMixerControls = new MixerControls("Mixer");

        final ChannelFormat channelFormat = ChannelFormat.STEREO;

        mainMixerControls.createAuxBusControls("AUX#1", channelFormat);
        mainMixerControls.createAuxBusControls("AUX#2", channelFormat);
        mainMixerControls.createAuxBusControls("AUX#3", channelFormat);
        mainMixerControls.createAuxBusControls("AUX#4", channelFormat);

        final int returnCount = 2;
        MixerControlsFactory.createBusStrips(mainMixerControls, "L-R", channelFormat, returnCount);

        final int channelCount = 32;
        MixerControlsFactory.createChannelStrips(mainMixerControls, channelCount);

        AudioMixer audioMixer = new AudioMixer(mainMixerControls, audioServer);

        audioSystem = new MixerConnectedAudioSystem(audioMixer);
        audioSystem.setAutoConnect(true);

        setFaderLevel("L-R", 100);
        setFaderLevel("1", 10);

        audioServer.start();

        insertSynth();
        multiMidiSynth.getMidiInputs().get(0).transport(new ShortMessage(), 0);

        audioMixer.work(512);

        audioServer.stop();

        boolean onlyZero = true;
        for (AudioBuffer b : audioServer.getBuffers()) {
            for (float f : b.getChannel(0)) {
                if (f != 0) {
                    onlyZero = false;
                    break;
                }
            }
        }

        assertFalse(onlyZero);
    }

    private void setFaderLevel(String faderName, int i) {
        AudioControlsChain sc = mainMixerControls.getStripControls(faderName);
        CompoundControl cc = (CompoundControl) sc.getControls().get(0);
        ((FaderControl) cc.getControls().get(2)).setValue(i);
    }

    private void insertSynth() {
        MultiSynthControls multiSynthControls = new MultiSynthControls();
        SynthRackControls synthRackControls = new SynthRackControls(1);
        SynthRack synthRack = new SynthRack(synthRackControls, midiSystem, audioSystem);
        synthRackControls.setSynthControls(0, multiSynthControls);
        multiMidiSynth = (MultiMidiSynth) synthRack.getMidiSynth(0);

        for (int i = 0; i < 4; i++) {
            multiSynthControls.setChannelControls(i, new ValorSynthControls());
        }
    }
}
