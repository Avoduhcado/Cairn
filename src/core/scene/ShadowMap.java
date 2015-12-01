package core.scene;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.dynamics.BodyType;
import org.lwjgl.opengl.GL11;
import core.Theater;
import core.entities_new.Entity;
import core.render.DrawUtils;
import core.utilities.MathFunctions;

public class ShadowMap {
	
	private float[] startSizes = {2f, 5f, 12f, 20f};
	private float[] sizes = {2f, 5f, 12f, 20f};
	private float time;
	private boolean flip;
	
	private ArrayList<Illumination> lightSources = new ArrayList<Illumination>();

	private static ShadowMap shadowMap;
	
	public static void init() {
		shadowMap = new ShadowMap();
	}
	
	public static ShadowMap get() {
		if(shadowMap == null) {
			init();
		}
		return shadowMap;
	}
	
	public void drawShadows(List<Entity> entities) {
		for(Entity e : entities) {
			if(e.getBody().m_type == BodyType.DYNAMIC && e.getRender() != null) {
				e.getRender().shadow();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void changeSizes() {
		time = MathFunctions.clamp(time + Theater.getDeltaSpeed(0.025f), 0, 6.5f);
		for(int x = 0; x<sizes.length; x++) {
			sizes[x] = MathFunctions.easeIn(time, startSizes[x], flip ? -5 : 5, 6.5f);
		}
		if(time >= 6.5f) {
			time = 0;
			flip = !flip;
			for(int x = 0; x<sizes.length; x++) {
				startSizes[x] = sizes[x];
			}
		}
	}
	
	public void drawIllumination() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear stencil buffer (0 by default)
		GL11.glColorMask(false, false, false, false);
		
		for(Illumination i : lightSources) {
			Entity illumSource = i.illumSource;
			Point illumOffset = i.illumOffset;
			
			if(i.resizeDuration != 0) {
				i.resize();
			}
			i.flicker();
			
			// Stencil in light circle edge
			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
			
			// Draw Illumination flicker
			DrawUtils.setTransform(0, 0, 0, 0, 
					illumSource.getRender().isFlipped() && !illumSource.isFixDirection() ? 1 : 0, 
					illumSource.getRender().getTransform().getRotation(),
					0, 0, 0);
			DrawUtils.drawShadowFan(
					(illumSource.getBody().getPosition().x * 30f) + illumOffset.x,
					(illumSource.getBody().getPosition().y * 30f) + illumOffset.y,
					i.shadowWidth + (i.glow * i.haloRatio), i.shadowHeight + (i.glow * i.haloRatio), (int) (Math.random() * 3) + 8);
		}
		
		for(Illumination i : lightSources) {
			Entity illumSource = i.illumSource;
			Point illumOffset = i.illumOffset;
			
			// Stencil in interior light circles
			GL11.glStencilFunc(GL11.GL_ALWAYS, 2, 0xFF); // Pass test if stencil value is 1
			GL11.glStencilOp(0, 0, GL11.GL_REPLACE);
			
			// Draw illumination circle
			DrawUtils.setTransform(0, 0, 0, 0,
					illumSource.getRender().isFlipped() && !illumSource.isFixDirection() ? 1 : 0, 
					illumSource.getRender().getTransform().getRotation(),
					0, 0, 0);
			DrawUtils.drawShadowFan(
					(illumSource.getBody().getPosition().x * 30f) + illumOffset.x,
					(illumSource.getBody().getPosition().y * 30f) + illumOffset.y,
					(i.shadowWidth + (i.glow * 1.5f)) * i.haloRatio, (i.shadowHeight + (i.glow * 1.5f)) * i.haloRatio,
					(int) (Math.random() * 3) + 8);
		}
		
		GL11.glColorMask(true, true, true, true);
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		
		// Fill light edges
		DrawUtils.fillColor(0, 0, 0, 0.65f);
		
		GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF); // Pass test if stencil value is 0
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		
		// Fill in darkness
		DrawUtils.fillColor(0, 0, 0, 1f);
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public ArrayList<Illumination> getLightSources() {
		return lightSources;
	}
	
	public void addIllumination(Entity source, Point offset, float radius) {
		lightSources.add(new Illumination(source, offset, radius));
	}
	
	public class Illumination {
		
		public Entity illumSource;
		public Point illumOffset;
		
		private float shadowWidth, shadowHeight;
		private float haloRatio = 0.9175f;
		
		private float glowTime, glowLimit = 1f, glowStart, glow;
		private boolean glowing = true;
		
		private float resizeTime, resizeDuration, resizeStart, resizeChange;
		
		Illumination(Entity source, Point offset, float radius) {
			illumSource = source;
			illumOffset = offset == null ? new Point(0, 0) : offset;
			shadowWidth = radius;
			shadowHeight = radius / 1.777f;
		}
		
		public void resize() {
			resizeTime = MathFunctions.clamp(resizeTime + Theater.getDeltaSpeed(0.025f), 0, resizeDuration);
			shadowWidth = MathFunctions.linearTween(resizeTime, resizeStart, resizeChange, resizeDuration);
			shadowHeight = shadowWidth / 1.77777f;
			if(resizeTime >= resizeDuration) {
				resizeDuration = 0;
			}
		}
		
		public void setResize(float resizeFactor) {
			resizeTime = 0f;
			resizeDuration = 1f;
			resizeStart = shadowWidth;
			resizeChange = shadowWidth * resizeFactor;
		}
		
		public void flicker() {
			glowTime = MathFunctions.clamp(glowTime + Theater.getDeltaSpeed(0.025f), 0, glowLimit);
			glow = MathFunctions.linearTween(glowTime, glowStart, glowing ? (glowLimit * 25) : -(glowLimit * 25), glowLimit);
			if(glowTime >= glowLimit) {
				glowStart = glow;
				glowTime = 0;
				if(!glowing) {
					int chance = (int) (Math.random() * 100);
					if(chance > 90) {
						glowLimit = (float) Math.random() + 0.25f;
					} else {
						glowLimit = (float) Math.random() * 0.35f;
					}
				}
				glowing = !glowing;
			}
		}
		
	}
	
}
