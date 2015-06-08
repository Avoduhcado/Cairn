package core.ui.utils;

import core.ui.UIElement;

public abstract class ValueChangeEvent extends UIEvent implements UIAction {
	
	public ValueChangeEvent(UIElement parent) {
		super(parent);
	}
	
	public abstract void changeValue();

	@Override
	public void actionPerformed() {
		if(parent.isValueChanged()) {
			changeValue();
		}
	}

}
