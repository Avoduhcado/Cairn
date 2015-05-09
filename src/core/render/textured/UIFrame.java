package core.render.textured;

import java.awt.geom.Rectangle2D;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import core.Camera;
import core.render.TextureLoader;

public class UIFrame extends Sprite {

	private float opacity = 0.8f;
	private float width;
	private float height;
	
	public UIFrame(String ref) {
		super(ref);
		
		width = getWidth() / 3f;
		height = getHeight() / 3f;
	}

	public void draw(float x, float y, Rectangle2D box) {
		if(Float.isNaN(x))
			x = Camera.get().getDisplayWidth(0.5f) - (getWidth() / 2f);
		if(Float.isNaN(y))
			y = Camera.get().getDisplayHeight(0.5f) - (getHeight() / 2f);
		
		x -= width / 2f;
		y -= height / 2f;
		
		texture.bind();
				
		GL11.glPushMatrix();
		
		if(still)
			GL11.glTranslatef((int) x, (int) y, 0f);
		else
			GL11.glTranslatef((int) (x - Camera.get().frame.getX()), (int) (y - Camera.get().frame.getY()), 0f);
		if(Camera.get().getScale() < 1)
			GL11.glScalef(Camera.get().getScale() + 1, Camera.get().getScale() + 1, 1f);
		GL11.glColor4f(1f, 1f, 1f, opacity);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		for(int a = 0; a<3; a++) {
			for(int b = 0; b<3; b++) {
				if(a % 2 == 0 && b % 2 == 0) {
					setCornerQuads(a, b, box);
				} else if((a + 1) % 2 == 0 && (b + 1) % 2 == 0) {
					if(box.getHeight() > height && box.getWidth() > width)
						setInnerQuads(a, b, box);
				} else if ((a + 1) % 2 != 0) {
					if(box.getHeight() > height)
						setVertQuads(a, b, box);
				} else {
					if(box.getWidth() > width)
						setHorizQuads(a, b, box);
				}
			}
		}
		
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void setCornerQuads(int x, int y, Rectangle2D box) {
		double texWidth = texture.getWidth() / 3f;
		double texHeight = texture.getHeight() / 3f;
		double verWidth = box.getWidth() * (x / 2);
		double verHeight = box.getHeight() * (y / 2);
				
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2d(x * texWidth, y * texHeight);
		    GL11.glVertex2d(verWidth, verHeight);
		    
		    GL11.glTexCoord2d((x * texWidth) + texWidth, y * texHeight);
		    GL11.glVertex2d(verWidth + width, verHeight);
		    
		    GL11.glTexCoord2d((x * texWidth) + texWidth, (y * texHeight) + texHeight);
		    GL11.glVertex2d(verWidth + width, verHeight + height);
		    
		    GL11.glTexCoord2d(x * texWidth, (y * texHeight) + texHeight);
		    GL11.glVertex2d(verWidth, verHeight + height);
		}
		GL11.glEnd();
	}
	
	public void setVertQuads(int x, int y, Rectangle2D box) {
		double texWidth = texture.getWidth() / 3f;
		double texHeight = texture.getHeight() / 3f;
		double verWidth = (box.getWidth()) * x / 2;
		double verHeight = box.getHeight();
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2d(x * texWidth, y * texHeight);
		    GL11.glVertex2d(verWidth, height);
		    
		    GL11.glTexCoord2d((x * texWidth) + texWidth, y * texHeight);
		    GL11.glVertex2d(verWidth + width, height);
		    
		    GL11.glTexCoord2d((x * texWidth) + texWidth, (y * texHeight) + texHeight);
		    GL11.glVertex2d(verWidth + width, verHeight);
		    
		    GL11.glTexCoord2d(x * texWidth, (y * texHeight) + texHeight);
		    GL11.glVertex2d(verWidth, verHeight);
		}
	}
	
	public void setHorizQuads(int x, int y, Rectangle2D box) {
		double texWidth = texture.getWidth() / 3f;
		double texHeight = texture.getHeight() / 3f;
		double verWidth = box.getWidth();
		double verHeight = (box.getHeight()) * y / 2;
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2d(texWidth, y * texHeight);
		    GL11.glVertex2d(width, verHeight);
		    
		    GL11.glTexCoord2d((x * texWidth) + texWidth, y * texHeight);
		    GL11.glVertex2d(verWidth, verHeight);
		    
		    GL11.glTexCoord2d((x * texWidth) + texWidth, (y * texHeight) + texHeight);
		    GL11.glVertex2d(verWidth, verHeight + height);
		    
		    GL11.glTexCoord2d(x * texWidth, (y * texHeight) + texHeight);
		    GL11.glVertex2d(width, verHeight + height);
		}
	}
	
	public void setInnerQuads(int x, int y, Rectangle2D box) {
		double texWidth = texture.getWidth() / 3f;
		double texHeight = texture.getHeight() / 3f;
		double verWidth = box.getWidth();
		double verHeight = box.getHeight();
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2d(x * texWidth, y * texHeight);
			GL11.glVertex2d(width, height);
		    
		    GL11.glTexCoord2d((x * texWidth) + texWidth, y * texHeight);
		    GL11.glVertex2d(verWidth, height);
		    
		    GL11.glTexCoord2d((x * texWidth) + texWidth, (y * texHeight) + texHeight);
		    GL11.glVertex2d(verWidth, verHeight);
		    
		    GL11.glTexCoord2d(x * texWidth, (y * texHeight) + texHeight);
		    GL11.glVertex2d(width, verHeight);
		}
	}
	
	public void setTexture(String ref) {
		this.texture = TextureLoader.get().getSlickTexture(System.getProperty("resources") + "/ui/" + ref + ".png");
	}
	
	public float getOpacity() {
		return opacity;
	}
	
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public float getWidth() {
		return texture.getImageWidth();
	}
	
	public float getHeight() {
		return texture.getImageHeight();
	}
	
}
