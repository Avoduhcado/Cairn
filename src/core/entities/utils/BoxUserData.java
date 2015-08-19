package core.entities.utils;

import core.Theater;
import core.utilities.MathFunctions;

public class BoxUserData {

	private float z;
	private String sprite;
	private boolean flipped;
	
	public BoxUserData(float z, String sprite, boolean flipped) {
		this.setZ(z);
		this.setSprite(sprite);
		this.setFlipped(flipped);
	}
	
	public void fall() {
		this.z = MathFunctions.clamp(this.z - Theater.getDeltaSpeed(9.8f), 0, this.z);
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
