package core.entities;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.Theater;
import core.entities.interfaces.Mobile;
import core.render.DrawUtils;
import core.render.SpriteIndex;

public class Backdrop extends Entity implements Mobile, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static transient int count = 0;
	
	private float depth;
	private transient Vector2f velocity;
	
	public Backdrop(float x, float y, String ref, float scale, float depth) {
		this.pos = new Vector2f(x, y);
		this.sprite = ref;
		
		this.scale = scale;
		this.box = new Rectangle2D.Double(x, y,
				SpriteIndex.getSprite(sprite).getWidth() * scale, SpriteIndex.getSprite(sprite).getHeight() * scale);
		
		this.depth = depth;
		this.velocity = new Vector2f();
	}
	
	@Override
	public void draw() {
		//SpriteIndex.getSprite(sprite).setStill(true);
		//SpriteIndex.getSprite(sprite).setIntScale(true);
		if(depth == -1f) {
			SpriteIndex.getSprite(sprite).setFixedSize(Camera.get().displayWidth, Camera.get().displayHeight);
		} else {
			SpriteIndex.getSprite(sprite).setFixedSize((int) box.getWidth(), (int) box.getHeight());
		}
		/*if(!animations.isEmpty()) {
				SpriteIndex.getSprite(sprite).setFrame(animations.get(state.toString()).getFrame());
			}*/
		SpriteIndex.getSprite(sprite).draw(pos.x, pos.y);

		if(Theater.get().debug) {
			DrawUtils.setColor(new Vector3f(1f, 0, 0.25f));
			DrawUtils.drawRect(pos.x, pos.y, getBox());
		}
	}
	
	public void update() {
		Vector2f focalVelocity = new Vector2f();
		focalVelocity.set(Camera.get().getFrameSpeed());
		focalVelocity.scale(-depth);
		this.velocity = focalVelocity;
		
		if(velocity.length() != 0) {
			move();
		}
	}
	
	public float getDepth() {
		return depth;
	}

	@Override
	public void checkCollision() {
		
	}

	@Override
	public void move() {
		Vector2f.add(velocity, pos, pos);
	}

	@Override
	public Vector2f getVelocity() {
		return null;
	}

	@Override
	public void setID() {
		this.ID = this.getClass().getSimpleName() + count++;
	}
	
	public static void reset() {
		count = 0;
	}
	
}
