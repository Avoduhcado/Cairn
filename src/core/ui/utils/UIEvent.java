package core.ui.utils;

import core.ui.UIElement;

public abstract class UIEvent {

	protected UIElement parent;
	
	public UIEvent(UIElement parent) {
		this.parent = parent;
	}
	
}
