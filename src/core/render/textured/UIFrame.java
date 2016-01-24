package core.render.textured;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

import core.render.SpriteList;
import core.render.transform.Transform;

public class UIFrame {

	private String frame;
	
	private float opacity = 0.8f;
	private boolean still;
	
	private Transform transform;
	
	public UIFrame(String ref) {
		this.frame = ref;
		
		this.transform = new Transform();
	}

	private void setTransform(int row, int col, Rectangle2D box) {
		Texture texture = SpriteList.get(frame).getTexture();
		transform.clear();
		transform.textureOffsets = new Vector4f();
		transform.color = new Vector4f(1f, 1f, 1f, opacity);
		transform.still = still;
		
		switch(row) {
		case 0:
			transform.y = (float) (box.getY() - (texture.getImageHeight() / 3f));
			transform.height = (texture.getImageHeight() / 3f);
			transform.textureOffsets.y = 0;
			transform.textureOffsets.w = (texture.getHeight() / 3f);
			break;
		case 1:
			transform.y = (float) box.getY();
			transform.height = (float) box.getHeight();
			transform.textureOffsets.y = (texture.getHeight() / 3f);
			transform.textureOffsets.w = (texture.getHeight() * 0.667f);
			break;
		case 2:
			transform.y = (float) box.getMaxY();
			transform.height = (texture.getImageHeight() / 3f);
			transform.textureOffsets.y = (texture.getHeight() * 0.667f);
			transform.textureOffsets.w = (texture.getHeight());
			break;
		}
		
		switch(col) {
		case 0:
			transform.x = (float) (box.getX() - (texture.getImageWidth() / 3f));
			transform.width = (texture.getImageWidth() / 3f);
			transform.textureOffsets.x = 0;
			transform.textureOffsets.z = (texture.getWidth() / 3f);
			break;
		case 1:
			transform.x = (float) box.getX();
			transform.width = (float) box.getWidth();
			transform.textureOffsets.x = (texture.getWidth() / 3f);
			transform.textureOffsets.z = (texture.getWidth() * 0.667f);
			break;
		case 2:
			transform.x = (float) box.getMaxX();
			transform.width = (texture.getImageWidth() / 3f);
			transform.textureOffsets.x = (texture.getWidth() * 0.667f);
			transform.textureOffsets.z = (texture.getWidth());
			break;
		}
	}
	
	/**
	 * Draw frame around supplied <code>Rectangle2D</code> box
	 * @param box
	 */
	public void draw(Rectangle2D box) {
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 3; col++) {
				setTransform(row, col, box);
								
				SpriteList.get(frame).draw(transform);
			}
		}
	}
	
	public float getOpacity() {
		return opacity;
	}
	
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}
	
	public boolean isStill() {
		return still;
	}
	
	public void setStill(boolean still) {
		this.still = still;
	}
	
}
