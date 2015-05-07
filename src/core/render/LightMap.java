package core.render;

import java.util.LinkedList;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities.LightSource;

public class LightMap {

	public static LinkedList<LightSource> lights = new LinkedList<LightSource>();
	public static Vector4f background = new Vector4f(0f, 0f, 0f, 1f);
	private static int fboID;
	private static int textureID;

	public static void init() {
		fboID = glGenFramebuffersEXT();                                        // create a new framebuffer
		textureID = glGenTextures();
		int stencilBufferID = glGenRenderbuffersEXT();  

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboID);

		glBindTexture(GL_TEXTURE_2D, textureID);                                   // Bind the colorbuffer texture
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);               // make it linear filterd
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, (int) Camera.get().getDisplayWidth(1f),
				(int) Camera.get().getDisplayHeight(1f), 0, GL11.GL_RGB, GL_INT, (java.nio.ByteBuffer) null);  // Create the texture data
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, textureID, 0); // attach it to the framebuffer
		
		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, stencilBufferID);
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_STENCIL_INDEX8_EXT, (int) Camera.get().getDisplayWidth(1f),
				(int) Camera.get().getDisplayHeight(1f));
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, stencilBufferID);
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_STENCIL_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, stencilBufferID);
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
	public static void draw() {
		// TODO Fix this fucking mess
		//render to fbo
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboID);
		glClear(GL_COLOR_BUFFER_BIT);
		glPushAttrib(GL_VIEWPORT_BIT);
		glViewport(0, 0, (int) Camera.get().getDisplayWidth(1f), (int) Camera.get().getDisplayHeight(1f));

		glDisable(GL_TEXTURE_2D);
		
		glEnable(GL_STENCIL_TEST);
		glColorMask(false, false, false, false);
		glClear(GL_STENCIL_BUFFER_BIT); // Clear stencil buffer (0 by default)

		glStencilFunc(GL_ALWAYS, 1, 1); // Set any stencil to 1
		glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);

		// Cut holes for light source
		for(LightSource l : lights) {
			l.drawLight(background);
		}
		
		glColorMask(true, true, true, true);
		glStencilFunc(GL_NOTEQUAL, 1, 1); // Pass test if stencil value is 1
		glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP);
		
		// THIS WORKSSS!!!!!!!!
		glPushMatrix();
		glColor4f(0f, 0f, 0f, background.w);
		glBegin(GL_QUADS);
		{
			glVertex2d(0, 0);
			glVertex2d(Camera.get().getDisplayWidth(1f), 0);
			glVertex2d(Camera.get().getDisplayWidth(1f), Camera.get().getDisplayHeight(1f));
			glVertex2d(0, Camera.get().getDisplayHeight(1f));
		}
		glEnd();
		glPopMatrix();
		
		glDisable(GL_STENCIL_TEST);

		/* old
		//glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ZERO);
		GL14.glBlendEquation(GL14.GL_MAX);
		*/

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_SRC_ALPHA);
		GL14.glBlendEquation(GL14.GL_MIN);
		
		for(LightSource l : lights) {
			l.drawLight(background);
		}
		
		glEnable(GL_TEXTURE_2D);
		glPopAttrib();
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		
		//render the texture
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		//GL14.glBlendFuncSeparate(GL_ONE, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
		
		glPushMatrix();
		glColor4f(1f, 1f, 1f, 1f);
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0f, 1f);
			glVertex2f(0, 0);
			glTexCoord2f(1f, 1f);
			glVertex2f(Camera.get().getDisplayWidth(1f), 0);
			glTexCoord2f(1f, 0f);
			glVertex2f(Camera.get().getDisplayWidth(1f), Camera.get().getDisplayHeight(1f));
			glTexCoord2f(0f, 0f);
			glVertex2f(0, Camera.get().getDisplayHeight(1f));
		}
		glEnd();
		glPopMatrix();
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public static void drawFBOMax() {
		glDisable(GL_TEXTURE_2D);
		
		// Draw shadows with holes
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glColorMask(false, false, false, false);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear stencil buffer (0 by default)

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

		// Cut holes for light source
		for(LightSource l : lights) {
			l.drawLight(background);
		}

		GL11.glColorMask(true, true, true, true);
		GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 1); // Pass test if stencil value is 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

		// Draw a dark box over the entire screen
		GL11.glPushMatrix();
		GL11.glColor4f(background.x, background.y, background.z, 1f);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(Camera.get().getDisplayWidth(1f), 0);
			GL11.glVertex2d(Camera.get().getDisplayWidth(1f), Camera.get().getDisplayHeight(1f));
			GL11.glVertex2d(0, Camera.get().getDisplayHeight(1f));
		}
		GL11.glEnd();
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_STENCIL_TEST);
		glEnable(GL_TEXTURE_2D);
		
		//render to fbo
		{
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboID);
			glClearColor(0,0,0,0);
			glClear(GL_COLOR_BUFFER_BIT);
			glPushAttrib(GL_VIEWPORT_BIT);
			glViewport(0, 0, (int) Camera.get().getDisplayWidth(1f), (int) Camera.get().getDisplayHeight(1f));
	
			glDisable(GL_TEXTURE_2D);
			
			// Clear screen?
			// TODO Make this save the FBO with actual transparency
			glPushMatrix();
			glColor4f(0f, 0f, 0f, 0f);
			glBegin(GL_QUADS);
			{
				glVertex2d(0, 0);
				glVertex2d(Camera.get().getDisplayWidth(1f), 0);
				glVertex2d(Camera.get().getDisplayWidth(1f), Camera.get().getDisplayHeight(1f));
				glVertex2d(0, Camera.get().getDisplayHeight(1f));
			}
			glEnd();
			glPopMatrix();
			
			glEnable(GL_STENCIL_TEST);
			glColorMask(false, false, false, false);
			glClear(GL_STENCIL_BUFFER_BIT); // Clear stencil buffer (0 by default)
	
			glStencilFunc(GL_ALWAYS, 1, 1); // Set any stencil to 1
			glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
	
			// Cut holes for light source
			for(LightSource l : lights) {
				l.drawLight(background);
			}
			
			glColorMask(true, true, true, true);
			glStencilFunc(GL_EQUAL, 1, 1); // Pass test if stencil value is 1
			glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
			
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ZERO);
			GL14.glBlendEquation(GL14.GL_MAX);
			GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE);
			
			// THIS WORKSSS!!!!!!!!
			glPushMatrix();
			glColor4f(0f, 0f, 0f, 1f);
			glBegin(GL_QUADS);
			{
				glVertex2d(0, 0);
				glVertex2d(Camera.get().getDisplayWidth(1f), 0);
				glVertex2d(Camera.get().getDisplayWidth(1f), Camera.get().getDisplayHeight(1f));
				glVertex2d(0, Camera.get().getDisplayHeight(1f));
			}
			glEnd();
			glPopMatrix();
			
			glDisable(GL_STENCIL_TEST);
			
			for(LightSource l : lights) {
				l.drawLight(background);
			}
			
			glEnable(GL_TEXTURE_2D);
			glPopAttrib();
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		}
		
		//render the texture
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		GL14.glBlendFuncSeparate(GL_ONE, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
		
		glPushMatrix();
		glColor4f(1f, 1f, 1f, 0.9f);
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0f, 1f);
			glVertex2f(0, 0);
			glTexCoord2f(1f, 1f);
			glVertex2f(Camera.get().getDisplayWidth(1f), 0);
			glTexCoord2f(1f, 0f);
			glVertex2f(Camera.get().getDisplayWidth(1f), Camera.get().getDisplayHeight(1f));
			glTexCoord2f(0f, 0f);
			glVertex2f(0, Camera.get().getDisplayHeight(1f));
		}
		glEnd();
		glPopMatrix();
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public static void drawNoFBO() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glColorMask(false, false, false, false);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear stencil buffer (0 by default)

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

		// Cut holes for light source
		for(LightSource l : lights) {
			l.drawLight(background);
		}

		//GL11.glDisable(GL11.GL_STENCIL_TEST);

		GL11.glColorMask(true, true, true, true);
		GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 1); // Pass test if stencil value is 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

		// Draw a dark box over the entire screen
		GL11.glPushMatrix();
		GL11.glColor4f(background.x, background.y, background.z, background.w);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(Camera.get().getDisplayWidth(1f), 0);
			GL11.glVertex2d(Camera.get().getDisplayWidth(1f), Camera.get().getDisplayHeight(1f));
			GL11.glVertex2d(0, Camera.get().getDisplayHeight(1f));
		}
		GL11.glEnd();
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_STENCIL_TEST);

		GL11.glEnable(GL11.GL_BLEND);
		// KEEP THESE
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_DST_ALPHA);
		// Save for neat effect
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA_SATURATE, GL11.GL_DST_COLOR);

		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		//GL14.glBlendEquation(GL14.GL_FUNC_ADD);

		// Draw light source
		for(LightSource l : lights) {
			l.drawLight(background);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
