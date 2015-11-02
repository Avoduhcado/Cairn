package core.render;

import org.lwjgl.util.vector.Vector4f;

public class Transform {

	public float x, y;
	public float rotation;
	public float scaleX, scaleY;
	public boolean flipX;
	public Vector4f color = new Vector4f();
	public boolean still;
	public boolean centerRotate;
	
	public Transform() {
		setX(0);
		setY(0);
		setRotation(0);
		setScaleX(1);
		setScaleY(1);
		setFlipX(false);
		setColor(new Vector4f(1f,1f,1f,1f));
		setStill(false);
		setCenterRotate(false);
	}
	
	public Transform(float x, float y, float rotation, float scaleX, float scaleY,
			boolean flipX, Vector4f color, boolean still, boolean centerRotate) {
		setX(x);
		setY(y);
		setRotation(rotation);
		setScaleX(scaleX);
		setScaleY(scaleY);
		setFlipX(flipX);
		setColor(color != null ? color : new Vector4f(1f,1f,1f,1f));
		setStill(still);
		setCenterRotate(centerRotate);
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

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public boolean isFlipX() {
		return flipX;
	}

	public void setFlipX(boolean flipX) {
		this.flipX = flipX;
	}

	public Vector4f getColor() {
		return color;
	}

	public void setColor(Vector4f color) {
		this.color = (color != null ? color : new Vector4f(1f,1f,1f,1f));
	}

	public boolean isStill() {
		return still;
	}

	public void setStill(boolean still) {
		this.still = still;
	}

	public boolean isCenterRotate() {
		return centerRotate;
	}

	public void setCenterRotate(boolean centerRotate) {
		this.centerRotate = centerRotate;
	}
	
}
