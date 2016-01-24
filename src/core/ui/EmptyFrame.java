package core.ui;

import java.awt.geom.Rectangle2D;

public class EmptyFrame extends UIElement {

	public EmptyFrame(float x, float y, String frame, float width, float height) {
		setBounds(x, y, width, height);
		setFrame(frame);
	}
	
	public EmptyFrame(Rectangle2D bounds, String frame) {
		setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		setFrame(frame);
	}
	
}
