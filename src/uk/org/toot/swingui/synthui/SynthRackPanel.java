package uk.org.toot.swingui.synthui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;

import uk.org.toot.service.ServiceDescriptor;
import uk.org.toot.service.ServiceVisitor;
import uk.org.toot.swingui.controlui.CompoundControlPanel;
import uk.org.toot.swingui.controlui.ControlPanelFactory;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.SynthRackControls;
import uk.org.toot.synth.SynthServices;

public class SynthRackPanel extends JTabbedPane
{
	private SynthRackControls rackControls;
	private static Vector<String> synthNames = new Vector<String>();

	private final static String NONE = "<none>";

	static {
		synthNames.add(NONE);
		SynthServices.accept(
			new ServiceVisitor() {
				public void visitDescriptor(ServiceDescriptor d) {
					synthNames.add(d.getName());
				}
			}, SynthControls.class
		);
	}

	public SynthRackPanel(SynthRackControls controls) {
		rackControls = controls;
		for ( int i = 0; i < controls.getMidiSynthCount(); i++ ) {
			addTab(String.valueOf((char)('A'+i)), createSynthSelectorPane(i));
		}
	}

	protected Component createSynthSelectorPane(int synth) {
		return new SynthSelectionPanel(synth);
	}

	public class SynthSelectionPanel extends JPanel
	{
		private ButtonGroup buttonGroup = new ButtonGroup();
		private JToggleButton dummyButton = new JToggleButton();
		private JPanel westPanel;
		private JPanel centerPanel;
		private CardLayout cardLayout = new CardLayout();

		public SynthSelectionPanel(int synth) {
			setLayout(new BorderLayout());
			add(westPanel = createLeftComponent(synth), BorderLayout.WEST);
			add(centerPanel = createCenterComponent(), BorderLayout.CENTER);
		}
		
		protected JPanel createLeftComponent(int synth) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			buttonGroup.add(dummyButton); // logical only, not displayed
			for ( int i = 0; i < 16; i++ ) {
				panel.add(new SynthChannelSelector(synth, i));
			}
			return panel;
		}
		
		protected JPanel createCenterComponent() {
			return new JPanel(cardLayout);
		}

		protected void checkSelection() {
			Component[] comps = westPanel.getComponents();
			for ( int i = 0; i < comps.length; i++ ) {
				SynthChannelSelector selector = (SynthChannelSelector)comps[i];
				if ( selector.isAvailable() ) {
					selector.select();
					return;
				}
			}
			dummyButton.setSelected(true); // ensure selectors deselected
		}
		
		public class SynthChannelSelector extends JPanel
		{
			private int N = 48;
			private final JToggleButton button;
			private boolean available = false;
			private JPanel ui;
			private String cardId;

			public SynthChannelSelector(final int synth, final int chan) {
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				cardId = String.valueOf(synth)+"/"+String.valueOf(chan);
				
				button = new JToggleButton(String.valueOf(1+chan), false);
				button.setEnabled(false);
				Dimension maxSize = button.getMaximumSize();
				maxSize.width = N;
				button.setMaximumSize(maxSize);
				button.setMinimumSize(maxSize);
				button.setPreferredSize(maxSize);

				final JComboBox combo = new JComboBox(synthNames);
				Dimension comboMaxSize = combo.getMaximumSize();
				comboMaxSize.height = maxSize.height;
				combo.setMaximumSize(comboMaxSize);
				combo.setPrototypeDisplayValue("A Fairly Long Synth Name");

				add(combo);
				add(Box.createHorizontalGlue());
				add(button);

				buttonGroup.add(button);

				ActionListener comboActionListener = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String s = (String)combo.getSelectedItem();
						boolean none = s.equals(NONE);
						button.setEnabled(!none);
						button.setSelected(!none);
						available = !none;
						if ( none ) {
							rackControls.set(synth, chan, null);
							if ( ui != null ) {
								centerPanel.remove(ui);
							}
							checkSelection(); // select something else or dummy button
						} else {
							SynthControls controls = SynthServices.createControls(s);
							rackControls.set(synth, chan, controls);
							ui = new CompoundControlPanel(controls, 1, null,
									new ControlPanelFactory() {
								protected boolean canEdit() { return true; }
							}, true, true);
							centerPanel.add(ui, cardId);
							showSynth();
						}
					}
				};

				combo.addActionListener(comboActionListener);

				ActionListener buttonActionListener = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if ( button.isSelected() ) showSynth();
					}
				};

				button.addActionListener(buttonActionListener);
			}

			public boolean isAvailable() {
				return available;
			}
			
			public void select() {
				button.setSelected(true);
				showSynth();
			}
			
			protected void showSynth() {
				cardLayout.show(centerPanel, cardId);
			}
		} // end of SynthChannelSelector
	} // ends SynthSelectionPanel
}


