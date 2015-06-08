package core.ui.utils;

import core.ui.UIElement;

public abstract class ClickEvent extends UIEvent implements UIAction {
	
	public ClickEvent(UIElement parent) {
		super(parent);
	}

	public abstract void click();

	@Override
	public void actionPerformed() {
		if(parent.isClicked()) {
			click();
		}
	}
	
}
