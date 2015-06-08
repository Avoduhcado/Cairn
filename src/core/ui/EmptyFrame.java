package core.ui;

import java.awt.geom.Rectangle2D;

public class EmptyFrame extends UIElement {

	public EmptyFrame(float x, float y, float width, float height, String image) {
		setBounds(x, y, width, height);
		setFrame(image);
	}
	
	public EmptyFrame(Rectangle2D bounds, String image) {
		setBounds((float) bounds.getX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
		setFrame(image);
	}
	
}
