package core.utilities.mouse;

import java.awt.geom.Point2D;

import org.lwjgl.input.Mouse;

import core.Camera;
import core.Input;

public class MouseInput {

	/**
	 * @return Mouse X scaled to any screen resizing
	 */
	public static float getMouseX() {
		return (float) (Input.mouseCurrent.getX() / Camera.get().getFrameXScale());
	}
	
	public static int getScreenMouseX() {
		//float scale = (float) ((Camera.get().frame.getWidth() * Camera.get().getScale()) / Camera.get().frame.getWidth());
		//return (int) ((Input.mouseCurrent.getX() * scale) + Camera.get().frame.getX());
		// TODO We're gettin' somewhere!
		return (int) ((Camera.get().frame.getWidth() / Camera.get().getScale()) / (Camera.get().frame.getWidth() / Mouse.getX()));
	}
	
	/**
	 * @return Mouse Y scaled to any screen resizing
	 */
	public static float getMouseY() {
		// Invert Mouse Y because 0 is the bottom of the window
		return (float) -(Input.mouseCurrent.getY() - Camera.get().frame.getHeight()) / Camera.get().getFrameYScale();
	}
	
	public static int getScreenMouseY() {
		return (int) (-((Input.mouseCurrent.getY() / Camera.get().getScale()) - Camera.get().frame.getHeight()) + Camera.get().frame.getY());
		/*return (int) (-((Input.mouseCurrent.getY() / Camera.get().getScale()) - (Camera.get().frame.getHeight() / Camera.get().getScale()))
				+ (Camera.get().frame.getY() + (Camera.get().frame.getHeight() * (Camera.get().getScale() - 1))));*/
	}

	/**
	 * @return Mouse as a Point2D
	 */
	public static Point2D getMouse() {
		return new Point2D.Double(getMouseX(), getMouseY());
	}
	
}
