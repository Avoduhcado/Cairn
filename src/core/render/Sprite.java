package core.render;

import java.io.IOException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import core.Camera;
import core.utilities.Resources;

public class Sprite {

	private Texture texture;
	private float texXOffset, texYOffset, texWidth, texHeight;
	
	public Sprite(String texture) {
		try {
			this.texture = load(texture);
		} catch (IOException e) {
			loadError();
		}
	}
	
	private Texture load(String ref) throws IOException {
		return TextureLoader.getTexture("PNG",
				//ResourceLoader.getResourceAsStream(System.getProperty("resources") + "/sprites/" + ref + ".png"));
				//Files.newInputStream(Resources.get().getFileSystem().getPath("/" + ref + ".png")));
				Resources.get().getResource(ref + ".png"));
	}
	
	private void loadError() {
		try {
			this.texture = load("error");
		} catch (IOException e2) {
			e2.printStackTrace();
			System.err.println("Resources folder may be missing.");
		}
	}

	public void draw(Transform transform) {
		texture.bind();
		updateTextureOffsets(transform);

		GL11.glPushMatrix();
		
		if(transform.still) {
			GL11.glTranslatef(transform.x, transform.y, 0f);
		} else {
			GL11.glTranslated(transform.x - Camera.get().frame.getX(), transform.y - Camera.get().frame.getY(), 0f);
		}
		
		GL11.glScalef(Camera.ASPECT_RATIO * transform.scaleX, Camera.ASPECT_RATIO * transform.scaleY, 0f);
		
		if(transform.flipX) {
			GL11.glRotatef(180f, 0, 1, 0);
		}
		
		if(transform.centerRotate) {
			GL11.glTranslatef(texture.getImageWidth() / 2f, texture.getImageHeight() / 2f, 0);
			// Use Math.toDegrees for dope cool spinning effect
			GL11.glRotated(transform.rotation, 0, 0, 1);
			GL11.glTranslatef(-texture.getImageWidth() / 2f, -texture.getImageHeight() / 2f, 0);
		} else {
			GL11.glRotated(transform.rotation, 0, 0, 1);
		}
		
		GL11.glColor4f(transform.color.x, transform.color.y, transform.color.z, transform.color.w);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(texXOffset, texYOffset);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(texWidth, texYOffset);
			GL11.glVertex2f(getWidth(), 0);
			GL11.glTexCoord2f(texWidth, texHeight);
			GL11.glVertex2f(getWidth(), getHeight());
			GL11.glTexCoord2f(texXOffset, texHeight);
			GL11.glVertex2f(0, getHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	private void updateTextureOffsets(Transform transform) {
		texXOffset = 0;
		texYOffset = 0;
		texWidth = texture.getWidth();
		texHeight = texture.getHeight();
	}
	
	public float getWidth() {
		return texture.getImageWidth();
	}
	
	public float getHeight() {
		return texture.getImageHeight();
	}
	
}
