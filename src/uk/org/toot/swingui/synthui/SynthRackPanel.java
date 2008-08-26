package uk.org.toot.swingui.synthui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
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
		rackControls.addSynthControlsListener(
			new SynthRackControls.SynthControlsListener() {
				public void synthControlsSet(int synth, int chan, SynthControls controls) {
					viewSynthControls(synth, chan, controls);
				}
			}
		);
	}

	protected Component createSynthSelectorPane(int synth) {
		return new SynthSelectionPanel(synth);
	}

	protected void setSynthControls(int synth, int chan, SynthControls controls) {
		try {
			rackControls.setSynthControls(synth, chan, controls);
//			viewSynthControls(synth, chan, controls); // TODO MVC
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	protected void viewSynthControls(int synth, int chan, SynthControls controls) {
		SynthSelectionPanel panel = (SynthSelectionPanel)getComponentAt(synth);
		panel.viewSynthControls(chan, controls);
	}
	
	public class SynthSelectionPanel extends JPanel
	{
		private ButtonGroup buttonGroup = new ButtonGroup();
		private JToggleButton dummyButton = new JToggleButton();
		private JPanel westPanel;
		private JPanel centerPanel;
		private CardLayout cardLayout = new CardLayout();
		private int synth;

		public SynthSelectionPanel(int synth) {
			setLayout(new BorderLayout());
			add(westPanel = createLeftComponent(synth), BorderLayout.WEST);
			add(centerPanel = createCenterComponent(), BorderLayout.CENTER);
			this.synth = synth;
		}
		
		protected void setSynthControls(int chan, SynthControls controls) {
			SynthRackPanel.this.setSynthControls(synth, chan, controls);
		}

		public void viewSynthControls(int chan, SynthControls controls) {
			Component[] comps = westPanel.getComponents();
			SynthChannelSelector selector = (SynthChannelSelector)comps[chan];
			selector.viewSynthControls(controls);
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
			private JComboBox combo;
			private boolean available = false;
			private JPanel ui;
			private int chan;
			private String cardId;
			private boolean disableCombo = false;

			public SynthChannelSelector(final int synth, final int chan) {
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				this.chan = chan;
				cardId = String.valueOf(synth)+"/"+String.valueOf(chan);
				
				button = new JToggleButton(String.valueOf(1+chan), false);
				button.setEnabled(false);
				Dimension maxSize = button.getMaximumSize();
				maxSize.width = N;
				button.setMaximumSize(maxSize);
				button.setMinimumSize(maxSize);
				button.setPreferredSize(maxSize);

				combo = new JComboBox(synthNames);
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
						if ( disableCombo ) return;
						String s = (String)combo.getSelectedItem();
						setSynthControls(
							s.equals(NONE) ? null : SynthServices.createControls(s));
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

			protected void setSynthControls(SynthControls controls) {
				SynthSelectionPanel.this.setSynthControls(chan, controls);
			}
			
			public void viewSynthControls(SynthControls controls) {
				available = controls != null;
				button.setEnabled(available);
				button.setSelected(available);
				if ( ui != null ) {
					centerPanel.remove(ui);
					ui = null;
					System.out.println("Removed "+cardId);
				}
				if ( available ) {
					disableCombo = true;
					combo.setSelectedItem(controls.getName()); // goes recursive!!!
					disableCombo = false;
					ui = new CompoundControlPanel(controls, 1, null,
							new ControlPanelFactory() {
						protected boolean canEdit() { return true; }
					}, true, true);
					centerPanel.add(ui, cardId);
					showSynth();					
				} else {
					checkSelection(); // select something else or dummy button		
				}
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


