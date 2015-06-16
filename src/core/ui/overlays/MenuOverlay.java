package core.ui.overlays;

import core.ui.ElementGroup;
import core.ui.UIElement;
import core.utilities.keyboard.Keybinds;

public abstract class MenuOverlay extends ElementGroup<UIElement> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected boolean toClose;
	
	public boolean isCloseRequest() {
		return toClose || Keybinds.EXIT.clicked();
	}

}
