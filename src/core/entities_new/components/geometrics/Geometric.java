package core.entities_new.components.geometrics;

import org.lwjgl.util.vector.Vector2f;

public interface Geometric {

	public void move();
	
	public Vector2f getPosition();
	public void setPosition(Vector2f position);
	public float getX();
	public float getY();
	
}
