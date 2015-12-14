package core.entities_new;

import org.jbox2d.dynamics.Body;

public class ZBody {

	private Body body;
	private float z, groundZ;

	public ZBody(Body body) {
		this.body = body;
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
		return (body.getPosition().y * 30f) + z;
	}

}
