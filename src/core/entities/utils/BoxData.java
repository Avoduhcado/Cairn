package core.entities.utils;

import java.awt.geom.Rectangle2D;

public class BoxData {
	
	private float xOffset;
	private float yOffset;
	private float width;
	private float height;

	public BoxData(float xOffset, float yOffset, float width, float height) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;
	}
	
	public Rectangle2D getRectangle(Rectangle2D box) {
		return new Rectangle2D.Double(box.getX() + (box.getWidth() * xOffset), box.getY() + (box.getHeight() * yOffset),
				box.getWidth() * width, box.getHeight() * height);
	}
}