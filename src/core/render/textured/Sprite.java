package core.render.textured;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import core.Camera;
import core.Theater;

public class Sprite {
	
	protected Texture texture;

	protected float width;
	protected float height;
	protected float textureX;
	protected float textureY;
	protected float textureXWidth;
	protected float textureYHeight;
	
	protected int maxDirection = 1;
	protected int maxFrame = 1;
	protected int direction = 0;
	protected int frame = 0;
	private float animStep;
	
	protected Vector4f color = new Vector4f(1f, 1f, 1f, 1f);
	protected Vector4f rotation = new Vector4f(0f, 0f, 0f, 0f);
	protected float rotateSpeed = 0f;
	protected Vector3f scale = new Vector3f(1f, 1f, 1f);
	
	protected boolean still;
	protected boolean flipped;
	protected Vector2f fixedSize;
	protected boolean intScale;
	
	public Sprite(String ref) {
		try {
			setTexture(ref);
		} catch (IOException | RuntimeException e) {
			setErrorTexture();
		}
		
		if(ref.contains("^")) {
			String[] temp = ref.split("\\^");
			maxFrame = Integer.parseInt(temp[1]);
			if(temp.length > 2)
				maxDirection = Integer.parseInt(temp[2]);
		}
		
		width = texture.getWidth() / maxFrame;
		height = texture.getHeight() / maxDirection;
	}
	
	public void draw(float x, float y) {
		if(Float.isNaN(x))
			x = Camera.get().getDisplayWidth(0.5f) - (getWidth() / 2f);
		if(Float.isNaN(y))
			y = Camera.get().getDisplayHeight(0.5f) - (getHeight() / 2f);
		
		texture.bind();
		
		updateTextureOffsets();
		
		GL11.glPushMatrix();
		
		if(still) {
			// Static positioning
			GL11.glTranslatef(x, y, 0f);
		} else {
			// Positioning relative to camera movement
			if(intScale) {
				GL11.glTranslatef((int) (x - Camera.get().frame.getX()),
						(int) (y - Camera.get().frame.getY()), 0f);
			} else {
				GL11.glTranslatef((float) (x - Camera.get().frame.getX()),
						(float) (y - Camera.get().frame.getY()), 0f);
			}
		}
		
		//GL11.glScalef(scale.x * Camera.get().getScale(), scale.y * Camera.get().getScale(), scale.z);
		GL11.glScalef(scale.x, scale.y, scale.z);
		//GL11.glTranslatef(Camera.get().getDisplayWidth() / 2f, Camera.get().getDisplayHeight() / 2f, 0);
		GL11.glColor4f(color.x, color.y, color.z, color.w);
		if(rotation.length() != 0) {
			// Uncomment to base rotation from center of object
			//GL11.glTranslatef(getWidth() / 2f, getHeight() / 2f, 0f);
			GL11.glRotatef(rotation.w, rotation.x, rotation.y, rotation.z);
			//GL11.glTranslatef(-getWidth() / 2f, -getHeight() / 2f, 0f);
		}
		if(flipped) {
			GL11.glRotatef(180f, 0, 1f, 0);
		}
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(textureX, textureY);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(textureXWidth, textureY);
			GL11.glVertex2f(getDrawWidth(), 0);
			GL11.glTexCoord2f(textureXWidth, textureYHeight);
			GL11.glVertex2f(getDrawWidth(), getDrawHeight());
			GL11.glTexCoord2f(textureX, textureYHeight);
			GL11.glVertex2f(0, getDrawHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void animate() {
		if(maxFrame > 1) {
			animStep += Theater.getDeltaSpeed(0.025f);
			if (animStep >= 0.16f) {
				animStep = 0f;
				frame++;
				if (frame >= maxFrame) {
					frame = 0;
				}
			}
		}
		
		if(rotateSpeed != 0f) {
			rotation.w += Theater.getDeltaSpeed(0.025f) * rotateSpeed;
			if(rotation.w > 360f)
				rotation.w = 0f;
			else if(rotation.w < 0f)
				rotation.w = 360f;
		}
	}
	
	public void updateTextureOffsets() {
		textureX = width * frame;
		textureY = height * direction;
		textureXWidth = (width * frame) + width;
		textureYHeight = (height * direction) + height;
	}
	
	public void setTexture(String ref) throws IOException {
		this.texture = TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream(System.getProperty("resources") + "/sprites/" + ref + ".png"));
	}
	
	private void setErrorTexture() {
		try {
			setTexture("Error");
		} catch (IOException e) {
			System.err.println("Resources folder may be missing.");
		}
	}
	
	public void destroy() {
		this.texture.release();
	}
	
	public boolean isAnimated() {
		return maxFrame > 1;
	}
	
	public int getFrame() {
		return frame;
	}
	
	public void setFrame(int frame) {
		this.frame = frame;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getMaxFrame() {
		return maxFrame;
	}

	/**
	 * Actual image width!!
	 * 
	 * @return Width of single frame of image
	 */
	public float getWidth() {
		return texture.getImageWidth() / maxFrame;
	}
	
	/**
	 * Actual image height!!
	 * 
	 * @return Height of single frame of image
	 */
	public float getHeight() {
		return texture.getImageHeight() / maxDirection;		
	}
	
	/**
	 * Get either a fixed width size or the frame width.
	 * @return Width of image to draw
	 */
	public float getDrawWidth() {
		if(fixedSize == null)
			return texture.getImageWidth() / maxFrame;
	
		return fixedSize.x;
	}
	
	/**
	 * Get either a fixed height size or the frame height.
	 * @return Height of image to draw
	 */
	public float getDrawHeight() {
		if(fixedSize == null)
			return texture.getImageHeight() / maxDirection;
		
		return fixedSize.y;
	}
	
	public void setStill(boolean still) {
		this.still = still;
	}

	public boolean isFlipped() {
		return flipped;
	}
	
	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
	
	public void setFixedSize(float width, float height) {
		this.fixedSize = new Vector2f(width, height);
	}
	
	public Vector4f getColor() {
		return color;
	}
	
	public void setColor(Vector4f color) {
		this.color = color;
	}
	
	public Vector4f getRotation() {
		return rotation;
	}
	
	public void setRotation(Vector4f rotation, float speed) {
		this.rotation = rotation;
		setRotateSpeed(speed);
	}
	
	public void set2DRotation(float rotation, float speed) {
		this.rotation.w = rotation;
		this.rotation.z = 1f;
		setRotateSpeed(speed);
	}
	
	public float getRotateSpeed() {
		return rotateSpeed;
	}
	
	public void setRotateSpeed(float speed) {
		this.rotateSpeed = speed;
		//this.maxSpeed = speed;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
	public void set2DScale(float scale) {
		this.scale.x = scale;
		this.scale.y = scale;
	}
	
	public void setIntScale(boolean intScale) {
		this.intScale = intScale;
	}

}
