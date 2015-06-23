package core.swing;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.FlowLayout;

import javax.swing.JCheckBoxMenuItem;

import core.setups.Stage;
import core.ui.overlays.EditMenu;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class EditorMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private EditMenu editMenu;
	private Stage stage;
	private JCheckBoxMenuItem chckbxmntmEntityList;

	/**
	 * Create the frame.
	 */
	public EditorMain(EditMenu editMenu, Stage stage) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		this.editMenu = editMenu;
		this.stage = stage;
		setTitle("Editor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(600, 100, 455, 80);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				EditorMain.this.editMenu.close();
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditorMain.this.stage.loadMap(null, 0, 0);
				EditorMain.this.editMenu.setMap(EditorMain.this.stage.getMap());
				EditorMain.this.editMenu.clearEntityList();
			}
		});
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		// TODO Load all entities into EntityList
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(null, "avo");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/maps/"));
				chooser.setDialogTitle("Select a Map to load");
				int returnVal = chooser.showOpenDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					EditorMain.this.stage.loadMap(chooser.getSelectedFile().getName().replace(".avo", ""), 0, 0);
					EditorMain.this.editMenu.setMap(EditorMain.this.stage.getMap());
				}
			}
		});
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(EditorMain.this.stage.getMap().getMapName() == null) {
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter(null, "avo");
					chooser.setFileFilter(filter);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/maps/"));
					chooser.setDialogTitle("Save Map");
					int returnVal = chooser.showSaveDialog(null);
					
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						EditorMain.this.stage.getMap().setMapName(chooser.getSelectedFile().getName());
						EditorMain.this.stage.getMap().serialize();
					}
				} else {
					EditorMain.this.stage.getMap().serialize();
				}
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as...");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(null, "avo");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/maps/"));
				chooser.setDialogTitle("Save Map");
				int returnVal = chooser.showSaveDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					EditorMain.this.stage.getMap().setMapName(chooser.getSelectedFile().getName());
					EditorMain.this.stage.getMap().serialize();
				}
			}
		});
		mnFile.add(mntmSaveAs);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		chckbxmntmEntityList = new JCheckBoxMenuItem("Entity List");
		chckbxmntmEntityList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxmntmEntityList.isSelected()) {
					EditorMain.this.editMenu.openEntityList();
				} else {
					EditorMain.this.editMenu.closeEntityList();
				}
			}
		});
		chckbxmntmEntityList.setSelected(true);
		mnView.add(chckbxmntmEntityList);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		setVisible(true);
	}

	public JCheckBoxMenuItem getChckbxmntmEntityList() {
		return chckbxmntmEntityList;
	}
}
