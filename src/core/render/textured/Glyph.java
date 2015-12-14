package core.render.textured;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import core.Camera;
import core.utilities.text.GameFont;

public class Glyph extends Sprite {

	// TODO Offset values are wonky at small scaling factors
	
	private int xOffset, yOffset;
	private int xAdvance;
	private int width, height;
	
	private Vector3f scale = new Vector3f(GameFont.defaultSize, GameFont.defaultSize, 1f);
	private Vector4f color = new Vector4f(0f, 0f, 0f, 1f);
	
	public Glyph(String ref, int x, int y, int width, int height, int xOffset, int yOffset, int xAdvance) {
		super(ref);
		
		setupVertices(x, y, width, height);
		//System.out.println(textureX + " " + textureY + " " + textureXWidth + " " + textureYHeight);
				
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xAdvance = xAdvance;
		this.width = width;
		this.height = height;
	}
	
	public void draw(float x, float y) {
		if(Float.isNaN(x))
			x = Camera.get().getDisplayWidth(0.5f) - (getWidth() * 0.5f);
		if(Float.isNaN(y))
			y = Camera.get().getDisplayHeight(0.5f) - (getHeight() * 0.5f);
		
		texture.bind();
		
		//updateTextureOffsets();
		
		GL11.glPushMatrix();
		
		if(still) {
			GL11.glTranslatef((x + getXOffset()), (y + getYOffset()), 0f);
		} else {
			GL11.glTranslated(((x + getXOffset()) - Camera.get().frame.getX()),
					((y + getYOffset()) - Camera.get().frame.getY()), 0f);
		}
		GL11.glColor4f(color.x, color.y, color.z, color.w);
		//GL11.glScalef(scale.x, scale.y, 1f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(textureX, textureY);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(textureXWidth, textureY);
			GL11.glVertex2f(getWidth(), 0);
			GL11.glTexCoord2f(textureXWidth, textureYHeight);
			GL11.glVertex2f(getWidth(), getHeight());
			GL11.glTexCoord2f(textureX, textureYHeight);
			GL11.glVertex2f(0, getHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void setupVertices(int x, int y, int width, int height) {
		float xRatio = texture.getWidth() / texture.getImageWidth();
		float yRatio = texture.getHeight() / texture.getImageHeight();
		
		textureX = x * xRatio;
		textureY = y * yRatio;
		textureXWidth = (x * xRatio) + (width * xRatio);
		textureYHeight = (y * yRatio) + (height * yRatio);
	}
	
	@Override
	public void setTexture(String ref) throws IOException {
		this.texture = TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream(System.getProperty("resources") + "/fonts/" + ref));
		/*this.texture = new DeferredTexture(ResourceLoader.getResourceAsStream(System.getProperty("resources") + "/fonts/" + ref), 
				ref, false, 0, null);
		this.texture.load();
		LoadingList.get().remove(texture);*/
	}
	
	public void setColor(Color color) {
		this.color.set(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
	}
	
	public void setSize(Vector2f size) {
		this.scale.set(size.x, size.y, 1f);
	}
	
	public void setSize(float size) {
		this.scale.set(size, size, 1f);
	}
	
	@Override
	public float getWidth() {
		return width * scale.x;
	}
	
	@Override
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
