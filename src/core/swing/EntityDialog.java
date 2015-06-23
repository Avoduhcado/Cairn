package core.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import core.entities.Actor;
import core.entities.Backdrop;
import core.entities.Enemy;
import core.entities.Entity;
import core.entities.utils.stats.Health;
import core.entities.utils.stats.Magic;
import core.entities.utils.stats.Stamina;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.util.LinkedList;

import javax.swing.JTabbedPane;

public class EntityDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	private LinkedList<Entity> entities;
	private LinkedList<Entity> defaultEntities = new LinkedList<Entity>();
	private JToggleButton directionToggle;
	private JSpinner depthSpinner;
	private JSpinner xSpinner;
	private JSpinner ySpinner;
	private JSpinner currentHealth;
	private JSpinner maxHealth;
	private JSpinner currentStamina;
	private JSpinner maxStamina;
	private JSpinner currentMagic;
	private JSpinner maxMagic;
	
	/**
	 * Create the dialog.
	 */
	public EntityDialog(final LinkedList<Entity> entitiesToEdit) {
		this.entities = entitiesToEdit;
		for(Entity e : entities) {
			defaultEntities.add(e.clone());
		}
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Entity Editor");
		setBounds(100, 100, 450, 450);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel entityName = new JLabel(entities.getFirst().getName());
			entityName.setBounds(10, 11, 414, 14);
			contentPanel.add(entityName);
		}
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 36, 414, 2);
		contentPanel.add(separator);
		
		JLabel lblXpos = new JLabel("xPos:");
		lblXpos.setBounds(10, 49, 46, 14);
		contentPanel.add(lblXpos);
		
		xSpinner = new JSpinner();
		xSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				float distance = (float) xSpinner.getValue() - entities.getFirst().getX();
				for(Entity e : entities) {
					e.movePosition(distance, 0);
				}
			}
		});
		xSpinner.setModel(new SpinnerNumberModel(new Float(entities.getFirst().getX()), null, null, new Float(1)));
		xSpinner.setBounds(66, 49, 55, 20);
		contentPanel.add(xSpinner);
		
		JLabel lblYpos = new JLabel("yPos:");
		lblYpos.setBounds(131, 49, 46, 14);
		contentPanel.add(lblYpos);
		
		ySpinner = new JSpinner();
		ySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				float distance = (float) ySpinner.getValue() - entities.getFirst().getY();
				for(Entity e : entities) {
					e.movePosition(0, distance);
				}
			}
		});
		ySpinner.setModel(new SpinnerNumberModel(new Float(entities.getFirst().getY()), null, null, new Float(1)));
		ySpinner.setBounds(187, 49, 55, 20);
		contentPanel.add(ySpinner);
		
		if(entities.getFirst() instanceof Backdrop) {
			JSeparator separator_1 = new JSeparator();
			separator_1.setBounds(10, 74, 414, 2);
			contentPanel.add(separator_1);
			
			JLabel lblDepth = new JLabel("Depth:");
			lblDepth.setBounds(10, 87, 46, 14);
			contentPanel.add(lblDepth);
			
			depthSpinner = new JSpinner();
			depthSpinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					for(Entity e : entities) {
						((Backdrop) e).setDepth((float) depthSpinner.getValue() / 100f);
					}
				}
			});
			depthSpinner.setModel(new SpinnerNumberModel(new Float(((Backdrop) entities.getFirst()).getDepth() * 100),
					new Float(-100), new Float(100), new Float(1)));
			depthSpinner.setBounds(66, 87, 55, 20);
			contentPanel.add(depthSpinner);
		}

		if(entities.getFirst() instanceof Actor) {
			JSeparator separator_2 = new JSeparator();
			separator_2.setBounds(10, 118, 414, 2);
			contentPanel.add(separator_2);
			
			directionToggle = new JToggleButton("Facing Right");
			directionToggle.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if(directionToggle.isSelected()) {
						directionToggle.setText("Facing Left");
						((Actor) entities.getFirst()).setDirection(1);
					} else {
						directionToggle.setText("Facing Right");
						((Actor) entities.getFirst()).setDirection(0);
					}
				}
			});
			directionToggle.setBounds(10, 131, 121, 23);
			contentPanel.add(directionToggle);
			
			if(entities.getFirst() instanceof Enemy) {
				JTabbedPane statsPane = new JTabbedPane(JTabbedPane.TOP);
				statsPane.setBounds(141, 131, 267, 91);
				contentPanel.add(statsPane);
				
				JPanel healthPanel = new JPanel();
				statsPane.addTab("Health", null, healthPanel, null);
				healthPanel.setLayout(null);
				
				JLabel lblCurrent = new JLabel("Current:");
				lblCurrent.setBounds(10, 11, 46, 14);
				healthPanel.add(lblCurrent);
				
				currentHealth = new JSpinner();
				currentHealth.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entities.getFirst()).getStats().getHealth().setCurrent((float) currentHealth.getValue());
					}
				});
				currentHealth.setModel(new SpinnerNumberModel(new Float(((Enemy) entities.getFirst()).getStats().getHealth().getCurrent()),
						null, null, new Float(1)));
				currentHealth.setBounds(66, 8, 60, 20);
				healthPanel.add(currentHealth);
				
				JLabel lblMaximum = new JLabel("Maximum");
				lblMaximum.setBounds(10, 36, 46, 14);
				healthPanel.add(lblMaximum);
				
				maxHealth = new JSpinner();
				maxHealth.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entities.getFirst()).getStats().getHealth().setMax((float) maxHealth.getValue());
					}
				});
				maxHealth.setModel(new SpinnerNumberModel(new Float(((Enemy) entities.getFirst()).getStats().getHealth().getMax()),
						null, null, new Float(1)));
				maxHealth.setBounds(66, 33, 60, 20);
				healthPanel.add(maxHealth);
				
				JPanel staminaPanel = new JPanel();
				statsPane.addTab("Stamina", null, staminaPanel, null);
				staminaPanel.setLayout(null);
				
				JLabel label = new JLabel("Current:");
				label.setBounds(10, 11, 46, 14);
				staminaPanel.add(label);
				
				currentStamina = new JSpinner();
				currentStamina.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entities.getFirst()).getStats().getStamina().setCurrent((float) currentStamina.getValue());
					}
				});
				currentStamina.setModel(new SpinnerNumberModel(new Float(((Enemy) entities.getFirst()).getStats().getStamina().getCurrent()),
						null, null, new Float(1)));
				currentStamina.setBounds(66, 8, 60, 20);
				staminaPanel.add(currentStamina);
				
				JLabel label_1 = new JLabel("Maximum");
				label_1.setBounds(10, 36, 46, 14);
				staminaPanel.add(label_1);
				
				maxStamina = new JSpinner();
				maxStamina.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entities.getFirst()).getStats().getStamina().setMax((float) maxStamina.getValue());
					}
				});
				maxStamina.setModel(new SpinnerNumberModel(new Float(((Enemy) entities.getFirst()).getStats().getStamina().getMax()),
						null, null, new Float(1)));
				maxStamina.setBounds(66, 33, 60, 20);
				staminaPanel.add(maxStamina);
				
				JPanel magicPanel = new JPanel();
				magicPanel.setLayout(null);
				statsPane.addTab("Magic", null, magicPanel, null);
				
				JLabel label_2 = new JLabel("Current:");
				label_2.setBounds(10, 11, 46, 14);
				magicPanel.add(label_2);
				
				currentMagic = new JSpinner();
				currentMagic.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entities.getFirst()).getStats().getMagic().setCurrent((float) currentMagic.getValue());
					}
				});
				currentMagic.setModel(new SpinnerNumberModel(new Float(((Enemy) entities.getFirst()).getStats().getMagic().getCurrent()),
						null, null, new Float(1)));
				currentMagic.setBounds(66, 8, 60, 20);
				magicPanel.add(currentMagic);
				
				JLabel label_3 = new JLabel("Maximum");
				label_3.setBounds(10, 36, 46, 14);
				magicPanel.add(label_3);
				
				maxMagic = new JSpinner();
				maxMagic.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entities.getFirst()).getStats().getMagic().setMax((float) maxMagic.getValue());
					}
				});
				maxMagic.setModel(new SpinnerNumberModel(new Float(((Enemy) entities.getFirst()).getStats().getMagic().getMax()),
						null, null, new Float(1)));
				maxMagic.setBounds(66, 33, 60, 20);
				magicPanel.add(maxMagic);
			}
		}
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						float xDistance = (float) xSpinner.getValue() - entities.getFirst().getX();
						float yDistance = (float) ySpinner.getValue() - entities.getFirst().getY();
						for(Entity e : entities) {
							e.movePosition(xDistance, yDistance);
							
							if(e instanceof Backdrop) {
								((Backdrop) e).setDepth((float) depthSpinner.getValue() / 100f);
							} else if(e instanceof Actor) {
								((Actor) e).setDirection(directionToggle.isSelected() ? 1 : 0);
								if(e instanceof Enemy) {
									((Enemy) e).getStats().setHealth(new Health((float) currentHealth.getValue(), (float) maxHealth.getValue()));
									((Enemy) e).getStats().setStamina(new Stamina((float) currentStamina.getValue(), (float) maxStamina.getValue()));
									((Enemy) e).getStats().setMagic(new Magic((float) currentMagic.getValue(), (float) maxMagic.getValue()));
								}
							}
						}
						
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
					public void actionPerformed(ActionEvent evt) {
						for(int i = 0; i<entities.size(); i++) {
							Entity e = entities.get(i);
							Entity eDefault = defaultEntities.get(i);
							e.setPosition(eDefault.getX(), eDefault.getY());
							
							if(e instanceof Backdrop) {
								((Backdrop) e).setDepth(((Backdrop) eDefault).getDepth());
							} else if(e instanceof Actor) {
								((Actor) e).setDirection(((Actor) eDefault).getDirection());
								if(e instanceof Enemy) {
									((Enemy) e).getStats().setHealth(((Enemy) eDefault).getStats().getHealth());
									((Enemy) e).getStats().setStamina(((Enemy) eDefault).getStats().getStamina());
									((Enemy) e).getStats().setMagic(((Enemy) eDefault).getStats().getMagic());
								}
								/*if(e instanceof Scriptable) {
									
								}*/
							}
						}
						
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		setVisible(true);
	}
	
	public JToggleButton getTglbtnFacing() {
		return directionToggle;
	}
	public JSpinner getSpinner() {
		return depthSpinner;
	}
	public JSpinner getXSpinner() {
		return xSpinner;
	}
	public JSpinner getYSpinner() {
		return ySpinner;
	}
	public JSpinner getCurrentHealth() {
		return currentHealth;
	}
	public JSpinner getMaxHealth() {
		return maxHealth;
	}
	public JSpinner getCurrentStamina() {
		return currentStamina;
	}
	public JSpinner getMaxStamina() {
		return maxStamina;
	}
	public JSpinner getCurrentMagic() {
		return currentMagic;
	}
	public JSpinner getMaxMagic() {
		return maxMagic;
	}
}
