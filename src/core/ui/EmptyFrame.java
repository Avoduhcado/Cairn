package core.ui;

import java.awt.geom.Rectangle2D;

public class EmptyFrame extends UIElement {

	public EmptyFrame(float x, float y, String image) {
		super(x, y, image);
	}

	public void setBox(float x, float y, float width, float height, boolean withBorders) {
		if(withBorders) {
			this.x = x - (frame.getWidth() / 3f);
			this.y = y - (frame.getHeight() / 3f);
			this.box = new Rectangle2D.Double(x, y,	width + ((frame.getWidth() / 3f) * 2f), height + ((frame.getHeight() / 3f) * 2f));
		} else {
			this.x = x;
			this.y = y;
			this.box = new Rectangle2D.Double(x, y,	width, height);
		}
	}
	
}
