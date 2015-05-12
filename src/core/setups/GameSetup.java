package core.setups;

import java.util.ArrayList;

import core.ui.UIElement;

public abstract class GameSetup {

	protected ArrayList<UIElement> uiElements = new ArrayList<UIElement>();
	
	/** Update the current game state */
	public abstract void update();
	/** Draw the current game state */
	public abstract void draw();
	/** Draw UI Elements of current game state */
	public abstract void drawUI();
	/** Readjust screen objects to match screen resize */
	public abstract void resizeRefresh();
	
	public ArrayList<UIElement> getUI() {
		return uiElements;
	}
	
	public UIElement getElement(int index) {
		return uiElements.get(index);
	}
	
	public void addUI(UIElement element) {
		uiElements.add(element);
	}
	
}
