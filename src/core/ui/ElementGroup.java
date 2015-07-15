package core.ui;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import core.Camera;
import core.Theater;
import core.render.SpriteIndex;
import core.ui.utils.Accessible;
import core.utilities.MathFunctions;
import core.utilities.keyboard.Keybinds;
import core.utilities.mouse.MouseInput;

public class ElementGroup<T extends UIElement> extends ArrayList<T> {
	
	private static final long serialVersionUID = 1L;
	
	private boolean singleSelection = true;
	private EmptyFrame frame;
	protected int selection = -1;
	private SelectionPointer pointer;

	public void update() {
		for(UIElement e : this) {
			e.update();
		}
		
		if(pointer != null) {
			pointer.update();
		}
		
		if(selection != -1 && (frame != null ? !frame.getBounds().contains(MouseInput.getScreenMouse()) : true)) {
			get(selection).setSelected(true);
			if(Keybinds.MENU_UP.clicked() && get(selection).getSurroundings()[0] != null) {
				changeSelection(0);
			} else if(Keybinds.MENU_RIGHT.clicked() && get(selection).getSurroundings()[1] != null) {
				changeSelection(1);
			} else if(Keybinds.MENU_LEFT.clicked() && get(selection).getSurroundings()[2] != null) {
				changeSelection(2);
			} else if(Keybinds.MENU_DOWN.clicked() && get(selection).getSurroundings()[3] != null) {
				changeSelection(3);
			}
		} else if(selection != -1 && frame.getBounds().contains(MouseInput.getScreenMouse())) {
			get(selection).setSelected(false);
			// TODO Get mouse coordinates and update pointer position
		}
	}
	
	public void draw() {
		if(frame != null) {
			frame.draw();
		}
		
		for(UIElement e : this) {
			e.draw();
		}
		
		if(pointer != null) {
			pointer.draw();
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
	
	public void setSelectionPointer(String pointerName) {
		this.pointer = new SelectionPointer(pointerName, (!isEmpty() ? get(0).still : false));
		if(selection != -1) {
			pointer.setPosition(get(selection));
		}
	}
	
	public void setKeyboardNavigable(boolean enabled, UIElement startIndex) {
		selection = (enabled ? indexOf(startIndex) : -1);
		if(get(selection) != null) {
			get(selection).setSelected(true);
		}
	}
	
	private void changeSelection(int direction) {
		get(selection).setSelected(false);
		selection = this.indexOf(get(selection).getSurroundings()[direction]);
		get(selection).setSelected(true);
		
		if(pointer != null) {
			pointer.setPosition(get(selection));
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

class SelectionPointer {
	
	private String texture;
	private Vector2f position = new Vector2f(0, 0);
	private Vector2f offset = new Vector2f(0, 0);
	private Dimension2D bounds = new Dimension();
	private boolean still;
	
	/** Tween variables */
	private float time;
	private float duration;
	private float distance;
	private boolean tweenOut;
	
	public SelectionPointer(String texture, boolean still) {
		this.texture = texture;
		this.still = still;
		this.bounds.setSize(SpriteIndex.getSprite(texture).getWidth() * Camera.ASPECT_RATIO,
				SpriteIndex.getSprite(texture).getHeight() * Camera.ASPECT_RATIO);
		
		this.time = 0f;
		this.duration = 1.5f;
		this.distance = 15f;
		this.tweenOut = true;
	}
	
	public void update() {
		time = MathFunctions.clamp(time + Theater.getDeltaSpeed(0.025f), 0, duration);
		if(tweenOut) {
			offset.setX(MathFunctions.easeOut(time, 0, -distance, duration));
		} else {
			offset.setX(MathFunctions.easeOut(time, -distance, distance, duration));
		}
		if(time == duration) {
			time = 0;
			tweenOut = !tweenOut;
		}
	}
	
	public void draw() {
		SpriteIndex.getSprite(texture).setStill(still);
		SpriteIndex.getSprite(texture).set2DScale(Camera.ASPECT_RATIO);
		SpriteIndex.getSprite(texture).draw(position.x + offset.x, position.y + offset.y);
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
	}
	
	public void setPosition(UIElement element) {
		position.setX((float) (element.getBounds().getX() - (bounds.getWidth() * 0.8f)));
		position.setY((float) (element.getBounds().getCenterY() - (bounds.getHeight() * 0.5f)));
	}
	
}
