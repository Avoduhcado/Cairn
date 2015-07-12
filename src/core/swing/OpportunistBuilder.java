package core.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JSpinner;

import java.awt.Dimension;

import javax.swing.SpinnerNumberModel;

import core.entities.utils.ai.traits.Opportunist;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OpportunistBuilder extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JSpinner ratingSpinner;
	
	private Opportunist opportunist;

	/**
	 * Create the dialog.
	 */
	public OpportunistBuilder() {
		setTitle("Opportunist Builder");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 260, 130);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		{
			JLabel lblRating = new JLabel("Rating:");
			contentPanel.add(lblRating);
		}
		{
			ratingSpinner = new JSpinner();
			ratingSpinner.setModel(new SpinnerNumberModel(new Float(0), new Float(0), new Float(1), new Float(1)));
			ratingSpinner.setPreferredSize(new Dimension(60, 20));
			contentPanel.add(ratingSpinner);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						opportunist = new Opportunist((float) ratingSpinner.getValue());
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

	public Opportunist getOpportunist() {
		return opportunist;
	}
	
	public JSpinner getSpinner() {
		return ratingSpinner;
	}
}
