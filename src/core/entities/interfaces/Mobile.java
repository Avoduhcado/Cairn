package core.entities.interfaces;

import org.lwjgl.util.vector.Vector2f;

public interface Mobile {
	
	public void checkCollision();
	public void move();
	
	public boolean canRun();
	
	public Vector2f getVelocity();
	public Vector2f getSpeed();

}