package core.ui.utils;

import java.util.ArrayList;

import core.ui.UIElement;
import core.ui.event.UIEvent;

public interface UIContainer {

	public void drawUI();
	
	public ArrayList<UIElement> getUI();
	public UIElement getElement(int index);
	public boolean removeElement(UIElement element);
	public void addUI(UIElement element);
	public void addUI(UIElement element, int index);
	
	public void fireEvent(UIEvent e);
	
}
