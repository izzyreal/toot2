package uk.org.toot.misc;

/**
 * A plugin wishing to receive time signature change notifications should implement this
 * interface and add (and remove) it using PluginSupport.
 * @author st
 *
 */
public interface TimeSignatureListener
{
	void timeSignatureChanged(int numerator, int denominator);
}
