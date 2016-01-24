package core.ui;

import java.awt.geom.Rectangle2D;
import core.Camera;
import core.render.textured.UIFrame;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.MouseMotionListener;
import core.ui.event.StateChangeEvent;
import core.ui.event.TimeEvent;
import core.ui.event.TimeListener;
import core.ui.event.UIEvent;
import core.ui.utils.Align;
import core.ui.utils.UIContainer;

public abstract class UIElement {

	public static final int KILL_FLAG = -1;
	public static final int DISABLED = 0;
	public static final int ENABLED = 1;
	
	protected Rectangle2D bounds = new Rectangle2D.Double(0, 0, 1, 1);
	
	protected UIFrame frame;
	protected float xBorder;
	protected float yBorder;
	protected Align alignment = Align.RIGHT;
	
	protected boolean selected;
	protected boolean still;
	
	private UIContainer container;
	private int state = ENABLED;
	
	/** For managing keyboard mapped menus. 0 = up, 1 = down, 2 = right, 3 = left */
	protected UIElement[] surroundings = new UIElement[4];
		
	protected MouseListener mouseListener;
	protected MouseMotionListener mouseMotionListener;
	protected TimeListener timeListener;
	
	public void draw() {
		if(frame != null) {
			frame.draw(bounds);
		}
	}

	public void setStill(boolean still) {
		this.still = still;
	}

	public float getX() {
		if(bounds == null)
			return 0;
		
		return (float) bounds.getX();
	}
	
	public float getY() {
		if(bounds == null)
			return 0;
		
		return (float) bounds.getY();
	}
	
	public Rectangle2D getBounds() {
		return bounds;
	}

	// TODO Introduce border offsets when drawing elements
	public void setBounds(float x, float y, float width, float height) {
		bounds = new Rectangle2D.Double((Float.isNaN(x) ? Camera.get().getDisplayWidth(0.5f) : x) - xBorder,
				(Float.isNaN(y) ? Camera.get().getDisplayHeight(0.5f) : y) - yBorder, width + (xBorder * 2), height + (yBorder * 2));
	}
	
	public void setBounds(double x, double y, double width, double height) {
		bounds = new Rectangle2D.Double((Double.isNaN(x) ? Camera.get().getDisplayWidth(0.5f) : x) - xBorder,
				(Double.isNaN(y) ? Camera.get().getDisplayHeight(0.5f) : y) - yBorder, width + (xBorder * 2), height + (yBorder * 2));
	}
	
	public void setPosition(float x, float y) {
		bounds = new Rectangle2D.Double((Float.isNaN(x) ? Camera.get().getDisplayWidth(0.5f) : x) - xBorder,
				(Float.isNaN(y) ? Camera.get().getDisplayHeight(0.5f) : y) - yBorder, bounds.getWidth(), bounds.getHeight());
	}
	
	public void setPosition(double x, double y) {
		bounds = new Rectangle2D.Double((Double.isNaN(x) ? Camera.get().getDisplayWidth(0.5f) : x) - xBorder,
				(Double.isNaN(y) ? Camera.get().getDisplayHeight(0.5f) : y) - yBorder, bounds.getWidth(), bounds.getHeight());
	}
	
	public float getXBorder() {
		return xBorder;
	}
	
	public void setXBorder(float xBorder) {
		this.xBorder = xBorder;
		setBounds((float) bounds.getX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
	}
	
	public float getYBorder() {
		return yBorder;
	}
	
	public void setYBorder(float yBorder) {
		this.yBorder = yBorder;
		setBounds((float) bounds.getX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
	}
	
	public void setAlign(Align alignment) {
		if(this.alignment != alignment) {
			this.alignment = alignment;
			
			switch(alignment) {
			case RIGHT:
				setBounds((float) bounds.getMaxX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
				break;
			case LEFT:
				setBounds((float) (bounds.getX() - bounds.getWidth()), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
				break;
			case CENTER:
				// TODO Borders
				bounds.setFrameFromCenter(bounds.getX(), bounds.getY(), 
						bounds.getX() - (bounds.getWidth() / 2f), bounds.getY() - (bounds.getHeight() / 2f));
				break;
			default:
				break;
			}
		}
	}
	
	public void setFrame(String image) {
		if(image != null) {
			this.frame = new UIFrame(image);
		}
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 *  0 = up, 1 = right, 2 = left, 3 = down 
	 * @return surroundings
	 */
	public UIElement[] getSurroundings() {
		return surroundings;
	}
	
	/**
	 * @param index 0 = up, 1 = right, 2 = left, 3 = down
	 * @param surround Element at <code>index</code>
	 */
	public void setSurrounding(int index, UIElement surround) {
		this.surroundings[index] = surround;
		if(surround.getSurroundings()[Math.abs(index - 3)] == null) {
			surround.setSurrounding(Math.abs(index - 3), this);
		}
	}

	public UIContainer getContainer() {
		return container;
	}
	
	public void setContainer(UIContainer container) {
		this.container = container;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
		
		if(container != null) {
			container.fireEvent(new StateChangeEvent(this, this.state));
		}
	}

	public void removeMouseListener(MouseListener l) {
		if(l == null) {
			return;
		}
		this.mouseListener = null;
	}
	
	public void addMouseListener(MouseListener l) {
		this.mouseListener = l;
	}
	
	public void removeMouseMotionListener(MouseMotionListener l) {
		if(l == null) {
			return;
		}
		this.mouseMotionListener = null;
	}
	
	public void addMouseMotionListener(MouseMotionListener l) {
		this.mouseMotionListener = l;
	}
	
	public void removeTimeListener(TimeListener l) {
		if(l == null) {
			return;
		}
		this.timeListener = null;
	}
	
	public void addTimeListener(TimeListener l) {
		this.timeListener = l;
	}
	
	public void fireEvent(UIEvent e) {
		if(e instanceof MouseEvent) {
			processMouseEvent((MouseEvent) e);
		} else if(e instanceof TimeEvent) {
			processTimeEvent((TimeEvent) e);
		}
	}
	
	protected void processMouseEvent(MouseEvent e) {
		if(mouseListener != null) {
			if(e.getEvent() == MouseEvent.MOVED) {
				if(getBounds().contains(e.getPosition()) && !getBounds().contains(e.getPrevPosition())) {
					mouseListener.mouseEntered(e);
					return;
				} else if(!getBounds().contains(e.getPosition()) && getBounds().contains(e.getPrevPosition())) {
					mouseListener.mouseExited(e);
					return;
				}
			}
			
			switch(e.getEvent()) {
			case MouseEvent.CLICKED:
				mouseListener.mouseClicked(e);
				break;
			case MouseEvent.PRESSED:
				mouseListener.mousePressed(e);
				break;
			case MouseEvent.RELEASED:
				mouseListener.mouseReleased(e);
				break;
			}
		}
		
		if(mouseMotionListener != null) {
			switch(e.getEvent()) {
			case MouseEvent.MOVED:
				mouseMotionListener.mouseMoved(e);
				break;
			case MouseEvent.DRAGGED:
				mouseMotionListener.mouseDragged(e);
				break;
			}
		}
	}
	
	protected void processTimeEvent(TimeEvent e) {
		if(timeListener != null) {
			timeListener.timeStep(e);
		}
	}

}
