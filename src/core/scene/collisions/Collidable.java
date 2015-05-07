package core.scene.collisions;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.lwjgl.util.vector.Vector2f;

public interface Collidable extends Serializable  {
	
	public abstract boolean intersects(Point2D entity);
	public abstract Vector2f collide(Rectangle2D entity, Vector2f velocity);
	
	public abstract Shape getBox();
	public abstract Rectangle2D getBounds();
	public abstract Vector2f getUnit();
	public abstract Vector2f getNormal();
	
}
