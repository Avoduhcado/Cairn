package core.render.textured;

import org.lwjgl.opengl.GL11;

import core.Camera;

@Deprecated
public class FixedSprite extends Sprite {

	/** Single frame Width */
	private float width;
	/** Single frame Height */
	private float height;
	
	public FixedSprite(String ref, float width, float height) {
		super(ref);
		
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void draw(float x, float y) {
		if(Float.isNaN(x))
			x = Camera.get().getDisplayWidth(0.5f) - (getWidth() * 0.5f);
		if(Float.isNaN(y))
			y = Camera.get().getDisplayHeight(0.5f) - (getHeight() * 0.5f);
		
		texture.bind();
		
		updateTextureOffsets();
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef((int) (x - Camera.get().frame.getX()), (int) (y - Camera.get().frame.getY()), 0f);
		GL11.glColor3f(1f, 1f, 1f);
		GL11.glScalef(1f, 1f, 1f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(textureX, textureY);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(textureXWidth, textureY);
			GL11.glVertex2f(width, 0);
			GL11.glTexCoord2f(textureXWidth, textureYHeight);
			GL11.glVertex2f(width, height);
			GL11.glTexCoord2f(textureX, textureYHeight);
			GL11.glVertex2f(0, height);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}

}
