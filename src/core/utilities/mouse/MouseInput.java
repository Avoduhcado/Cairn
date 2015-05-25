package core.utilities.mouse;

import java.awt.geom.Point2D;

import core.Camera;
import core.Input;

public class MouseInput {

	/**
	 * @return Mouse X scaled to any screen resizing
	 */
	public static float getMouseX() {
		return (float) (Input.mouseCurrent.getX() / Camera.get().getWindowXScale());
	}
	
	public static int getScreenMouseX() {
		// Calculate Screen Space Offset = (Scaled Width - Width) / 2
		float sso = (float) (((Camera.get().frame.getWidth() / Camera.get().getScale()) - Camera.get().frame.getWidth()) / 2f);
		return (int) (((Camera.get().frame.getWidth() / Camera.get().getScale()) / (Camera.get().frame.getWidth() / getMouseX()))
				- (sso - Camera.get().frame.getX()));
	}
	
	/**
	 * @return Mouse Y scaled to any screen resizing
	 */
	public static float getMouseY() {
		// Invert Mouse Y because 0 is the bottom of the window
		return (float) -(Input.mouseCurrent.getY() - Camera.get().displayHeight) / Camera.get().getWindowYScale();
	}
	
	public static int getScreenMouseY() {
		float sso = (float) (((Camera.get().frame.getHeight() / Camera.get().getScale()) - Camera.get().frame.getHeight()) / 2f);
		return (int) (((Camera.get().frame.getHeight() / Camera.get().getScale()) / (Camera.get().frame.getHeight() / getMouseY())
				- (sso - Camera.get().frame.getY())));
	}

	/**
	 * @return Mouse as a Point2D
	 */
	public static Point2D getMouse() {
		return new Point2D.Double(getMouseX(), getMouseY());
	}
	
}
