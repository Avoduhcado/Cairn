package core.entities.utils;

import org.jbox2d.common.Vec2;

import core.Theater;
import core.utilities.MathFunctions;

public class BoxUserData {

	private final float startY;
	private float z;
	private String sprite;
	private boolean flipped;
	
	public BoxUserData(float y, float z, String sprite, boolean flipped) {
		this.startY = y;
		this.setZ(z);
		this.setSprite(sprite);
		this.setFlipped(flipped);
	}
	
	public void fall(Vec2 linearVelocity) {
		//this.z -= linearVelocity.y;
		this.z = MathFunctions.clamp(this.z - Theater.getDeltaSpeed(linearVelocity.y / 30), 0, this.z);
	}

	public boolean isFalling() {
		return z > 0;
	}
	
	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public String getSprite() {
		return sprite;
	}

	public void setSprite(String sprite) {
		this.sprite = sprite;
	}
	
	@Override
	public String toString() {
		return "Z: " + z + " Sprite: " + sprite;
	}

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
	
}
