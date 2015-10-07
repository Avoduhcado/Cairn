package core.entities.utils;

import org.jbox2d.common.Vec2;

public class Transform {

	private Vec2 position;
	private float rotation;
	private float scale;
	
	public Transform(Vec2 position, float rotation, float scale) {
		this.setPosition(position);
		this.setRotation(rotation);
		this.setScale(scale);
	}

	public Vec2 getPosition() {
		return position;
	}

	public void setPosition(Vec2 position) {
		this.position = position;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
}
