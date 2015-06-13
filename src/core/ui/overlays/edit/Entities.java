package core.ui.overlays.edit;

import core.Camera;
import core.Input;
import core.entities.Prop;
import core.scene.Map;
import core.ui.Button;
import core.ui.CheckBox;
import core.ui.ElementGroup;
import core.ui.InputBox;
import core.ui.Label;
import core.ui.utils.Align;
import core.ui.utils.ClickEvent;
import core.ui.utils.ValueChangeEvent;
import core.utilities.keyboard.Keybinds;
import core.utilities.mouse.MouseInput;

public class Entities extends ElementGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map map;
	
	private Button addProp;
	private CheckBox grabProp;
	private InputBox name;
	
	private ElementGroup propList;
	private Label propLabel;
	
	private boolean groupSelect;

	public Entities(Map map) {
		this.map = map;
		
		addProp = new Button("Add Prop", 20, Camera.get().getDisplayHeight(0.35f), 0, null);
		addProp.setStill(true);
		addProp.addEvent(new ClickEvent(addProp) {

			@Override
			public void click() {
				name.setEnabled(true);
			}
			
		});
		add(addProp);
		
		grabProp = new CheckBox("Grab Prop", 20, (float) addProp.getBounds().getMaxY(), null);
		grabProp.setStill(true);
		add(grabProp);
		
		name = new InputBox(null, 20, (float) grabProp.getBounds().getMaxY(), null, 0, 0);
		name.setStill(true);
		name.setCentered(false);
		name.setEnabled(false);
		name.addEvent(new ValueChangeEvent(name) {

			@Override
			public void changeValue() {
				Entities.this.map.addProp(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY(), name.getText());
				propList.add(new CheckBox(Entities.this.map.getProps().getLast().getID(), Camera.get().getDisplayWidth(0.985f),
						(float) propLabel.getBounds().getMaxY(), null));
				propList.addFrame("Menu2");
				name.setEnabled(false);
			}
			
		});
		add(name);
		
		addFrame("Menu2");
		
		propList = new ElementGroup();
		
		propLabel = new Label("Props:", Camera.get().getDisplayWidth(0.985f), 20, null);
		propLabel.setStill(true);
		propLabel.setAlign(Align.LEFT);
		propList.add(propLabel);
		
		for(Prop p : map.getProps()) {
			CheckBox propCheck = new CheckBox(p.getID(), Camera.get().getDisplayWidth(0.985f),
					(float) propList.get(propList.size() - 1).getBounds().getMaxY(), null);
			propCheck.setStill(true);
			propCheck.setAlign(Align.LEFT);
			propCheck.addEvent(new ClickEvent(propCheck) {

				@Override
				public void click() {
					grabProp.setChecked(false);
					groupSelect = true;
				}
				
			});
			propList.add(propCheck);
		}
		
		propList.addFrame("Menu2");
	}
	
	@Override
	public void update() {
		super.update();
		
		propList.update();
		
		if(Keybinds.CANCEL.clicked()) {
			for(int i = 1; i < propList.size(); i++) {
				if(((CheckBox) propList.get(i)).isChecked()) {
					map.removeProp(((CheckBox) propList.get(i)).getText());
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
			for(int i = 1; i < propList.size(); i++) {
				if(((CheckBox) propList.get(i)).isChecked()) {
					map.getProp(((CheckBox) propList.get(i)).getText()).movePosition(Input.mouseDelta.x, Input.mouseDelta.y);
				}
			}
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		
		propList.draw();
	}
	
}
