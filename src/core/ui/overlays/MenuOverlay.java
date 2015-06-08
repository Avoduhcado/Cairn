package core.ui.overlays;

import core.ui.ElementGroup;
import core.utilities.keyboard.Keybinds;

public abstract class MenuOverlay extends ElementGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected boolean toClose;
	
	public boolean isCloseRequest() {
		return toClose || Keybinds.EXIT.clicked();
	}

}
