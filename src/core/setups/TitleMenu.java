package core.setups;

import core.Camera;
import core.Theater;
import core.ui.Button;
import core.ui.ElementGroup;
import core.ui.Icon;
import core.ui.overlays.OptionsMenu;
import core.ui.utils.Align;
import core.ui.utils.ClickEvent;

public class TitleMenu extends GameSetup {

	/** Title logo */
	private Icon logo;
	/** A button group contain New Game, Options, and Exit */
	private ElementGroup<Button> buttons;
	/** The options menu */
	private OptionsMenu optionsMenu;
	
	/**
	 * Title Menu
	 * Set up buttons for game operation.
	 */
	public TitleMenu() {
		// Ensure fading has reset
		Camera.get().setFade(-0.5f);
		Camera.get().frame.setFrame(0, 0, Camera.get().frame.getWidth(), Camera.get().frame.getHeight());
		
		// Load title logo
		logo = new Icon("Avogine Title");
		logo.setPosition(Float.NaN, Camera.get().getDisplayHeight(0.1667f));
		
		Button newGame = new Button("New Game", Float.NaN, Camera.get().getDisplayHeight(0.55f), 0, null);
		newGame.setStill(true);
		newGame.setAlign(Align.CENTER);
		newGame.addEvent(new ClickEvent(newGame) {
			public void click() {
				Theater.get().swapSetup(new Stage_new());
				//Theater.get().swapSetup(new Stage());
			}
		});
		
		Button options = new Button("Options", Float.NaN, (float) newGame.getBounds().getMaxY(), 0, null);
		options.setStill(true);
		options.setAlign(Align.CENTER);
		options.addEvent(new ClickEvent(options) {
			public void click() {
				optionsMenu = new OptionsMenu("Menu2");
			}
		});
		
		Button exit = new Button("Exit", Float.NaN, (float) options.getBounds().getMaxY(), 0, null);
		exit.setStill(true);
		exit.setAlign(Align.CENTER);
		exit.addEvent(new ClickEvent(exit) {
			public void click() {
				Theater.get().close();
			}
		});
		
		// Initialize game buttons
		newGame.setSurrounding(3, options);
		options.setSurrounding(3, exit);
		exit.setSurrounding(3, newGame);
		
		buttons = new ElementGroup<Button>();
		buttons.add(newGame);
		buttons.add(options);
		buttons.add(exit);
		buttons.setKeyboardNavigable(true, newGame);
		//buttons.setSelectionPointer("screen ui/Pointer");
		buttons.addFrame("Menu2");
		
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
			buttons.update();
		}
	}
	
	@Override
	public void draw() {
		
	}
	
	@Override
	public void drawUI() {
		// Draw logo
		logo.draw();

		// If options menu is open, draw it
		if(optionsMenu != null) {
			optionsMenu.draw();
		} else {
			// Draw buttons
			buttons.draw();
		}
	}

	@Override
	public void resizeRefresh() {
	}

}
