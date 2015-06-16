package core.ui;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.ui.utils.Accessible;
import core.utilities.keyboard.Keybinds;
import core.utilities.mouse.MouseInput;

public class ElementGroup<T extends UIElement> extends ArrayList<T> {
	
	private static final long serialVersionUID = 1L;
	
	private boolean singleSelection = true;
	private EmptyFrame frame;
	private int selection = -1;

	public void update() {
		for(UIElement e : this) {
			e.update();
		}
		
		if(selection != -1 && !frame.getBounds().contains(MouseInput.getScreenMouse())) {
			get(selection).setSelected(true);
			if(Keybinds.UP.clicked() && get(selection).getSurroundings()[0] != null) {
				get(selection).setSelected(false);
				selection = this.indexOf(get(selection).getSurroundings()[0]);
				get(selection).setSelected(true);
			} else if(Keybinds.RIGHT.clicked() && get(selection).getSurroundings()[1] != null) {
				get(selection).setSelected(false);
				selection = this.indexOf(get(selection).getSurroundings()[1]);
				get(selection).setSelected(true);
			} else if(Keybinds.LEFT.clicked() && get(selection).getSurroundings()[2] != null) {
				get(selection).setSelected(false);
				selection = this.indexOf(get(selection).getSurroundings()[2]);
				get(selection).setSelected(true);
			} else if(Keybinds.DOWN.clicked() && get(selection).getSurroundings()[3] != null) {
				get(selection).setSelected(false);
				selection = this.indexOf(get(selection).getSurroundings()[3]);
				get(selection).setSelected(true);
			}
		} else if(selection != -1 && frame.getBounds().contains(MouseInput.getScreenMouse())) {
			get(selection).setSelected(false);
		}
	}
	
	public void draw() {
		frame.draw();
		for(UIElement e : this) {
			e.draw();
		}
	}
	
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
	
	public void setEnabledAllExcept(boolean enabled, int index) {
		for(int i = 0; i<size(); i++) {
			if(i != index) {
				get(i).setEnabled(enabled);
			}
		}
	}
	
	public void setFocus(Accessible focus) {
		for(UIElement e : this) {
			if(e instanceof Accessible && e != focus) {
				e.setEnabled(false);
			}
		}
	}
	
	public boolean isSingleSelection() {
		return singleSelection;
	}

	public void setSingleSelection(boolean singleSelection) {
		this.singleSelection = singleSelection;
	}
	
	public void setKeyboardNavigable(boolean enabled) {
		selection = (enabled ? 0 : -1);
		if(get(selection) != null) {
			get(selection).setSelected(true);
		}
	}
	
	private Rectangle2D getBounds() {
		if(this.isEmpty()) {
			return null;
		} else {
			Rectangle2D tempBounds = (Rectangle2D) get(0).getBounds().clone();
			for(UIElement e : this) {
				Rectangle2D.union(tempBounds, e.getBounds(), tempBounds);
			}
			
			return tempBounds;
		}
	}
	
	public void addFrame(String image) {
		frame = new EmptyFrame(getBounds(), image);
		frame.setStill(true);
	}
	
	public void addFrame(String image, float xBorder, float yBorder) {
		frame = new EmptyFrame(getBounds(), image);
		frame.setStill(true);
		frame.setXBorder(xBorder);
		frame.setYBorder(yBorder);
	}

}
