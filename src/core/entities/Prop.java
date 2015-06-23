package core.entities;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.lwjgl.util.vector.Vector2f;

import core.Camera;
import core.render.SpriteIndex;

public class Prop extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Prop(int x, int y, String ref, float scale) {
		this.pos = new Vector2f(x, y);
		this.sprite = "props/" + ref;
		this.name = ref.contains("/") ? ref.split("/")[1] : ref;
		
		this.scale = scale;
		this.box = new Rectangle2D.Double(x, y,
				SpriteIndex.getSprite(sprite).getWidth() * scale, SpriteIndex.getSprite(sprite).getHeight() * scale);
	}
	
	public Prop(int x, int y, float width, float height, String ref) {
		this.pos = new Vector2f(x, y);
		this.sprite = "props/" + ref;
		this.name = ref.contains("/") ? ref.split("/")[1] : ref;
		
		this.scale = Camera.ASPECT_RATIO;
		this.box = new Rectangle2D.Double(x, y, width * scale, height * scale);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		if(name == null) {
			name = sprite.contains("/") ? sprite.split("/")[1] : sprite;
		}
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public void draw() {
		//SpriteIndex.getSprite(sprite).set2DScale(scale);
		//SpriteIndex.getSprite(sprite).setIntScale(true);
		SpriteIndex.getSprite(sprite).setFixedSize((int) Math.ceil(box.getWidth()), (int) Math.ceil(box.getHeight()));
		//if(!animations.isEmpty()) {
		//SpriteIndex.getSprite(sprite).setFrame(animations.get(state.toString()).getFrame());
		//}
		SpriteIndex.getSprite(sprite).draw((int) pos.x, (int) pos.y);

		drawDebug();
	}
	
	@Override
	public void setID() {
		this.ID = this.getClass().getSimpleName() + count++;
	}

}
