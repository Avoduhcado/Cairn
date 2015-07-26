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
import core.entities.utils.Faction;
import core.entities.utils.ai.AggressiveAI;
import core.entities.utils.ai.DocileAI;
import core.entities.utils.ai.traits.Minion;
import core.entities.utils.ai.traits.Opportunist;
import core.entities.utils.ai.traits.PackLeader;
import core.entities.utils.ai.traits.Trait;
import core.entities.utils.stats.Health;
import core.entities.utils.stats.Magic;
import core.entities.utils.stats.Stamina;
import core.setups.Stage;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EntityDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private Entity entity;
	private Entity defaultEntity;
	
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
	private JList<Trait> traitList;
	private JList<Faction> allyList;
	private JList<Faction> enemyList;
	private JComboBox<String> aiCombo;
	private JSpinner speedSpinner;
	private JSpinner intRateSpinner;
	private JSpinner viewAngleSpinner;
	private JSpinner viewDistanceSpinner;

	/**
	 * Create the dialog.
	 */
	public EntityDialog(final Entity entityEdit, final Stage stage) {
		this.entity = entityEdit;
		this.defaultEntity = entityEdit.clone();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Entity Editor");
		setBounds(100, 100, 570, 477);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				resetToDefault();
			}
		});
		
		contentPanel.setLayout(null);
		{
			JLabel entityName = new JLabel(entity.getName());
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
				float distance = (float) xSpinner.getValue() - entity.getX();
				entity.movePosition(distance, 0);
			}
		});
		xSpinner.setModel(new SpinnerNumberModel(new Float(entity.getX()), null, null, new Float(1)));
		xSpinner.setBounds(66, 49, 55, 20);
		contentPanel.add(xSpinner);

		JLabel lblYpos = new JLabel("yPos:");
		lblYpos.setBounds(131, 49, 46, 14);
		contentPanel.add(lblYpos);

		ySpinner = new JSpinner();
		ySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				float distance = (float) ySpinner.getValue() - entity.getY();
				entity.movePosition(0, distance);
			}
		});
		ySpinner.setModel(new SpinnerNumberModel(new Float(entity.getY()), null, null, new Float(1)));
		ySpinner.setBounds(187, 49, 55, 20);
		contentPanel.add(ySpinner);

		if(entity instanceof Backdrop) {
			JSeparator separator_1 = new JSeparator();
			separator_1.setBounds(10, 74, 414, 2);
			contentPanel.add(separator_1);

			JLabel lblDepth = new JLabel("Depth:");
			lblDepth.setBounds(10, 87, 46, 14);
			contentPanel.add(lblDepth);

			depthSpinner = new JSpinner();
			depthSpinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					((Backdrop) entity).setDepth((float) depthSpinner.getValue() / 100f);
				}
			});
			depthSpinner.setModel(new SpinnerNumberModel(new Float(((Backdrop) entity).getDepth() * 100),
					new Float(-100), new Float(100), new Float(1)));
			depthSpinner.setBounds(66, 87, 55, 20);
			contentPanel.add(depthSpinner);
		}

		if(entity instanceof Actor) {
			JSeparator separator_2 = new JSeparator();
			separator_2.setBounds(10, 118, 414, 2);
			contentPanel.add(separator_2);

			directionToggle = new JToggleButton("Facing Right");
			directionToggle.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if(directionToggle.isSelected()) {
						directionToggle.setText("Facing Left");
						((Actor) entity).setDirection(1);
					} else {
						directionToggle.setText("Facing Right");
						((Actor) entity).setDirection(0);
					}
				}
			});
			directionToggle.setBounds(10, 131, 91, 23);
			directionToggle.setSelected(((Actor) entity).getDirection() == 1);
			contentPanel.add(directionToggle);

			JLabel lblSpeed = new JLabel("Speed");
			lblSpeed.setBounds(111, 135, 30, 14);
			contentPanel.add(lblSpeed);

			speedSpinner = new JSpinner();
			speedSpinner.setModel(new SpinnerNumberModel(new Float(((Actor) entity).getMaxSpeed()),
					new Float(0), null, new Float(1)));
			speedSpinner.setBounds(151, 131, 46, 20);
			contentPanel.add(speedSpinner);

			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.setBounds(10, 157, 534, 238);
			contentPanel.add(tabbedPane);

			JPanel statsPanel = new JPanel();
			tabbedPane.addTab("Statistics", null, statsPanel, null);
			statsPanel.setLayout(new BorderLayout(0, 0));

			if(entity instanceof Enemy) {
				JTabbedPane statsPane = new JTabbedPane(JTabbedPane.TOP);
				statsPanel.add(statsPane, BorderLayout.CENTER);

				JPanel healthPanel = new JPanel();
				statsPane.addTab("Health", null, healthPanel, null);
				healthPanel.setLayout(null);

				JLabel lblCurrent = new JLabel("Current:");
				lblCurrent.setBounds(10, 11, 46, 14);
				healthPanel.add(lblCurrent);

				currentHealth = new JSpinner();
				currentHealth.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entity).getStats().getHealth().setCurrent((float) currentHealth.getValue());
					}
				});
				currentHealth.setModel(new SpinnerNumberModel(new Float(((Enemy) entity).getStats().getHealth().getCurrent()),
						null, null, new Float(1)));
				currentHealth.setBounds(66, 8, 60, 20);
				healthPanel.add(currentHealth);

				JLabel lblMaximum = new JLabel("Maximum");
				lblMaximum.setBounds(10, 36, 46, 14);
				healthPanel.add(lblMaximum);

				maxHealth = new JSpinner();
				maxHealth.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entity).getStats().getHealth().setMax((float) maxHealth.getValue());
					}
				});
				maxHealth.setModel(new SpinnerNumberModel(new Float(((Enemy) entity).getStats().getHealth().getMax()),
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
						((Enemy) entity).getStats().getStamina().setCurrent((float) currentStamina.getValue());
					}
				});
				currentStamina.setModel(new SpinnerNumberModel(new Float(((Enemy) entity).getStats().getStamina().getCurrent()),
						null, null, new Float(1)));
				currentStamina.setBounds(66, 8, 60, 20);
				staminaPanel.add(currentStamina);

				JLabel label_1 = new JLabel("Maximum");
				label_1.setBounds(10, 36, 46, 14);
				staminaPanel.add(label_1);

				maxStamina = new JSpinner();
				maxStamina.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entity).getStats().getStamina().setMax((float) maxStamina.getValue());
					}
				});
				maxStamina.setModel(new SpinnerNumberModel(new Float(((Enemy) entity).getStats().getStamina().getMax()),
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
						((Enemy) entity).getStats().getMagic().setCurrent((float) currentMagic.getValue());
					}
				});
				currentMagic.setModel(new SpinnerNumberModel(new Float(((Enemy) entity).getStats().getMagic().getCurrent()),
						null, null, new Float(1)));
				currentMagic.setBounds(66, 8, 60, 20);
				magicPanel.add(currentMagic);

				JLabel label_3 = new JLabel("Maximum");
				label_3.setBounds(10, 36, 46, 14);
				magicPanel.add(label_3);

				maxMagic = new JSpinner();
				maxMagic.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entity).getStats().getMagic().setMax((float) maxMagic.getValue());
					}
				});
				maxMagic.setModel(new SpinnerNumberModel(new Float(((Enemy) entity).getStats().getMagic().getMax()),
						null, null, new Float(1)));
				maxMagic.setBounds(66, 33, 60, 20);
				magicPanel.add(maxMagic);

				JPanel intelligencePanel = new JPanel();
				tabbedPane.addTab("Intelligence", null, intelligencePanel, null);
				intelligencePanel.setLayout(null);

				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBounds(369, 11, 150, 190);
				intelligencePanel.add(scrollPane);

				traitList = new JList<Trait>();
				traitList.setVisibleRowCount(12);
				traitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				DefaultListModel<Trait> traitsModel = new DefaultListModel<Trait>();
				for(Trait t : ((Enemy) entity).getIntelligence().getTraits()) {
					traitsModel.addElement(t);
				}
				traitList.setModel(traitsModel);
				scrollPane.setColumnHeaderView(traitList);

				aiCombo = new JComboBox<String>();
				aiCombo.setBounds(10, 11, 78, 20);
				intelligencePanel.add(aiCombo);
				aiCombo.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						switch((String) aiCombo.getSelectedItem()) {
						case "Docile":
							((Enemy) entity).changeIntelligence(new DocileAI());
							break;
						case "Aggressive":
							((Enemy) entity).changeIntelligence(new AggressiveAI(0.5f));
							break;
						}
					}
				});
				aiCombo.setModel(new DefaultComboBoxModel<String>(new String[] {"Docile", "Aggressive"}));
				if(((Enemy) entity).getIntelligence() instanceof DocileAI) {
					aiCombo.setSelectedIndex(0);
				} else if(((Enemy) entity).getIntelligence() instanceof AggressiveAI) {
					aiCombo.setSelectedIndex(1);
				}

				JButton btnNewButton = new JButton("Add Trait");
				btnNewButton.setBounds(282, 9, 77, 23);
				intelligencePanel.add(btnNewButton);

				JButton btnRemoveTrait = new JButton("Remove Trait");
				btnRemoveTrait.setBounds(262, 77, 97, 23);
				intelligencePanel.add(btnRemoveTrait);

				JButton btnEditTrait = new JButton("Edit Trait");
				btnEditTrait.setBounds(270, 43, 89, 23);
				intelligencePanel.add(btnEditTrait);

				JLabel lblViewDistance = new JLabel("View Distance");
				lblViewDistance.setBounds(77, 98, 66, 14);
				intelligencePanel.add(lblViewDistance);

				JLabel lblViewRadius = new JLabel("View Angle");
				lblViewRadius.setBounds(10, 98, 52, 14);
				intelligencePanel.add(lblViewRadius);

				JLabel lblIntelligenceRating = new JLabel("Intelligence Rating");
				lblIntelligenceRating.setBounds(10, 42, 89, 14);
				intelligencePanel.add(lblIntelligenceRating);

				intRateSpinner = new JSpinner();
				intRateSpinner.setModel(new SpinnerNumberModel(((Enemy) entity).getIntelligence().getRating() * 100,
						0, 100, 1));
				intRateSpinner.setBounds(10, 67, 57, 20);
				intelligencePanel.add(intRateSpinner);

				viewAngleSpinner = new JSpinner();
				viewAngleSpinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entity).getIntelligence().setViewAngle((int) viewAngleSpinner.getValue());
					}
				});
				viewAngleSpinner.setModel(new SpinnerNumberModel(((Enemy) entity).getIntelligence().getViewAngle(),
						0, 360, 1));
				viewAngleSpinner.setBounds(10, 123, 57, 20);
				intelligencePanel.add(viewAngleSpinner);

				viewDistanceSpinner = new JSpinner();
				viewDistanceSpinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						((Enemy) entity).getIntelligence().setViewDistance((int) viewDistanceSpinner.getValue());
					}
				});
				viewDistanceSpinner.setModel(new SpinnerNumberModel(((Enemy) entity).getIntelligence().getViewDistance(),
						1, null, 1));
				viewDistanceSpinner.setBounds(77, 123, 57, 20);
				intelligencePanel.add(viewDistanceSpinner);

				btnRemoveTrait.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(traitList.getSelectedIndex() != -1) {
							((DefaultListModel<Trait>) traitList.getModel()).removeElementAt(traitList.getSelectedIndex());
						}
					}
				});
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						String[] traits = new String[]{"Minion", "PackLeader", "Opportunist"};
						String result = (String) JOptionPane.showInputDialog(EntityDialog.this, "Select a trait", "Trait Builder",
								JOptionPane.QUESTION_MESSAGE, null, traits, traits[0]);
						if(result != null) {
							// TODO Clean this up
							switch(result) {
							case "Minion":
								MinionBuilder mBuilder = new MinionBuilder(stage);
								Trait minion = mBuilder.getMinion();
								if(minion != null) {
									boolean placed = false;
									for(int i = 0; i<traitList.getModel().getSize(); i++) {
										if(traitList.getModel().getElementAt(i) instanceof Minion) {
											((DefaultListModel<Trait>) traitList.getModel()).set(i, minion);
											placed = true;
											break;
										}
									}
									if(!placed) {
										((DefaultListModel<Trait>) traitList.getModel()).addElement(minion);
									}
								}
								break;
							case "PackLeader":
								PackLeaderBuilder plBuilder = new PackLeaderBuilder();
								Trait packLeader = plBuilder.getPackLeader();
								if(packLeader != null) {
									boolean placed = false;
									for(int i = 0; i<traitList.getModel().getSize(); i++) {
										if(traitList.getModel().getElementAt(i) instanceof PackLeader) {
											((DefaultListModel<Trait>) traitList.getModel()).set(i, packLeader);
											placed = true;
											break;
										}
									}
									if(!placed) {
										((DefaultListModel<Trait>) traitList.getModel()).addElement(packLeader);
									}
								}
								break;
							case "Opportunist":
								OpportunistBuilder oBuilder = new OpportunistBuilder();
								Trait opportunist = oBuilder.getOpportunist();
								if(opportunist != null) {
									boolean placed = false;
									for(int i = 0; i<traitList.getModel().getSize(); i++) {
										if(traitList.getModel().getElementAt(i) instanceof Opportunist) {
											((DefaultListModel<Trait>) traitList.getModel()).set(i, opportunist);
											placed = true;
											break;
										}
									}
									if(!placed) {
										((DefaultListModel<Trait>) traitList.getModel()).addElement(opportunist);
									}
								}
								break;
							default:
								break;
							}
						}
					}
				});

				JPanel repPanel = new JPanel();
				tabbedPane.addTab("Reputation", null, repPanel, null);
				repPanel.setLayout(null);

				JScrollPane scrollPane_1 = new JScrollPane();
				scrollPane_1.setBounds(10, 11, 151, 188);
				repPanel.add(scrollPane_1);

				JScrollPane scrollPane_3 = new JScrollPane();
				scrollPane_3.setBounds(337, 43, 182, 156);
				repPanel.add(scrollPane_3);

				enemyList = new JList<Faction>();
				DefaultListModel<Faction> enemiesModel = new DefaultListModel<Faction>();
				for(Faction f : ((Enemy) entity).getReputation().getEnemies()) {
					enemiesModel.addElement(f);
				}
				enemyList.setModel(enemiesModel);
				scrollPane_3.setViewportView(enemyList);

				JLabel lblEnemies = new JLabel("Enemies");
				scrollPane_3.setColumnHeaderView(lblEnemies);

				JScrollPane scrollPane_2 = new JScrollPane();
				scrollPane_2.setBounds(171, 43, 151, 156);
				repPanel.add(scrollPane_2);

				allyList = new JList<Faction>();
				scrollPane_2.setViewportView(allyList);
				DefaultListModel<Faction> alliesModel = new DefaultListModel<Faction>();
				for(Faction f : ((Enemy) entity).getReputation().getAllies()) {
					alliesModel.addElement(f);
				}
				allyList.setModel(alliesModel);

				JLabel lblAllies = new JLabel("Allies");
				scrollPane_2.setColumnHeaderView(lblAllies);
				
				final JList<Faction> factionList = new JList<Faction>();
				DefaultListModel<Faction> factionModel = new DefaultListModel<Faction>();
				for(Faction f : Faction.values()) {
					if(!alliesModel.contains(f) && !enemiesModel.contains(f)) {
						factionModel.addElement(f);
					}
				}
				factionList.setModel(factionModel);
				scrollPane_1.setViewportView(factionList);

				JButton btnAlly = new JButton("Ally");
				btnAlly.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(factionList.getSelectedIndex() != -1) {
							for(Faction f : factionList.getSelectedValuesList()) {
								((Enemy) entity).getReputation().addAlly(f);
								((DefaultListModel<Faction>) allyList.getModel()).addElement(f);
								((DefaultListModel<Faction>) factionList.getModel()).removeElement(f);
							}
						}
					}
				});
				btnAlly.setBounds(171, 9, 49, 23);
				repPanel.add(btnAlly);

				JButton btnEnemy = new JButton("Enemy");
				btnEnemy.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(factionList.getSelectedIndex() != -1) {
							for(Faction f : factionList.getSelectedValuesList()) {
								((Enemy) entity).getReputation().addEnemy(f);
								((DefaultListModel<Faction>) enemyList.getModel()).addElement(f);
								((DefaultListModel<Faction>) factionList.getModel()).removeElement(f);
							}
						}
					}
				});
				btnEnemy.setBounds(337, 9, 65, 23);
				repPanel.add(btnEnemy);

				JButton btnRemove = new JButton("Remove Ally");
				btnRemove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(allyList.getSelectedIndex() != -1) {
							for(Faction f : factionList.getSelectedValuesList()) {
								((Enemy) entity).getReputation().getAllies().remove(f);
								((DefaultListModel<Faction>) factionList.getModel()).addElement(f);
								((DefaultListModel<Faction>) allyList.getModel()).removeElement(f);
							}
						}
					}
				});
				btnRemove.setBounds(230, 9, 91, 23);
				repPanel.add(btnRemove);

				JButton btnRemoveEnemy = new JButton("Remove Enemy");
				btnRemoveEnemy.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(enemyList.getSelectedIndex() != -1) {
							for(Faction f : factionList.getSelectedValuesList()) {
								((Enemy) entity).getReputation().getEnemies().remove(f);
								((DefaultListModel<Faction>) factionList.getModel()).addElement(f);
								((DefaultListModel<Faction>) enemyList.getModel()).removeElement(f);
							}
						}
					}
				});
				btnRemoveEnemy.setBounds(412, 9, 107, 23);
				repPanel.add(btnRemoveEnemy);

				JPanel equipPanel = new JPanel();
				tabbedPane.addTab("Equipment", null, equipPanel, null);
				equipPanel.setLayout(null);
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
						float xDistance = (float) xSpinner.getValue() - entity.getX();
						float yDistance = (float) ySpinner.getValue() - entity.getY();
						entity.movePosition(xDistance, yDistance);

						if(entity instanceof Backdrop) {
							((Backdrop) entity).setDepth((float) depthSpinner.getValue() / 100f);
						} else if(entity instanceof Actor) {
							((Actor) entity).setDirection(directionToggle.isSelected() ? 1 : 0);
							((Actor) entity).setMaxSpeed((float) speedSpinner.getValue());
							if(entity instanceof Enemy) {
								((Enemy) entity).getStats().setHealth(new Health((float) currentHealth.getValue(), (float) maxHealth.getValue()));
								((Enemy) entity).getStats().setStamina(new Stamina((float) currentStamina.getValue(), (float) maxStamina.getValue()));
								((Enemy) entity).getStats().setMagic(new Magic((float) currentMagic.getValue(), (float) maxMagic.getValue()));

								((Enemy) entity).getIntelligence().setViewAngle((int) viewAngleSpinner.getValue());
								((Enemy) entity).getIntelligence().setViewDistance((int) viewDistanceSpinner.getValue());
								for(int i = 0; i<traitList.getModel().getSize(); i++) {
									((Enemy) entity).getIntelligence().addTrait(traitList.getModel().getElementAt(i));
								}
							}
						}

						setVisible(false);
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
						resetToDefault();

						setVisible(false);
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public Entity showEntityDialog() {
		setVisible(true);
		
		return entity;
	}
	
	private void resetToDefault() {
		entity.setPosition(defaultEntity.getX(), defaultEntity.getY());

		if(entity instanceof Backdrop) {
			((Backdrop) entity).setDepth(((Backdrop) defaultEntity).getDepth());
		} else if(entity instanceof Actor) {
			((Actor) entity).setDirection(((Actor) defaultEntity).getDirection());
			((Actor) entity).setMaxSpeed(((Actor) defaultEntity).getMaxSpeed());
			if(entity instanceof Enemy) {
				((Enemy) entity).getStats().setHealth(((Enemy) defaultEntity).getStats().getHealth());
				((Enemy) entity).getStats().setStamina(((Enemy) defaultEntity).getStats().getStamina());
				((Enemy) entity).getStats().setMagic(((Enemy) defaultEntity).getStats().getMagic());

				((Enemy) entity).changeIntelligence(((Enemy) defaultEntity).getIntelligence());
				((Enemy) entity).getIntelligence().setViewAngle(((Enemy) defaultEntity).getIntelligence().getViewAngle());
				((Enemy) entity).getIntelligence().setViewDistance(((Enemy) defaultEntity).getIntelligence().getViewDistance());

				((Enemy) entity).getReputation().setAllies(((Enemy) defaultEntity).getReputation().getAllies());
				((Enemy) entity).getReputation().setEnemies(((Enemy) defaultEntity).getReputation().getEnemies());
			}
		}
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
	public JList<Trait> getTraitList() {
		return traitList;
	}
	public JList<Faction> getAllyList() {
		return allyList;
	}
	public JList<Faction> getEnemyList() {
		return enemyList;
	}
	public JComboBox<String> getAiCombo() {
		return aiCombo;
	}
	public JSpinner getSpeedSpinner() {
		return speedSpinner;
	}
	public JSpinner getIntRateSpinner() {
		return intRateSpinner;
	}
	public JSpinner getViewRadiusSpinner() {
		return viewAngleSpinner;
	}
	public JSpinner getViewDistanceSpinner() {
		return viewDistanceSpinner;
	}
}
