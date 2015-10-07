package core.scene;

import java.util.List;

import org.jbox2d.dynamics.BodyType;
import org.lwjgl.opengl.GL11;

import core.Camera;
import core.Theater;
import core.entities.Actor;
import core.entities_new.Entity;
import core.render.DrawUtils;
import core.utilities.MathFunctions;

public class ShadowMap {
	
	private static float[] startSizes = {2f, 5f, 12f, 20f};
	private static float[] sizes = {2f, 5f, 12f, 20f};
	private static float time;
	private static boolean flip;
	
	public static void drawShadows(List<Entity> entities) {
		changeSizes();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glColorMask(false, false, false, false);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear stencil buffer (0 by default)
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		
		for(Entity e : entities) {
			//DrawUtils.drawShadowFan((float) a.getBox().getCenterX(), a.getYPlane(),
				//	(float) (a.getBox().getWidth() * Camera.ASPECT_RATIO), 5.5f);
			if(e.getBody().m_type == BodyType.DYNAMIC) {
				DrawUtils.drawShadowFan(e.getBody().getPosition().x * 30f, e.getBody().getPosition().y * 30f,
							e.getWidth() * 0.6f, 5.5f);
			}
		}
		
		GL11.glColorMask(true, true, true, true);
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1); // Pass test if stencil value is 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		
		//GL11.glEnable(GL11.GL_LINE_STIPPLE);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		
		GL11.glPushMatrix();
		GL11.glColor3f(0f, 0f, 0f);
		//GL11.glLineStipple(20, (short) 0x3F07);
		GL11.glLineWidth(3.5f);
		GL11.glBegin(GL11.GL_LINES);
		{
			//for(int x = -Camera.get().displayWidth / 2; x<Camera.get().displayWidth; x+= 28) {
			for(int x = -Camera.get().displayHeight / 2; x<Camera.get().displayHeight; x+= 28) {
				//GL11.glVertex2d(x + (sizes[0] * 2), 0);
				GL11.glVertex2d(0, x + (sizes[0] * 2));
				//GL11.glVertex2d(x + (Camera.get().displayWidth * 0.55f), Camera.get().displayHeight);
				GL11.glVertex2d(Camera.get().displayWidth, x + (Camera.get().displayHeight * 0.05f));
			}
		}
		GL11.glEnd();
		
		//GL11.glLineStipple(15, (short) 0xAAF7);
		GL11.glLineWidth(2f);
		GL11.glBegin(GL11.GL_LINES);
		{
			for(int x = -Camera.get().displayHeight / 2; x<Camera.get().displayHeight; x+= 14) {
				//GL11.glVertex2d(x, 0 - (sizes[1] * 2));
				//GL11.glVertex2d(x + (Camera.get().displayWidth * 0.55f), Camera.get().displayHeight);
				GL11.glVertex2d(0, x - (sizes[1] * 2));
				GL11.glVertex2d(Camera.get().displayWidth, x + (Camera.get().displayHeight * 0.05f));
			}
		}
		GL11.glEnd();
		
		GL11.glLineWidth(5f);
		GL11.glBegin(GL11.GL_LINES);
		{
			for(int x = -Camera.get().displayHeight / 2; x<Camera.get().displayHeight; x+= 56) {
				//GL11.glVertex2d(x - (sizes[2] * 3), 0);
				//GL11.glVertex2d(x + (Camera.get().displayWidth * 0.55f), Camera.get().displayHeight);
				GL11.glVertex2d(0, x - (sizes[2] * 3));
				GL11.glVertex2d(Camera.get().displayWidth, x + (Camera.get().displayHeight * 0.05f));
			}
		}
		GL11.glEnd();
		
		GL11.glLineWidth(1.2f);
		GL11.glBegin(GL11.GL_LINES);
		{
			for(int x = -Camera.get().displayHeight / 2; x<Camera.get().displayHeight; x+= 19) {
				//GL11.glVertex2d(x + (sizes[3] * 3), 0);
				//GL11.glVertex2d(x + (Camera.get().displayWidth * 0.55f), Camera.get().displayHeight);
				GL11.glVertex2d(0, x + (sizes[3] * 3));
				GL11.glVertex2d(Camera.get().displayWidth, x + (Camera.get().displayHeight * 0.05f));
			}
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	private static void changeSizes() {
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
	
}
