package core.ui.overlays;

import core.Camera;
import core.Theater;
import core.setups.TitleMenu;
import core.ui.Button;
import core.ui.ElementGroup;
import core.ui.utils.Align;

public class GameMenu extends MenuOverlay {
	
	public GameMenu() {
		super();
		
		Button toGame = new Button(Float.NaN, Camera.get().getDisplayHeight(0.2f), null, "Return to Game");
		toGame.setAlign(Align.CENTER);
		toGame.addActionListener(e -> setState(KILL_FLAG));
		
		Button openOptions = new Button(Float.NaN, (float) toGame.getBounds().getMaxY(), null, "Options");
		openOptions.setAlign(Align.CENTER);
		openOptions.addActionListener(e -> addUI(new OptionsMenu()));
		
		Button toTitle = new Button(Float.NaN, (float) openOptions.getBounds().getMaxY(), null, "Quit to Title");
		toTitle.setAlign(Align.CENTER);
		toTitle.addActionListener(e -> Theater.get().swapSetup(new TitleMenu()));
		
		Button toDesktop = new Button(Float.NaN, (float) toTitle.getBounds().getMaxY(), null, "Quit to Desktop");
		toDesktop.setAlign(Align.CENTER);
		toDesktop.addActionListener(e -> Theater.get().close());
		
		toGame.setSurrounding(0, toDesktop);
		openOptions.setSurrounding(0, toGame);
		toTitle.setSurrounding(0, openOptions);
		toDesktop.setSurrounding(0, toTitle);
	
		ElementGroup<Button> buttons = new ElementGroup<Button>();
		buttons.add(toGame);
		buttons.add(openOptions);
		buttons.add(toTitle);
		buttons.add(toDesktop);
		buttons.setKeyboardNavigable(true, toGame);
		addUI(buttons);
		
		setBounds();
		setFrame("Menu2");
		setStill(true);
	}

}
