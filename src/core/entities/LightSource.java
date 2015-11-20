package core.entities;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.Theater;
import core.render.DrawUtils;
import core.render.SpriteIndex;

@Deprecated
public class LightSource extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private Vector2f radius;
	private transient float flicker;
	private transient float flickerTime;
	private transient float flickerDelay = 0.05f;
	private Vector3f color = new Vector3f(1f, 1f, 1f);
	
	public LightSource(int x, int y, String ref, float scale, Vector2f radius, Vector3f color) {
		this.pos = new Vector2f(x, y);
		this.name = ref;
		this.sprite = ref;
		try {
			if(Display.isCurrent() && ref != null) {
				SpriteIndex.getSprite(sprite);
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		this.scale = scale;
		this.box = new Rectangle2D.Double(x, y, radius.x * scale, radius.y * scale);
		
		this.radius = radius;
		if(color != null) {
			this.color = color;
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		flickerDelay = 0.05f;
	}
	
	@Override
	public void update() {
		if(sprite != null) {
			SpriteIndex.getSprite(sprite).animate();
		}
		
		if(flickerTime < flickerDelay) {
			flickerTime += Theater.getDeltaSpeed(0.025f);
		} else {
			flicker = (float) (Math.random() * 7.5f);
			flickerTime = 0;
		}
	}
	
	@Override
	public void draw() {
		if(Camera.get().frame.intersects(box)) {
			if(sprite != null) {
				SpriteIndex.getSprite(sprite).setFixedSize((int) (SpriteIndex.getSprite(sprite).getWidth() * scale),
						(int) (SpriteIndex.getSprite(sprite).getHeight() * scale));
				/*if(!animations.isEmpty()) {
					SpriteIndex.getSprite(sprite).setFrame(animations.get(state.toString()).getFrame());
				}*/
				SpriteIndex.getSprite(sprite).draw(pos.x, pos.y);
				
				if(Theater.get().debug) {
					DrawUtils.drawRect(pos.x, pos.y, getBox());
				}
			}
		}
	}
	
	public void drawLight(Vector4f background) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) (pos.x - Camera.get().frame.getX()), (float) (pos.y - Camera.get().frame.getY()), 0f);
		GL11.glColor4f(color.x, color.y, color.z, 0f);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN); 
		{
			GL11.glVertex2f(0, 0);
			GL11.glColor4f(background.x, background.y, background.z, background.w);
			for(int i = 0; i<=360; i+=10) {
				GL11.glVertex2f((float) Math.sin(Math.toRadians(i)) * (radius.x + flicker),
						(float) Math.cos(Math.toRadians(i)) * (radius.y + flicker));
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void setParent(Entity parent) {
		// TODO
		this.pos = parent.pos;
	}
	
	public void setDelay(float flickerDelay) {
		this.flickerDelay = flickerDelay;
	}

	@Override
	public void setID() {
		this.ID = this.getClass().getSimpleName() + count++;
	}
	
}
