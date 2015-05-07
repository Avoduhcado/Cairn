package core.scene;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector2f;

public class Platform {

	private Rectangle2D box;
	private boolean oneWay;
	private boolean slope;
	private String property;
	
	public Platform(Rectangle2D box) {
		//if(box.getHeight() == 1)
			//oneWay = true;
		this.box = box;
	}
	
	public Platform(Rectangle2D box, String property) {
		this.box = box;
		this.property = property;
	}
	
	public boolean intersects(Rectangle2D entity, Vector2f velocity) {
		if(entity.intersects(box)) {
			if(oneWay && entity.getMaxY() <= box.getMaxY() && velocity.y > 0)
				return true;
			else if(oneWay) {
				return false;
			} else {
				return true;
			}
		}
		
		return false;
	}
	
	public Rectangle2D getBox() {
		return box;
	}
	
	// TODO
	public boolean isStep() {
		return (getBox().getWidth() < 32 && getBox().getWidth() > 1);
	}
	
	public boolean canStep(Rectangle2D entity) {
		return (entity.getMaxY() - this.box.getY() < 2);
	}

	public boolean canJumpThrough(Rectangle2D entity, Vector2f velocity) {
		if(this.isOneWay()) {
			if(velocity.y > 0 && entity.getMaxY() - 1 < this.box.getY())
				return false;
			else if(velocity.y > 0)
				return true;
		}
		
		return false;
	}
	
	public boolean isOneWay() {
		return oneWay;
	}
	
	public void setOneWay(boolean oneWay) {
		this.oneWay = oneWay;
	}
	
	public boolean isSlope() {
		return slope;
	}
	
	public Vector2f getSlope() {
		return new Vector2f((float) ((box.getMaxX() - box.getX()) / box.getWidth()),
				(float) ((box.getY() - box.getMaxY()) / box.getHeight()));
	}
	
	public void setSlope(boolean slope) {
		this.slope = slope;
	}
	
	public String getProperty() {
		return property;
	}
	
	public void setProperty(String property) {
		this.property = property;
	}
}
