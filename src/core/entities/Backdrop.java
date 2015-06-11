package core.entities;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.lwjgl.util.vector.Vector2f;

import core.Camera;
import core.Theater;
import core.render.SpriteIndex;

public class Backdrop extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static transient int count = 0;
	
	private float depth;
	
	public Backdrop(float x, float y, String ref, float scale, float depth) {
		this.pos = new Vector2f(x, y);
		this.sprite = ref;
		
		this.scale = scale;
		this.box = new Rectangle2D.Double(x, y,
				SpriteIndex.getSprite(sprite).getWidth() * scale, SpriteIndex.getSprite(sprite).getHeight() * scale);
		
		this.depth = depth;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

	@Override
	public void draw() {
		SpriteIndex.getSprite(sprite).setStill(true);
		//SpriteIndex.getSprite(sprite).setIntScale(true);
		if(depth == -1f) {
			SpriteIndex.getSprite(sprite).setFixedSize(Camera.get().displayWidth, Camera.get().displayHeight);
		} else {
			SpriteIndex.getSprite(sprite).setFixedSize((int) Math.ceil(box.getWidth()), (int) Math.ceil(box.getHeight()));
			//SpriteIndex.getSprite(sprite).setFixedSize((int) box.getWidth(), (int) box.getHeight());
		}
		//SpriteIndex.getSprite(sprite).draw(pos.x + offset.x, pos.y + offset.y);
		SpriteIndex.getSprite(sprite).draw((float) (pos.x - Camera.get().frame.getX() - (Camera.get().frame.getX() * depth)),
				(float)  (pos.y - Camera.get().frame.getY() - (Camera.get().frame.getY() * depth)));

		if(Theater.get().debug) {
			//DrawUtils.setColor(new Vector3f(1f, 0, 0.25f));
			//DrawUtils.drawRect(pos.x + offset.x, pos.y + offset.y, getBox());
			//DrawUtils.drawRect((float) (pos.x + (Camera.get().frame.getX() * depth)),
				//	(float)  (pos.y + (Camera.get().frame.getY() * depth)), getBox());
		}
	}
	
	public void update() {
		
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
