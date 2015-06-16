package core.entities;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector2f;
import core.render.SpriteIndex;

public class Prop extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Prop(int x, int y, String ref, float scale) {
		this.pos = new Vector2f(x, y);
		this.sprite = ref;
		this.name = ref;
		
		this.scale = scale;
		this.box = new Rectangle2D.Double(x, y,
				SpriteIndex.getSprite(sprite).getWidth() * scale, SpriteIndex.getSprite(sprite).getHeight() * scale);
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
