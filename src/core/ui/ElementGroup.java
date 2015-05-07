package core.ui;

import java.util.ArrayList;

public class ElementGroup extends ArrayList<UIElement> {
	
	private static final long serialVersionUID = 1L;

	private boolean singleSelection = true;

	public void setEnabledAll(boolean enabled) {
		for(UIElement e : this) {
			e.setEnabled(enabled);
		}
	}
	
	public void setEnabledAllExcept(boolean enabled, UIElement except) {
		for(UIElement e : this) {
			if(e != except)
				e.setEnabled(enabled);
		}
	}
	
	public boolean isSingleSelection() {
		return singleSelection;
	}

	public void setSingleSelection(boolean singleSelection) {
		this.singleSelection = singleSelection;
	}
	
}
