package core.setups;

import core.Camera;
import core.Theater;
import core.audio.Ensemble;
import core.audio.Track;
import core.render.textured.Sprite;
import core.ui.Button;
import core.ui.ButtonGroup;
import core.ui.overlays.OptionsMenu;
import core.utilities.keyboard.Keybinds;

public class TitleMenu extends GameSetup {

	/** Title logo */
	private Sprite logo;
	/** A button group contain New Game, Options, and Exit */
	private ButtonGroup buttonGroup;
	/** The options menu */
	private OptionsMenu optionsMenu;
	
	/**
	 * Title Menu
	 * Set up buttons for game operation.
	 */
	public TitleMenu() {
		// Ensure fading has reset
		Camera.get().setFadeTimer(-0.5f);
		Camera.get().frame.setFrame(0, 0, Camera.get().frame.getWidth(), Camera.get().frame.getHeight());
		
		// Load title logo
		//logo = new Sprite("Avogine Title");
		
		// Initialize game buttons
		buttonGroup = new ButtonGroup(Float.NaN, Camera.get().getDisplayHeight(0.575f), "Menu2", true);
		buttonGroup.addButton(new Button("New Game"));
		buttonGroup.addButton(new Button("Options"));
		buttonGroup.addButton(new Button("Exit"));
		buttonGroup.setCentered(true);
		
		// Play title track
		//Ensemble.get().swapBackground(new Track("TitleTheme2"), 0.75f, 1.75f);
	}
	
	@Override
	public void update() {
		// Update options instead of main screen if it's open
		if(optionsMenu != null) {
			optionsMenu.update();
			// Close options if user chooses to close
			if(optionsMenu.isCloseRequest())
				optionsMenu = null;
		} else {
			// Update buttons
			buttonGroup.update();
			switch(buttonGroup.getClicks()) {
			case 0:
				// Start game, proceed with state swap
				Theater.get().swapSetup(new Stage());
				break;
			case 1:
				// Open options menu
				optionsMenu = new OptionsMenu(20, 20, "Menu2");
				break;
			case 2:
				// Exit game
				Theater.get().close();
				break;
			}
		}
	}
	
	@Override
	public void draw() {
		
	}
	
	@Override
	public void drawUI() {
		// Draw logo
		//logo.draw(Float.NaN, Camera.get().getDisplayHeight(0.1667f));

		// Draw buttons
		buttonGroup.draw();

		// If options menu is open, draw it
		if(optionsMenu != null)
			optionsMenu.draw();
	}

	@Override
	public void resizeRefresh() {
		// Reposition and center
		buttonGroup.setPosition(Float.NaN, Camera.get().getDisplayHeight(0.667f));
	}

}
