package core.entities_new;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector2f;

public interface Physical {

	public void init();
	
	public void update();
	public void draw();
	public void drawDebug();
	
	public Vector2f getPosition();
	public Rectangle2D getBox();
	
}
