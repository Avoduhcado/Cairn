package core.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

import core.entities.Actor;
import core.entities.interfaces.Intelligent;
import core.entities.utils.ai.traits.Minion;
import core.entities.utils.ai.traits.PackLeader;
import core.entities.utils.ai.traits.Trait;
import core.setups.Stage;

import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MinionBuilder extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JSpinner wanderSpinner;
	private JComboBox<Actor> leaderCombo;

	private Minion minion;
	
	/**
	 * Create the dialog.
	 */
	public MinionBuilder(Stage stage) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Minion Trait");
		setBounds(100, 100, 214, 206);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblLeader = new JLabel("Leader:");
			lblLeader.setBounds(10, 11, 46, 14);
			contentPanel.add(lblLeader);
		}
		{
			leaderCombo = new JComboBox<Actor>();
			DefaultComboBoxModel<Actor> comboModel = new DefaultComboBoxModel<Actor>();
			comboModel.addElement(null);
			for(Actor a : stage.getCast()) {
				if(a instanceof Intelligent) {
					for(Trait t : ((Intelligent) a).getIntelligence().getTraits()) {
						if(t instanceof PackLeader) {
							comboModel.addElement(a);
							break;
						}
					}
				}
			}
			leaderCombo.setModel(comboModel);
			leaderCombo.setBounds(10, 36, 130, 20);
			contentPanel.add(leaderCombo);
		}
		{
			JLabel lblWanderRange = new JLabel("Wander Range:");
			lblWanderRange.setBounds(10, 67, 76, 14);
			contentPanel.add(lblWanderRange);
		}
		{
			wanderSpinner = new JSpinner();
			wanderSpinner.setModel(new SpinnerNumberModel(new Integer(275), new Integer(1), null, new Integer(1)));
			wanderSpinner.setBounds(10, 92, 76, 20);
			contentPanel.add(wanderSpinner);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						minion = new Minion((Intelligent) leaderCombo.getSelectedItem());
						minion.setWanderRange((int) wanderSpinner.getValue());
						
						dispose();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						minion = null;
						
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		
		setVisible(true);
	}
	
	public static Minion showBuilder(Stage stage) {
		MinionBuilder builder = new MinionBuilder(stage);
		builder.setVisible(true);
		
		return builder.getMinion();
	}

	public Minion getMinion() {
		return minion;
	}
	
	public JSpinner getWanderSpinner() {
		return wanderSpinner;
	}
	public JComboBox<Actor> getLeaderCombo() {
		return leaderCombo;
	}
}
