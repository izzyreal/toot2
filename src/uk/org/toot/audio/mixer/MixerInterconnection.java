package uk.org.toot.audio.mixer;

import uk.org.toot.audio.core.AudioProcess;

/**
 * This interface provides a way for mixer inputs and outputs to be connected together.
 * They otherwise can't do this because they have no state so there is nothing to
 * flow between them.
 * 
 * Once an implementation has been created, typically by calling AudioMixer.createInterconnection(), 
 * it can be used with AudioMixer.setInputProcess() or AudioMixer.setDirectOutputProcess()
 * 
 * A typical use would be:
 * 
 *     AudioMixerStrip strip1, strip2;
 *     ...
 *     MixerInterconnection mi = AudioMixer.createInterconnection("con1");
 *     strip1.setDirectOutputProcess(mi);
 *     strip2.setInputProcess(mi);
 *     
 * The direct output from strip1 is then sent to the input of strip2.
 * A latency of one AudioBuffer will occur if strip1 is after strip2 in the AudioMixer since
 * evaluation of strips is sequential.
 * 
 * @author st
 */
public interface MixerInterconnection
{
    AudioProcess getInputProcess();
    AudioProcess getOutputProcess();
}
