package core.swing;

import java.awt.BorderLayout;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JTabbedPane;
import javax.swing.JList;

import core.entities.Prop;
import core.scene.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

public class EntityList extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTree propTree;

	/**
	 * Create the frame.
	 */
	public EntityList(Map map) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setTitle("Entity List");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 500);
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
				if(e.getOldLeadSelectionPath() != null &&
						((DefaultMutableTreeNode) e.getOldLeadSelectionPath().getLastPathComponent()).getUserObject() instanceof Prop) {
					((Prop) ((DefaultMutableTreeNode) e.getOldLeadSelectionPath().getLastPathComponent()).getUserObject()).setDebug(false);
				}
				if(((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject() instanceof Prop) {
					((Prop) ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject()).setDebug(e.isAddedPath());
				}
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
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Props");
		
		for(Prop p : props) {
			root = new DefaultMutableTreeNode(p);
			top.add(root);
		}
		
		return top;
	}

	public JTree getPropTree() {
		return propTree;
	}
}
