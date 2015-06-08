package core.ui.overlays;

import core.Camera;
import core.Theater;
import core.render.DrawUtils;
import core.setups.TitleMenu;
import core.ui.Button;
import core.ui.utils.Align;
import core.ui.utils.ClickEvent;

public class GameMenu extends MenuOverlay {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OptionsMenu options;
	
	public GameMenu(String image) {
		Button toGame = new Button("Return to Game", Float.NaN, Camera.get().getDisplayHeight(0.2f), 0, null);
		toGame.setAlign(Align.CENTER);
		toGame.setStill(true);
		toGame.addEvent(new ClickEvent(toGame) {
			public void click() {
				toClose = true;
			}
		});
		this.add(toGame);
		
		Button openOptions = new Button("Options", Float.NaN, (float) toGame.getBounds().getMaxY(), 0, null);
		openOptions.setAlign(Align.CENTER);
		openOptions.setStill(true);
		openOptions.addEvent(new ClickEvent(openOptions) {
			public void click() {
				options = new OptionsMenu("Menu2");
			}
		});
		this.add(openOptions);
		
		Button toTitle = new Button("Quit to Title", Float.NaN, (float) openOptions.getBounds().getMaxY(), 0, null);
		toTitle.setAlign(Align.CENTER);
		toTitle.setStill(true);
		toTitle.addEvent(new ClickEvent(toTitle) {
			public void click() {
				Theater.get().swapSetup(new TitleMenu());
			}
		});
		this.add(toTitle);
		
		Button toDesktop = new Button("Quit to Desktop", Float.NaN, (float) toTitle.getBounds().getMaxY(), 0, null);
		toDesktop.setAlign(Align.CENTER);
		toDesktop.setStill(true);
		toDesktop.addEvent(new ClickEvent(toDesktop) {
			public void click() {
				Theater.get().close();
			}
		});
		this.add(toDesktop);
		
		addFrame(image);
	}
	
	@Override
	public void update() {
		if(options != null) {
			options.update();
			if(options.isCloseRequest())
				options = null;
		} else {
			super.update();
		}
	}
	
	@Override
	public void draw() {
		DrawUtils.fillColor(0f, 0f, 0f, 0.65f);
		
		if(options != null) {
			options.draw();
		} else {
			super.draw();
		}
	}

	@Override
	public boolean isCloseRequest() {
		return super.isCloseRequest() && options == null;
	}

}
