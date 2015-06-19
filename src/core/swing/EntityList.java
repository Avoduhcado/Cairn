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
import javax.swing.JList;

import core.entities.Entity;
import core.entities.Prop;
import core.scene.Map;
import core.ui.overlays.edit.Entities;
import core.utilities.mouse.MouseInput;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class EntityList extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTree propTree;
	
	private Map map;

	/**
	 * Create the frame.
	 */
	public EntityList(Map map) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		this.map = map;
		setTitle("Entity List");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 500);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmLoad = new JMenuItem("Load Entity");
		mntmLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/sprites"));
				chooser.setDialogTitle("Select an entity to load");
				int returnVal = chooser.showOpenDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					EntityList.this.map.addProp(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY(),
							chooser.getSelectedFile().getName());
				}
			}
		});
		mnFile.add(mntmLoad);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmUndo = new JMenuItem("Undo");
		mnEdit.add(mntmUndo);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel propTab = new JPanel();
		tabbedPane.addTab("Props", null, propTab, null);
		propTab.setLayout(new BorderLayout(0, 0));
		
		propTree = new JTree();
		propTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				System.out.println("Shit dicks" + e.getPaths());
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) propTree.getLastSelectedPathComponent();

				if (node == null)
					//Nothing is selected.     
					return;
				
				NodeInfo nodeInfo = null;
				
				if(node.isLeaf()) {
					nodeInfo = (NodeInfo) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
				} else {
					nodeInfo = (NodeInfo) node.getUserObject();
				}
				
				if(nodeInfo.getValue() != null) {
					((Prop) nodeInfo.getValue()).setDebug(e.isAddedPath());
				}
				/*if(e.getOldLeadSelectionPath() != null) {
					NodeInfo oldNode = (NodeInfo) ((DefaultMutableTreeNode) e.getOldLeadSelectionPath().getLastPathComponent()).getUserObject();
					((Prop) oldNode.getValue()).setDebug(false);
				}*/
				
				/*NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
				
				if(e.getOldLeadSelectionPath() != null && nodeInfo.getValue() instanceof Prop) {
					((Prop) nodeInfo.getValue()).setDebug(false);
				}
				if(nodeInfo.getValue() instanceof Prop) {
					((Prop) nodeInfo.getValue()).setDebug(e.isAddedPath());
				}*/
			}
		});
		propTree.setModel(new DefaultTreeModel(populateTree(map.getProps())));
		propTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		propTree.setShowsRootHandles(true);
		propTab.add(propTree, BorderLayout.CENTER);
		
		JPanel actorTab = new JPanel();
		tabbedPane.addTab("Actors", null, actorTab, null);
		actorTab.setLayout(new BorderLayout(0, 0));
		
		JTree actorList = new JTree();
		actorTab.add(actorList, BorderLayout.CENTER);
		
		JPanel backdropTab = new JPanel();
		tabbedPane.addTab("Backdrops", null, backdropTab, null);
		backdropTab.setLayout(new BorderLayout(0, 0));
		
		JTree backdropList = new JTree();
		backdropTab.add(backdropList, BorderLayout.CENTER);
		
		//setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}
	
	public DefaultMutableTreeNode populateTree(LinkedList<Prop> props) {
		DefaultMutableTreeNode root = null;
		DefaultMutableTreeNode node = null;
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new NodeInfo("Props", null));
		
		for(Prop p : props) {
			root = new DefaultMutableTreeNode(new NodeInfo(p.getID(), p));
			node = new DefaultMutableTreeNode(new NodeInfo("xPos", p.getX()));
			root.add(node);
			node = new DefaultMutableTreeNode(new NodeInfo("yPos", p.getX()));
			root.add(node);
			top.add(root);
		}
		
		return top;
	}

	public JTree getPropTree() {
		return propTree;
	}
}
