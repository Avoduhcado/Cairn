package core.entities;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.Theater;
import core.render.DrawUtils;
import core.render.SpriteIndex;

public class Backdrop extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static transient int count = 0;
	
	private float depth;
	private transient Vector2f offset;
	
	public Backdrop(float x, float y, String ref, float scale, float depth) {
		this.pos = new Vector2f(x, y);
		this.sprite = ref;
		
		this.scale = scale;
		this.box = new Rectangle2D.Double(x, y,
				SpriteIndex.getSprite(sprite).getWidth() * scale, SpriteIndex.getSprite(sprite).getHeight() * scale);
		
		this.depth = depth;
		this.offset = new Vector2f();
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.offset = new Vector2f();
	}

	@Override
	public void draw() {
		//SpriteIndex.getSprite(sprite).setStill(true);
		//SpriteIndex.getSprite(sprite).setIntScale(true);
		if(depth == -1f) {
			SpriteIndex.getSprite(sprite).setFixedSize(Camera.get().displayWidth, Camera.get().displayHeight);
		} else {
			SpriteIndex.getSprite(sprite).setFixedSize((int) Math.ceil(box.getWidth()), (int) Math.ceil(box.getHeight()));
			//SpriteIndex.getSprite(sprite).setFixedSize((int) box.getWidth(), (int) box.getHeight());
		}
		SpriteIndex.getSprite(sprite).draw(pos.x + offset.x, pos.y + offset.y);

		if(Theater.get().debug) {
			DrawUtils.setColor(new Vector3f(1f, 0, 0.25f));
			DrawUtils.drawRect(pos.x + offset.x, pos.y + offset.y, getBox());
		}
	}
	
	public void update() {
		Vector2f focalVelocity = new Vector2f();
		focalVelocity.set(Camera.get().getFrameSpeed());
		focalVelocity.scale(-depth);
		if(focalVelocity.length() != 0) {
			Vector2f.add(focalVelocity, offset, offset);
		}
	}
	
	public float getDepth() {
		return depth;
	}

	@Override
	public void setID() {
		this.ID = this.getClass().getSimpleName() + count++;
	}
	
	public static void reset() {
		count = 0;
	}
	
}
