package core.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import core.entities_new.Entity;
import core.entities_new.EntityComponent;

import javax.swing.JList;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class EntityDisplay extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private Entity entity;
	private JList<String> componentData;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			EntityDisplay dialog = new EntityDisplay(null);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EntityDisplay(Entity entity) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		if(entity != null) {
			setTitle(entity.getName());
			this.entity = entity;
		}
		
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				setComponentList(EntityDisplay.this.entity);
			}
		});
		
		setBounds(100, 100, 446, 512);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			componentData = new JList<String>();
			setComponentList(entity);
			contentPanel.add(componentData, BorderLayout.CENTER);
		}
	}

	public JList<String> getComponentList() {
		return componentData;
	}
	
	private void setComponentList(Entity entity) {
		if(entity == null) {
			componentData.setModel(null);
			return;
		}
		
		DefaultListModel<String> listModel = new DefaultListModel<>();
		
		listModel.addElement("Body: " + entity.getZBody() + ", " + entity.getZBody().getPosition());
		
		for(@SuppressWarnings("rawtypes") Class clazz : entity.getComponents().keySet()) {
			listModel.addElement(clazz.getSimpleName() + ": " + entity.getComponent(clazz).toString());
		}
		
		componentData.setModel(listModel);
	}
}
