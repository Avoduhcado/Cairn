package core.ui.overlays;

import java.util.ArrayList;

import core.render.DrawUtils;
import core.ui.ElementGroup;
import core.ui.UIElement;
import core.ui.event.StateChangeEvent;
import core.ui.event.StateChangeListener;
import core.ui.event.UIEvent;
import core.ui.utils.UIContainer;

public abstract class MenuOverlay extends ElementGroup<UIElement> implements UIContainer {

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
	
	public MenuOverlay() {
		super();
	}
	
	@Override
	public void draw() {
		DrawUtils.fillColor(0f, 0f, 0f, 0.35f);
		
		super.draw();
	}
	
	@Override
	public void drawUI() {
		for(UIElement ui : uiElements) {
			ui.draw();
		}
	}

	@Override
	public ArrayList<UIElement> getUI() {
		return uiElements;
	}

	@Override
	public UIElement getElement(int index) {
		return uiElements.get(index);
	}

	@Override
	public boolean removeElement(UIElement element) {
		return uiElements.remove(element);
	}

	@Override
	public void addUI(UIElement element) {
		element.setContainer(this);
		uiElements.add(element);
	}

	@Override
	public void addUI(UIElement element, int index) {
		element.setContainer(this);
		uiElements.add(index, element);
	}
	
	@Override
	public void addAllUI(UIElement...elements) {
		for(UIElement e : elements) {
			addUI(e);
		}
	}
	
	@Override
	public void fireEvent(UIEvent e) {
		super.fireEvent(e);
		
		if(e instanceof StateChangeEvent) {
			processStateChangeEvent(e);
		}
	}
	
	protected void processStateChangeEvent(UIEvent e) {
		stateChangeListener.changeState((StateChangeEvent) e);
	}

}
