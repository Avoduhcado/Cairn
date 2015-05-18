package core.entities;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities.interfaces.Mobile;
import core.render.SpriteIndex;

public class Fog extends Prop implements Mobile {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float speed;
	private Vector2f direction;
	private float opacity = 1f;
	
	public Fog(String ref, float scale, float speed, Vector2f direction) {
		super(0, 0, ref, scale);
		
		if(scale == -1f) {
			this.box = new Rectangle2D.Double(0, 0, Camera.get().displayWidth, Camera.get().displayHeight);
		}
		
		this.speed = speed;
		this.direction = direction;
	}
	
	@Override
	public void draw() {
		SpriteIndex.getSprite(sprite).setStill(true);
		SpriteIndex.getSprite(sprite).setColor(new Vector4f(1f, 1f, 1f, opacity));
		SpriteIndex.getSprite(sprite).setFixedSize((float) box.getWidth(), (float) box.getHeight());
		SpriteIndex.getSprite(sprite).draw(pos.x, pos.y);
		
		if(pos.x < Camera.get().frame.getX()) {
			// Draw to the right
			SpriteIndex.getSprite(sprite).draw((float) box.getMaxX(), pos.y);
			if(pos.y < Camera.get().frame.getY()) {
				SpriteIndex.getSprite(sprite).draw((float) box.getMaxX(), (float) box.getMaxY());
			} else if(box.getMaxY() > Camera.get().displayHeight) {
				SpriteIndex.getSprite(sprite).draw((float) box.getMaxX(), (float) (pos.y - box.getHeight()));
			}
		} else if(box.getMaxX() > Camera.get().displayWidth) {
			// Draw to the left
			SpriteIndex.getSprite(sprite).draw((float) (pos.x - box.getWidth()), pos.y);
			if(pos.y < Camera.get().frame.getY()) {
				SpriteIndex.getSprite(sprite).draw((float) (pos.x - box.getWidth()), (float) box.getMaxY());
			} else if(box.getMaxY() > Camera.get().displayHeight) {
				SpriteIndex.getSprite(sprite).draw((float) (pos.x - box.getWidth()), (float) (pos.y - box.getHeight()));
			}
		}
		if(pos.y < Camera.get().frame.getY()) {
			// Draw below
			SpriteIndex.getSprite(sprite).draw(pos.x, (float) box.getMaxY());
		} else if(box.getMaxY() > Camera.get().displayHeight) {
			// Draw above
			SpriteIndex.getSprite(sprite).draw(pos.x, (float) (pos.y - box.getHeight()));
		}
	}
	
	@Override
	public void update() {
		if(speed != 0) {
			move();
			
			if(pos.x > Camera.get().displayWidth || getBox().getMaxX() < 0) {
			//if(Camera.get().frame.outcode(pos.x, 0) == Rectangle2D.OUT_RIGHT 
					//|| Camera.get().frame.outcode(getBox().getMaxX(), 0) == Rectangle2D.OUT_LEFT) {
				// TODO If not still reset to right point
				//pos.setX((float) Camera.get().frame.getX());
				pos.setX(0);
				updateBox();
			}
			if(pos.y > Camera.get().displayHeight || getBox().getMaxY() < 0) {
			//if(Camera.get().frame.outcode(0, pos.y) == Rectangle2D.OUT_BOTTOM 
					//|| Camera.get().frame.outcode(0, getBox().getMaxY()) == Rectangle2D.OUT_TOP) {
				//pos.setY((float) Camera.get().frame.getY());
				pos.setY(0);
				updateBox();
			}
		}
	}
	
	@Override
	public void updateBox() {
		box = new Rectangle2D.Double(pos.x, pos.y, box.getWidth(), box.getHeight());
	}

	@Override
	public void checkCollision() {
	}

	@Override
	public void move() {
		direction.normalise();
		direction.scale(speed);
		Vector2f.add(direction, pos, pos);
		updateBox();
	}
	
	@Override
	public boolean canRun() {
		return false;
	}

	@Override
	public Vector2f getVelocity() {
		return direction;
	}
	
	@Override
	public Vector2f getSpeed() {
		return direction;
	}
	
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

}
