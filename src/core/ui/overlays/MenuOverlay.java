package core.ui.overlays;

import core.ui.UIElement;

public abstract class MenuOverlay extends UIElement {

	public MenuOverlay(float x, float y, String image) {
		super(x, y, image);
		this.setOpacity(1f);
	}
	
	public abstract boolean isCloseRequest();

}
