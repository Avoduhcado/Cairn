package core.ui.overlays.edit;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.Camera;
import core.Input;
import core.entities.Entity;
import core.entities.Prop;
import core.scene.Map;
import core.ui.Button;
import core.ui.CheckBox;
import core.ui.ElementGroup;
import core.ui.Label;
import core.ui.UIElement;
import core.ui.utils.Align;
import core.ui.utils.ClickEvent;
import core.utilities.keyboard.Keybinds;
import core.utilities.mouse.MouseInput;

public class Entities extends ElementGroup<UIElement> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map map;
	
	private Button addProp;
	private CheckBox grabProp;
	
	private ElementGroup<CheckBoxEntity> propList;
	private Label propLabel;
	
	private boolean groupSelect;

	public Entities(Map map) {
		this.map = map;
		
		addProp = new Button("Add Prop", 20, Camera.get().getDisplayHeight(0.35f), 0, null);
		addProp.setStill(true);
		addProp.addEvent(new ClickEvent(addProp) {
			public void click() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(new File(System.getProperty("resources") + "/sprites"));
				chooser.setDialogTitle("Open a background image");
				int returnVal = chooser.showOpenDialog(null);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					Entities.this.map.addProp(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY(),
							chooser.getSelectedFile().getName());
					setupProps();
				}
			}
		});
		add(addProp);
		
		grabProp = new CheckBox("Grab Prop", 20, (float) addProp.getBounds().getMaxY(), null);
		grabProp.setStill(true);
		add(grabProp);
		
		addFrame("Menu2");
		
		propLabel = new Label("Props:", Camera.get().getDisplayWidth(0.985f), 20, "Menu2");
		propLabel.setStill(true);
		propLabel.setAlign(Align.LEFT);
		
		setupProps();
	}
	
	@Override
	public void update() {
		super.update();
		
		propList.update();
		
		if(Keybinds.CANCEL.clicked()) {
			for(int i = 0; i<propList.size(); i++) {
				if(propList.get(i).isChecked()) {
					map.getProps().remove(map.getProps().indexOf(propList.get(i).entity));
					propList.remove(i);
					i--;
					if(i < propList.size()) {
						for(int j = i; j < propList.size(); j++) {
							propList.get(j).setBounds(propList.get(j).getBounds().getX(), propList.get(j - 1).getBounds().getMaxY(),
									propList.get(j).getBounds().getWidth(), propList.get(j).getBounds().getHeight());
						}
						propList.addFrame("Menu2");
					}
				}
			}
		}
		
		if(grabProp.isChecked() && Input.mouseHeld()) {
			for(Prop p : map.getProps()) {
				if(p.getBox().contains(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY())) {
					p.setPosition((float) (MouseInput.getScreenMouseX() - (p.getBox().getWidth() / 2f)),
							(float) (MouseInput.getScreenMouseY() - (p.getBox().getHeight() / 2f)));
					break;
				}
			}
		} else if(groupSelect && Input.mouseHeld()) {
			for(CheckBoxEntity e : propList) {
				if(e.isChecked()) {
					map.getProps().get(map.getProps().indexOf(e.entity)).movePosition(Input.mouseDelta.x / Camera.get().getScale(),
							Input.mouseDelta.y / Camera.get().getScale());
				}
			}
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		
		propLabel.draw();
		propList.draw();
	}
	
	private void setupProps() {
		propList = new ElementGroup<CheckBoxEntity>();
				
		for(Prop p : map.getProps()) {
			final CheckBoxEntity propCheck = new CheckBoxEntity(p.getID(), Camera.get().getDisplayWidth(0.985f),
					(float) (propList.isEmpty() ? propLabel.getBounds().getMaxY() : propList.get(propList.size() - 1).getBounds().getMaxY()),
					null, p);
			propCheck.setStill(true);
			propCheck.setAlign(Align.LEFT);
			propCheck.addEvent(new ClickEvent(propCheck) {
				public void click() {
					grabProp.setChecked(false);
					groupSelect = true;
					propCheck.entity.setDebug(!propCheck.entity.isDebug());
				}
			});
			propList.add(propCheck);
		}
		
		propList.addFrame("Menu2");
	}
	
}

class CheckBoxEntity extends CheckBox {

	protected Entity entity;
	
	public CheckBoxEntity(String text, float x, float y, String image, Entity entity) {
		super(text, x, y, image);
		this.entity = entity;
	}
	
}