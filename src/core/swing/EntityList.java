package core.swing;

import java.awt.BorderLayout;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JTabbedPane;

import core.entities.Actor;
import core.entities.Ally;
import core.entities.Backdrop;
import core.entities.Enemy;
import core.entities.Entity;
import core.entities.Prop;
import core.scene.Map;
import core.setups.Stage;
import core.ui.overlays.EditMenu;
import core.utilities.mouse.MouseInput;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EntityList extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTree propTree;
	private JTree actorTree;
	private JTree backdropTree;
	
	private Map map;
	private EditMenu editMenu;
	private JTabbedPane tabbedPane;

	/**
	 * Create the frame.
	 */
	public EntityList(final Stage stage, EditMenu editMenu) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		this.map = stage.getMap();
		this.editMenu = editMenu;
		setTitle("Entity List");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 300, 500);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				EntityList.this.editMenu.closeEntityList();
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnLoad = new JMenu("Load");
		menuBar.add(mnLoad);
		
		JMenuItem mntmLoadGround = new JMenuItem("Ground...");
		mntmLoadGround.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/sprites/backdrops/"));
				chooser.setDialogTitle("Select a Ground to load");
				int returnVal = chooser.showOpenDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					EntityList.this.map.loadBackdrop(0, 0, chooser.getSelectedFile().getName(), 0f);
				}
			}
		});
		mnLoad.add(mntmLoadGround);
		
		mnLoad.addSeparator();
		
		JMenuItem mntmLoad = new JMenuItem("Prop...");
		mntmLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/sprites/props/"));
				chooser.setDialogTitle("Select a prop to load");
				int returnVal = chooser.showOpenDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					propTree.setModel(new DefaultTreeModel(addToPropTree((DefaultMutableTreeNode) propTree.getModel().getRoot(),
							EntityList.this.map.loadProp(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY(),
									chooser.getSelectedFile().getName()))));
					tabbedPane.setSelectedIndex(0);
				}
			}
		});
		mnLoad.add(mntmLoad);
		
		mnLoad.addSeparator();
		
		JMenuItem mntmLoadAlly = new JMenuItem("Ally...");
		mntmLoadAlly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/sprites/actors/"));
				chooser.setDialogTitle("Select an Ally to load");
				int returnVal = chooser.showOpenDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					actorTree.setModel(new DefaultTreeModel(addToActorTree((DefaultMutableTreeNode) actorTree.getModel().getRoot(),
							EntityList.this.map.loadActor(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY(),
									chooser.getSelectedFile().getName(), 0))));
					tabbedPane.setSelectedIndex(1);
				}
			}
		});
		mnLoad.add(mntmLoadAlly);
		
		JMenuItem mntmLoadEnemy = new JMenuItem("Enemy...");
		mntmLoadEnemy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/sprites/actors/"));
				chooser.setDialogTitle("Select an Enemy to load");
				int returnVal = chooser.showOpenDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					actorTree.setModel(new DefaultTreeModel(addToActorTree((DefaultMutableTreeNode) actorTree.getModel().getRoot(),
							EntityList.this.map.loadActor(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY(),
									chooser.getSelectedFile().getName(), 1))));
					tabbedPane.setSelectedIndex(1);
				}
			}
		});
		mnLoad.add(mntmLoadEnemy);
		
		mnLoad.addSeparator();
		
		JMenuItem mntmLoadBackground = new JMenuItem("Background...");
		mntmLoadBackground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/sprites/backdrops/"));
				chooser.setDialogTitle("Select a Background to load");
				int returnVal = chooser.showOpenDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					backdropTree.setModel(new DefaultTreeModel(addToBackdropTree((DefaultMutableTreeNode) backdropTree.getModel().getRoot(),
							EntityList.this.map.loadBackdrop(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY(),
									chooser.getSelectedFile().getName(), -0.1f))));
					tabbedPane.setSelectedIndex(2);
				}
			}
		});
		mnLoad.add(mntmLoadBackground);
		
		JMenuItem mntmLoadForeground = new JMenuItem("Foreground...");
		mntmLoadForeground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/sprites/backdrops/"));
				chooser.setDialogTitle("Select a Foreground to load");
				int returnVal = chooser.showOpenDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					backdropTree.setModel(new DefaultTreeModel(addToBackdropTree((DefaultMutableTreeNode) backdropTree.getModel().getRoot(),
							EntityList.this.map.loadBackdrop(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY(),
									chooser.getSelectedFile().getName(), 0.1f))));
					tabbedPane.setSelectedIndex(2);
				}
			}
		});
		mnLoad.add(mntmLoadForeground);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmUndo = new JMenuItem("Undo");
		mntmUndo.setEnabled(false);
		mnEdit.add(mntmUndo);
		
		mnEdit.addSeparator();
		
		JMenuItem mntmSelection = new JMenuItem("Selection");
		mntmSelection.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = null;
				
				switch(tabbedPane.getSelectedIndex()) {
				case 0:
					if(propTree.getSelectionPath() != null) {
						node = ((DefaultMutableTreeNode) propTree.getSelectionPath().getLastPathComponent());
					}
					break;
				case 1:
					if(actorTree.getSelectionPath() != null) {
						node = ((DefaultMutableTreeNode) actorTree.getSelectionPath().getLastPathComponent());
					}
					break;
				case 2:
					if(backdropTree.getSelectionPath() != null) {
						node = ((DefaultMutableTreeNode) backdropTree.getSelectionPath().getLastPathComponent());
					}
					break;
				}
				
				if(node == null) {
					return;
				}
				
				if(node.isLeaf()) {
					node = (DefaultMutableTreeNode) node.getParent();
				}
				
				LinkedList<Entity> entities = new LinkedList<Entity>();
				if(((NodeInfo) node.getUserObject()).getValue() instanceof Entity) {
					entities.add((Entity) ((NodeInfo) node.getUserObject()).getValue());
				} else if(((NodeInfo) node.getUserObject()).getValue() instanceof LinkedList) {
					entities.addAll(((LinkedList<? extends Entity>) ((NodeInfo) node.getUserObject()).getValue()));
				}
				
				if(!entities.isEmpty()) {
					new EntityDialog(entities, stage);
					// TODO Call initXXXXData after a successful EntityDialog
				}
			}
		});
		mnEdit.add(mntmSelection);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// TODO Unselect entities between tab swaps
			}
		});
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel propTab = new JPanel();
		tabbedPane.addTab("Props", null, propTab, null);
		propTab.setLayout(new BorderLayout(0, 0));
		
		propTree = new JTree();
		propTree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_DELETE) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) propTree.getLastSelectedPathComponent();
					if(node == null || node.isLeaf()) {
						return;
					}
					
					NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
					
					if(nodeInfo.getValue() instanceof Prop) {
						EntityList.this.map.removeEntity((Entity) nodeInfo.getValue());
					} else if(nodeInfo.getValue() instanceof LinkedList<?>) {
						LinkedList<?> props = (LinkedList<?>) nodeInfo.getValue();
						for(Object o : props) {
							EntityList.this.map.removeEntity((Prop) o);
						}
					}
					
					((DefaultTreeModel) propTree.getModel()).removeNodeFromParent(node);
				}
			}
		});
		propTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {				
				DefaultMutableTreeNode previousNode = null;
				if(e.getOldLeadSelectionPath() != null) {
					previousNode = (DefaultMutableTreeNode) e.getOldLeadSelectionPath().getLastPathComponent();
					NodeInfo previousInfo = (NodeInfo) previousNode.getUserObject();
					if(previousNode.isLeaf()) {
						previousInfo = (NodeInfo) ((DefaultMutableTreeNode) previousNode.getParent()).getUserObject();
					}
					
					if(previousInfo.getValue() instanceof Prop) {
						((Prop) previousInfo.getValue()).setDebug(false);
					} else if(previousInfo.getValue() instanceof LinkedList<?>) {
						LinkedList<?> props = (LinkedList<?>) previousInfo.getValue();
						for(Object p : props) {
							((Prop) p).setDebug(false);
						}
					}
				}
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) propTree.getLastSelectedPathComponent();
				if(node != null) {
					NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
					if(node.isLeaf() && node.getParent() != null) {
						nodeInfo = (NodeInfo) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
					}
					
					if(nodeInfo.getValue() instanceof Prop) {
						((Prop) nodeInfo.getValue()).setDebug(e.isAddedPath());
					} else if(nodeInfo.getValue() instanceof LinkedList<?>) {
						LinkedList<?> props = (LinkedList<?>) nodeInfo.getValue();
						for(Object p : props) {
							((Prop) p).setDebug(e.isAddedPath());
						}
					}
				}
			}
		});
		propTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(propTree.getPathForLocation(e.getX(), e.getY()) != null) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) propTree.getPathForLocation(e.getX(), e.getY()).getLastPathComponent();
					
					if(propTree.getRowForLocation(e.getX(), e.getY()) != -1) {
						if(e.getClickCount() == 2 && node.isLeaf()) {
							
						}
					}
				}
			}
		});
		propTree.setModel(new DefaultTreeModel(populatePropTree(map.getProps())));
		propTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		propTree.setShowsRootHandles(true);
		propTab.add(propTree, BorderLayout.CENTER);
		
		JPanel actorTab = new JPanel();
		tabbedPane.addTab("Actors", null, actorTab, null);
		actorTab.setLayout(new BorderLayout(0, 0));
		
		actorTree = new JTree();
		actorTree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_DELETE) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) actorTree.getLastSelectedPathComponent();
					if(node == null || node.isLeaf()) {
						return;
					}
					
					NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
					
					if(nodeInfo.getValue() instanceof Actor) {
						EntityList.this.map.removeEntity((Entity) nodeInfo.getValue());
					} else if(nodeInfo.getValue() instanceof LinkedList<?>) {
						LinkedList<?> actors = (LinkedList<?>) nodeInfo.getValue();
						for(Object o : actors) {
							EntityList.this.map.removeEntity((Actor) o);
						}
					}
					
					((DefaultTreeModel) actorTree.getModel()).removeNodeFromParent(node);
				}
			}
		});
		actorTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {				
				DefaultMutableTreeNode previousNode = null;
				if(e.getOldLeadSelectionPath() != null) {
					previousNode = (DefaultMutableTreeNode) e.getOldLeadSelectionPath().getLastPathComponent();
					NodeInfo previousInfo = (NodeInfo) previousNode.getUserObject();
					if(previousNode.isLeaf()) {
						previousInfo = (NodeInfo) ((DefaultMutableTreeNode) previousNode.getParent()).getUserObject();
					}
					
					if(previousInfo.getValue() instanceof Actor) {
						((Actor) previousInfo.getValue()).setDebug(false);
					} else if(previousInfo.getValue() instanceof LinkedList<?>) {
						LinkedList<?> actors = (LinkedList<?>) previousInfo.getValue();
						for(Object a : actors) {
							((Actor) a).setDebug(false);
						}
					}
				}
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) actorTree.getLastSelectedPathComponent();
				if(node != null) {
					NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
					if(node.isLeaf()) {
						nodeInfo = (NodeInfo) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
					}
					
					if(nodeInfo.getValue() instanceof Actor) {
						((Actor) nodeInfo.getValue()).setDebug(e.isAddedPath());
					} else if(nodeInfo.getValue() instanceof LinkedList<?>) {
						LinkedList<?> actors = (LinkedList<?>) nodeInfo.getValue();
						for(Object a : actors) {
							((Actor) a).setDebug(e.isAddedPath());
						}
					}
				}
			}
		});
		actorTree.setModel(new DefaultTreeModel(populateActorTree(map.getCast())));
		actorTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		actorTree.setShowsRootHandles(true);
		actorTab.add(actorTree, BorderLayout.CENTER);
		
		JPanel backdropTab = new JPanel();
		tabbedPane.addTab("Backdrops", null, backdropTab, null);
		backdropTab.setLayout(new BorderLayout(0, 0));
		
		backdropTree = new JTree();
		backdropTree.setModel(new DefaultTreeModel(populateBackdropTree(map.getBackground())));
		backdropTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		backdropTree.setShowsRootHandles(true);
		backdropTab.add(backdropTree, BorderLayout.CENTER);
		
		//setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}
	
	public DefaultMutableTreeNode populatePropTree(LinkedList<Prop> props) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new NodeInfo("Props", props));
		
		return addToPropTree(top, props);
	}
	
	@SuppressWarnings("unchecked")
	public DefaultMutableTreeNode addToPropTree(DefaultMutableTreeNode top, LinkedList<Prop> props) {
		DefaultMutableTreeNode root = null;
		
		for(int i = 0; i<props.size(); i++) {
			root = initPropData(root, props.get(i)); 

			while(i < props.size() - 1) {
				String name1 = props.get(i).getName();
				String name2 = props.get(i + 1).getName();

				if(name1.startsWith(name2.substring(0, name2.lastIndexOf('[')))) {
					if(((NodeInfo) root.getUserObject()).getValue() instanceof Prop) {
						((NodeInfo) root.getUserObject()).setValue(new LinkedList<Prop>());
						((LinkedList<Prop>) ((NodeInfo) root.getUserObject()).getValue()).add(props.get(i));
					}

					i++;
					((LinkedList<Prop>) ((NodeInfo) root.getUserObject()).getValue()).add(props.get(i));
				} else {
					break;
				}
			}
			
			top.add(root);
		}
		
		return top;
	}
	
	public DefaultMutableTreeNode initPropData(DefaultMutableTreeNode root, Prop prop) {
		DefaultMutableTreeNode node = null;
		
		root = new DefaultMutableTreeNode(new NodeInfo(prop.getID(), prop));
		node = new DefaultMutableTreeNode(new NodeInfo("xPos", prop.getX()));
		root.add(node);
		node = new DefaultMutableTreeNode(new NodeInfo("yPos", prop.getY()));
		root.add(node);
		
		return root;
	}
	
	public DefaultMutableTreeNode populateActorTree(LinkedList<Actor> actors) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new NodeInfo("Actors", actors));
		
		return addToActorTree(top, actors);		
	}
	
	public DefaultMutableTreeNode addToActorTree(DefaultMutableTreeNode top, LinkedList<Actor> actors) {
		DefaultMutableTreeNode root = null;
		
		for(int i = 0; i<actors.size(); i++) {
			top.add(initActorData(root, actors.get(i)));
		}
				
		return top;
	}
	
	public DefaultMutableTreeNode initActorData(DefaultMutableTreeNode root, Actor actor) {
		DefaultMutableTreeNode node = null;
		
		root = new DefaultMutableTreeNode(new NodeInfo(actor.getID(), actor));
		node = new DefaultMutableTreeNode(new NodeInfo("xPos", actor.getX()));
		root.add(node);
		node = new DefaultMutableTreeNode(new NodeInfo("yPos", actor.getY()));
		root.add(node);
		node = new DefaultMutableTreeNode(new NodeInfo("Facing", actor.getDirection()));
		root.add(node);
		node = new DefaultMutableTreeNode(new NodeInfo("Speed", actor.getMaxSpeed()));
		root.add(node);
		if(actor instanceof Ally) {
			node = new DefaultMutableTreeNode(new NodeInfo("Dialogue", ((Ally) actor).getScript()));
			root.add(node);
		} else if(actor instanceof Enemy) {
			node = new DefaultMutableTreeNode(new NodeInfo("Stats", ((Enemy) actor).getStats()));
			root.add(node);
			node = new DefaultMutableTreeNode(new NodeInfo("Equip", ((Enemy) actor).getEquipment()));
			root.add(node);
			node = new DefaultMutableTreeNode(new NodeInfo("Intel", ((Enemy) actor).getIntelligence()));
			root.add(node);
			node = new DefaultMutableTreeNode(new NodeInfo("Rep", ((Enemy) actor).getReputation()));
			root.add(node);
		}
		
		return root;
	}
	
	public DefaultMutableTreeNode populateBackdropTree(LinkedList<Backdrop> backdrops) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new NodeInfo("Backdrops", backdrops));
		
		return addToBackdropTree(top, backdrops);		
	}
	
	@SuppressWarnings("unchecked")
	public DefaultMutableTreeNode addToBackdropTree(DefaultMutableTreeNode top, LinkedList<Backdrop> backdrops) {
		DefaultMutableTreeNode root = null;
		
		for(int i = 0; i<backdrops.size(); i++) {
			root = initBackdropData(root, backdrops.get(i)); 

			while(i < backdrops.size() - 1) {
				String name1 = backdrops.get(i).getName();
				String name2 = backdrops.get(i + 1).getName();

				if(name1.startsWith(name2.substring(0, name2.lastIndexOf('[')))) {
					if(((NodeInfo) root.getUserObject()).getValue() instanceof Backdrop) {
						((NodeInfo) root.getUserObject()).setValue(new LinkedList<Backdrop>());
						((LinkedList<Backdrop>) ((NodeInfo) root.getUserObject()).getValue()).add(backdrops.get(i));
					}

					i++;
					((LinkedList<Backdrop>) ((NodeInfo) root.getUserObject()).getValue()).add(backdrops.get(i));
				} else {
					break;
				}
			}
			
			top.add(root);
		}
		
		return top;
	}
	
	public DefaultMutableTreeNode initBackdropData(DefaultMutableTreeNode root, Backdrop backdrop) {
		DefaultMutableTreeNode node = null;
		
		root = new DefaultMutableTreeNode(new NodeInfo(backdrop.getID(), backdrop));
		node = new DefaultMutableTreeNode(new NodeInfo("xPos", backdrop.getX()));
		root.add(node);
		node = new DefaultMutableTreeNode(new NodeInfo("yPos", backdrop.getY()));
		root.add(node);
		node = new DefaultMutableTreeNode(new NodeInfo("Depth", backdrop.getDepth()));
		root.add(node);
		
		return root;
	}

	public boolean hasSelection() {
		switch(tabbedPane.getSelectedIndex()) {
		case 0:
			return propTree.getSelectionPath() != null;
		case 1:
			return actorTree.getSelectionPath() != null;
		case 2:
			return backdropTree.getSelectionPath() != null;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public LinkedList<Entity> getSelection() {
		if(hasSelection()) {
			DefaultMutableTreeNode node = null;
			switch(tabbedPane.getSelectedIndex()) {
			case 0:
				node = (DefaultMutableTreeNode) propTree.getLastSelectedPathComponent();
				break;
			case 1:
				node = (DefaultMutableTreeNode) actorTree.getLastSelectedPathComponent();
				break;
			case 2:
				node = (DefaultMutableTreeNode) backdropTree.getLastSelectedPathComponent();
				break;
			}
			
			if(node != null) {
				LinkedList<Entity> selection = new LinkedList<Entity>();
				NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
				
				if(node.isLeaf()) {
					nodeInfo = (NodeInfo) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
				}
				
				if(nodeInfo.getValue() instanceof Entity) {
					selection.add((Entity) nodeInfo.getValue());
					return selection;
				} else if(nodeInfo.getValue() instanceof LinkedList<?>) {
					return (LinkedList<Entity>) nodeInfo.getValue();
				}
			}
		}
		
		return null;
	}
	
	public void setMap(Map map) {
		this.map = map;
		
		propTree.setModel(new DefaultTreeModel(populatePropTree(map.getProps())));
		actorTree.setModel(new DefaultTreeModel(populateActorTree(map.getCast())));
		backdropTree.setModel(new DefaultTreeModel(populateBackdropTree(map.getBackground())));
	}

	public JTree getPropTree() {
		return propTree;
	}
	public JTree getActorTree() {
		return actorTree;
	}
	public JTree getBackdropTree() {
		return backdropTree;
	}
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
}
