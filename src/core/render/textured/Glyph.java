package core.render.textured;

import java.awt.Color;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

import core.Camera;
import core.render.SpriteList;
import core.render.transform.Transform;
import core.utilities.text.GameFont;
import core.utilities.text.TextModifier;

public class Glyph {

	// TODO Offset values are wonky at small scaling factors
	
	private String page;
	private int xOffset, yOffset;
	private int xAdvance;
	private int width, height;
	
	private Transform transform;
	
	private Vector3f scale = new Vector3f(GameFont.defaultSize, GameFont.defaultSize, 1f);
	private Vector4f color = new Vector4f(0f, 0f, 0f, 0f);
	
	public Glyph(String page, int x, int y, int width, int height, int xOffset, int yOffset, int xAdvance) {
		this.page = page.replaceFirst(".png", "");
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xAdvance = xAdvance;
		this.width = width;
		this.height = height;

		transform = new Transform();
		setupVertices(x, y, width, height);
	}
	
	public void draw(float x, float y, TextModifier modifier) {		
		setTransform(x, y, modifier);

		SpriteList.get(page).draw(transform);
		
		color.set(0, 0, 0, 0);
	}

	public void draw(float x, float y, TextModifier modifier, Color color) {
		setColor(color);
		draw(x, y, modifier);
	}

	private void setTransform(float x, float y, TextModifier modifier) {
		transform.setStill(modifier.still);
		if(modifier.size != 0) {
			setScale(modifier.size);
		} else {
			setScale(GameFont.defaultSize);
		}
		if(color.length() == 0f) {
			setColor(modifier.color);
		}
		transform.setX(x + getXOffset());
		transform.setY(y + getYOffset());
		transform.width = getWidth();
		transform.height = getHeight();
				
		transform.setColor(color);
	}
	
	private void setupVertices(int x, int y, int width, int height) {
		Texture texture = SpriteList.get(page).getTexture();
		float xRatio = texture.getWidth() / texture.getImageWidth();
		float yRatio = texture.getHeight() / texture.getImageHeight();
		
		transform.setTextureOffsets(new Vector4f(
				(x < 0 ? 1 + (x * xRatio) : x * xRatio), 
				(y < 0 ? 1 + (y * yRatio) : y * yRatio),
				(x < 0 ? 1 + ((x * xRatio) + (width * xRatio)) : (x * xRatio) + (width * xRatio)), 
				(y < 0 ? 1 + ((y * yRatio) + (height * yRatio)) : (y * yRatio) + (height * yRatio))));
	}
	
	public void setScale(float scale) {
		this.scale.set(scale, scale, 1f);
	}
	
	public void setColor(Color color) {
		this.color.set(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
	}
	
	public float getWidth() {
		return width * scale.x;
	}
	
	public float getHeight() {
		return height * scale.y;
	}
	
	public float getLineHeight() {
		return getHeight() + getYOffset();
	}
	
	public float getXAdvance() {
		return xAdvance * scale.x;
	}
	
	public float getXOffset() {
		return xOffset * scale.x;
	}
	
	public float getYOffset() {
		return yOffset * scale.y;
	}

}
