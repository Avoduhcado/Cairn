package core.utilities.mouse;

import java.awt.geom.Point2D;

import core.Camera;
import core.Input;

public class MouseInput {

	/**
	 * @return Mouse X scaled to any screen resizing
	 */
	public static float getMouseX() {
		return (float) (Input.mouseCurrent.getX() / Camera.get().getFrameXScale());
	}
	
	/**
	 * @return Mouse Y scaled to any screen resizing
	 */
	public static float getMouseY() {
		// Invert Mouse Y for some odd reason
		return (float) -(Input.mouseCurrent.getY() - Camera.get().frame.getHeight()) / Camera.get().getFrameYScale();
	}

	/**
	 * @return Mouse as a Point2D
	 */
	public static Point2D getMouse() {
		return new Point2D.Double(getMouseX(), getMouseY());
	}
	
}
