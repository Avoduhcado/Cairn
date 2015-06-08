package core.ui;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.ui.utils.Accessible;

public class ElementGroup extends ArrayList<UIElement> {
	
	private static final long serialVersionUID = 1L;
	
	private boolean singleSelection = true;

	public void update() {
		for(UIElement e : this) {
			e.update();
		}
	}
	
	public void draw() {
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
		EmptyFrame frame = new EmptyFrame(getBounds(), image);
		frame.setStill(true);
		this.add(0, frame);
	}
	
	public void addFrame(String image, float xBorder, float yBorder) {
		EmptyFrame frame = new EmptyFrame(getBounds(), image);
		frame.setStill(true);
		frame.setXBorder(xBorder);
		frame.setYBorder(yBorder);
		this.add(0, frame);
	}

}
