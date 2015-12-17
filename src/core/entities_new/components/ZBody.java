package core.entities_new.components;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.lwjgl.util.vector.Vector2f;

import core.setups.Stage_new;

public class ZBody implements Geometric {

	private Body body;
	private float z, groundZ;

	public ZBody(Body body) {
		this.body = body;
	}

	@Override
	public Vector2f getPosition() {
		return new Vector2f(body.getPosition().x * Stage_new.SCALE_FACTOR, body.getPosition().y * Stage_new.SCALE_FACTOR);
	}

	/**
	 * Not recommended usage for ZBody
	 */
	@Override
	public void setPosition(Vector2f position) {
		body.setTransform(new Vec2(position.x, position.y), 0);
	}

	@Override
	public float getX() {
		return body.getPosition().x * Stage_new.SCALE_FACTOR;
	}

	@Override
	public float getY() {
		return body.getPosition().y * Stage_new.SCALE_FACTOR;
	}
	
	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getGroundZ() {
		return groundZ;
	}

	public void setGroundZ(float groundZ) {
		this.groundZ = groundZ;
	}
	
	public float getScreenY() {
		return (body.getPosition().y * Stage_new.SCALE_FACTOR) + z;
	}

}
