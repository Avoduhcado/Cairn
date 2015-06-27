package core.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import core.entities.utils.ai.traits.PackLeader;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class PackLeaderBuilder extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	private PackLeader packLeader;
	private JSpinner wanderSpinner;
	private JSpinner xRallySpinner;
	private JSpinner yRallySpinner;
	private JComboBox<Integer> facingCombo;

	/**
	 * Create the dialog.
	 */
	public PackLeaderBuilder() {
		setTitle("PackLeader Trait");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 217, 246);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblRallyPoint = new JLabel("Rally Point");
		lblRallyPoint.setBounds(10, 11, 50, 14);
		contentPanel.add(lblRallyPoint);
		
		xRallySpinner = new JSpinner();
		xRallySpinner.setBounds(90, 8, 50, 20);
		contentPanel.add(xRallySpinner);
		
		JLabel lblY = new JLabel("Y:");
		lblY.setBounds(70, 39, 10, 14);
		contentPanel.add(lblY);
		
		yRallySpinner = new JSpinner();
		yRallySpinner.setBounds(90, 39, 50, 20);
		contentPanel.add(yRallySpinner);
		
		JLabel lblX = new JLabel("X:");
		lblX.setBounds(70, 11, 10, 14);
		contentPanel.add(lblX);
		
		JLabel lblWanderRange = new JLabel("Wander Range:");
		lblWanderRange.setBounds(10, 101, 76, 14);
		contentPanel.add(lblWanderRange);
		
		wanderSpinner = new JSpinner();
		wanderSpinner.setModel(new SpinnerNumberModel(new Integer(15), new Integer(1), null, new Integer(1)));
		wanderSpinner.setBounds(10, 126, 70, 20);
		contentPanel.add(wanderSpinner);
		
		JLabel lblFacing = new JLabel("Facing");
		lblFacing.setBounds(10, 67, 31, 14);
		contentPanel.add(lblFacing);
		
		facingCombo = new JComboBox<Integer>();
		facingCombo.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {-1, 0, 1}));
		facingCombo.setBounds(51, 64, 50, 20);
		contentPanel.add(facingCombo);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						packLeader = new PackLeader(null);
						packLeader.setDefaultFacing((int) facingCombo.getSelectedItem());
						if((int) xRallySpinner.getValue() != 0 && (int) yRallySpinner.getValue() != 0) {
							packLeader.setRallyPoint(new Point((int) xRallySpinner.getValue(), (int) yRallySpinner.getValue()));
						}
						packLeader.setWanderRange((int) wanderSpinner.getValue());
						
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		setVisible(true);
	}
	
	public PackLeader getPackLeader() {
		return packLeader;
	}
	
	public JSpinner getWanderSpinner() {
		return wanderSpinner;
	}
	public JSpinner getXRallySpinner() {
		return xRallySpinner;
	}
	public JSpinner getYRallySpinner() {
		return yRallySpinner;
	}
	public JComboBox<Integer> getFacingCombo() {
		return facingCombo;
	}
}
