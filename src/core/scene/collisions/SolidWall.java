package core.scene.collisions;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector2f;

public class SolidWall implements Collidable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Rectangle2D box;
	
	public boolean intersects(Rectangle2D entity) {
		return box.intersects(entity);
	}

	@Override
	public Vector2f collide(Rectangle2D entity, Vector2f velocity) {
		return null;
	}

	@Override
	public Shape getBox() {
		return box;
	}

	@Override
	public Rectangle2D getBounds() {
		return box.getBounds2D();
	}

	@Override
	public Vector2f getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector2f getNormal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean intersects(Point2D entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
