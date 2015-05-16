package core.ui.overlays;

import core.Camera;
import core.Theater;
import core.render.DrawUtils;
import core.setups.TitleMenu;
import core.ui.Button;
import core.ui.ButtonGroup;
import core.utilities.keyboard.Keybinds;

public class GameMenu extends MenuOverlay {

	private ButtonGroup buttons;
	private OptionsMenu options;
	
	public GameMenu(float x, float y, String image) {
		super(x, y, image);
		
		buttons = new ButtonGroup(Float.NaN, Camera.get().getDisplayHeight(0.2f), "Menu2", true);
		buttons.setStill(true);
		buttons.addButton(new Button("Return to Game"));
		buttons.addButton(new Button("Options"));
		buttons.addButton(new Button("Quit to Title"));
		buttons.addButton(new Button("Quit to Desktop"));
	}
	
	@Override
	public void update() {
		if(options != null) {
			options.update();
			if(options.isCloseRequest())
				options = null;
		} else {
			buttons.update();
			switch(buttons.getClicks()) {
			case 1:
				options = new OptionsMenu(20, 20, "Menu2");
				break;
			case 2:
				Theater.get().swapSetup(new TitleMenu());
				break;
			case 3:
				Theater.get().close();
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void draw() {
		DrawUtils.fillColor(0f, 0f, 0f, 0.65f);
		
		if(options != null) {
			options.draw();
		} else {
			buttons.draw();
		}
	}

	@Override
	public boolean isCloseRequest() {
		return (buttons.getButton(0).isClicked() || Keybinds.EXIT.clicked()) && options == null;
	}

}
