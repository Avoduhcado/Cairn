package core.entities.interfaces;

import org.lwjgl.util.vector.Vector2f;

public interface Mobile {
	
	public abstract void checkCollision();
	public abstract void move();
	
	public abstract Vector2f getVelocity();

}