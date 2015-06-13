package core.entities;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.render.DrawUtils;
import core.render.SpriteIndex;
import core.utilities.text.Text;

public class Prop extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static transient int count = 0;
	
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
		//if(Camera.get().frame.intersects(getBox())) {
			//SpriteIndex.getSprite(sprite).set2DScale(scale);
			//SpriteIndex.getSprite(sprite).setIntScale(true);
			SpriteIndex.getSprite(sprite).setFixedSize((int) Math.ceil(box.getWidth()), (int) Math.ceil(box.getHeight()));
			//if(!animations.isEmpty()) {
				//SpriteIndex.getSprite(sprite).setFrame(animations.get(state.toString()).getFrame());
			//}
			SpriteIndex.getSprite(sprite).draw((int) pos.x, (int) pos.y);
			
			if(Theater.get().debug) {
				DrawUtils.setColor(new Vector3f(1f, 0, 0));
				DrawUtils.drawRect(pos.x, pos.y, getBox());
				Text.getDefault().drawString(getID(), pos.x, pos.y);
			}
		//}
	}
	
	@Override
	public void setID() {
		this.ID = this.getClass().getSimpleName() + count++;
	}
	
	public static void reset() {
		count = 0;
	}

}
