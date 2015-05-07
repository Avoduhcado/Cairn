package core.scene.collisions;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.lwjgl.util.vector.Vector2f;

public class Slope implements Collidable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Line2D box;
	private Vector2f unit;
	
	public Slope(Line2D line) {
		this.box = line;
		
		unit = new Vector2f((float) (box.getX2() - box.getX1()), (float) (box.getY2() - box.getY1()));
		float magnitude = (float) Math.sqrt(Math.pow(unit.x, 2) + Math.pow(unit.y, 2));
		unit.set(unit.x / magnitude, unit.y / magnitude);
		System.out.println(unit.toString());
	}
	
	@Override
	public boolean intersects(Point2D entity) {
		/*if(box.getP1().distance(entity.getX(), entity.getY()) <= 1 || box.getP2().distance(entity.getX(), entity.getY()) <= 1) {
		 * TODO Do something with this?
			 System.out.println("ON THE EDGEEEEE");
		}*/
		
		return new Rectangle2D.Double(entity.getX() - 5, entity.getY() - 5, 10, 10).intersectsLine(box);
		
		//return box.ptSegDist(entity) < 2.5f;
		//return entity.intersectsLine(box);
		//return box.intersects(entity);
	}

	@Override
	public Vector2f collide(Rectangle2D entity, Vector2f velocity) {
		if(velocity.y == 0) {
			System.out.println("Sheheit");
			return new Vector2f(velocity.x * Math.abs(unit.x), velocity.x * unit.y);
		}
		//if(velocity.x == 0)
			System.out.println("DICKS");
		return new Vector2f(velocity.x * Math.abs(unit.x), velocity.y * Math.abs(unit.y));
	}

	@Override
	public Shape getBox() {
		return box;
	}

	@Override
	public Rectangle2D getBounds() {
		return box.getBounds();
	}
	
	public Vector2f getUnit() {
		return unit;
	}

	@Override
	public Vector2f getNormal() {
		//System.out.println(Vector2f.angle(getUnit(), new Vector2f(unit.getY(), unit.getX() * -1f)));
		return new Vector2f(unit.getY(), unit.getX() * -1f);
	}

}
