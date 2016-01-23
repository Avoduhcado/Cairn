package core.entities_new.utils;

public class BodyData {

	private float x, y, width = 15, height = 15;
	private int bodyType;

	public BodyData() {
		
	}
	
	public BodyData(float x, float y, int bodyType) {
		setX(x);
		setY(y);
		setBodyType(bodyType);
	}
	
	public BodyData(float x, float y, float width, float height, int bodyType) {
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		setBodyType(bodyType);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public int getBodyType() {
		return bodyType;
	}

	public void setBodyType(int bodyType) {
		this.bodyType = bodyType;
	}
	
}
