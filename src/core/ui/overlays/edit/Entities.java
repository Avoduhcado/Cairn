package core.ui.overlays.edit;

import core.Camera;
import core.Input;
import core.entities.Prop;
import core.scene.Map;
import core.ui.Button;
import core.ui.CheckBox;
import core.ui.ElementGroup;
import core.ui.InputBox;
import core.ui.TextBox;
import core.ui.utils.Align;
import core.ui.utils.ClickEvent;
import core.ui.utils.ValueChangeEvent;
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
	private TextBox propList;
	
	private boolean grabbing;

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
		grabProp.addEvent(new ClickEvent(grabProp) {

			@Override
			public void click() {
				grabbing = grabProp.isChecked();
			}
			
		});
		add(grabProp);
		
		name = new InputBox(null, 20, (float) grabProp.getBounds().getMaxY(), null, 0, 0);
		name.setStill(true);
		name.setCentered(false);
		name.setEnabled(false);
		name.addEvent(new ValueChangeEvent(name) {

			@Override
			public void changeValue() {
				Entities.this.map.addProp(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY(), name.getText());
				propList.addText(";" + name.getText());
				name.setEnabled(false);
			}
			
		});
		add(name);
		
		addFrame("Menu2");
		
		propList = new TextBox("Props:", Camera.get().getDisplayWidth(0.985f), 20, "Menu2", false);
		propList.setStill(true);
		for(Prop p : map.getProps()) {
			propList.addText(";" + p.getName());
		}
		propList.setAlign(Align.LEFT);
		add(propList);
	}
	
	@Override
	public void update() {
		super.update();
		
		if(grabbing && Input.mouseHeld()) {
			for(Prop p : map.getProps()) {
				if(p.getBox().contains(MouseInput.getScreenMouseX(), MouseInput.getScreenMouseY())) {
					p.setPosition((float) (MouseInput.getScreenMouseX() - (p.getBox().getWidth() / 2f)),
							(float) (MouseInput.getScreenMouseY() - (p.getBox().getHeight() / 2f)));
					break;
				}
			}
		}
	}
	
}
