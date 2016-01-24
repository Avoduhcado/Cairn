package core.setups;

import java.util.ArrayList;

import core.ui.UIElement;
import core.ui.event.StateChangeEvent;
import core.ui.event.StateChangeListener;
import core.ui.event.UIEvent;
import core.ui.utils.UIContainer;

public abstract class GameSetup implements UIContainer {

	private ArrayList<UIElement> uiElements = new ArrayList<UIElement>();
	
	/** Handler for all contained UI state changes */
	private final StateChangeListener stateChangeListener = new StateChangeListener() {
		public void changeState(StateChangeEvent e) {
			switch(e.getState()) {
			case UIElement.ENABLED:
				break;
			case UIElement.DISABLED:
				break;
			case UIElement.KILL_FLAG:
				removeElement(e.getElement());
				break;
			}
		}
	};
	
	/** Update the current game state */
	public abstract void update();
	/** Draw the current game state */
	public abstract void draw();
	
	/** Draw the setup's UI */
	public void drawUI() {
		for(UIElement ui : uiElements) {
			ui.draw();
		}
	}
	
	public ArrayList<UIElement> getUI() {
		return uiElements;
	}
	
	public UIElement getElement(int index) {
		return uiElements.get(index);
	}
	
	public boolean removeElement(UIElement element) {
		return uiElements.remove(element);
	}
	
	public void addUI(UIElement element) {
		element.setContainer(this);
		uiElements.add(element);
	}
	
	public void addUI(UIElement element, int index) {
		element.setContainer(this);
		uiElements.add(index, element);
	}
	
	public void fireEvent(UIEvent e) {
		if(e instanceof StateChangeEvent) {
			processStateChangeEvent(e);
		}
	}
	
	protected void processStateChangeEvent(UIEvent e) {
		stateChangeListener.changeState((StateChangeEvent) e);
	}
	
}
