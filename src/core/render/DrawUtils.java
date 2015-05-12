package core.render;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;

public class DrawUtils {
	
	/** Color of object to be drawn */
	private static Vector3f color = new Vector3f(0f, 0f, 0f);

	/**
	 * Set new drawing color.
	 * 
	 * @param color to draw with
	 */
	public static void setColor(Vector3f color) {
		DrawUtils.color = color;
	}
	
	/**
	 * Draw a line to the screen.
	 * 
	 * @param x1 of line
	 * @param y1 of line
	 * @param x2 of line
	 * @param y2 of line
	 */
	public static void drawLine(Line2D line) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslated(line.getX1() - Camera.get().frame.getX(), line.getY1() - Camera.get().frame.getY(), 0);
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glLineWidth(1.0f);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(line.getX2() - line.getX1(), line.getY2() - line.getY1());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	/**
	 * Draw a rectangle to the screen.
	 * 
	 * @param x of rectangle
	 * @param y of rectangle
	 * @param rect to be drawn
	 */
	public static void drawRect(float x, float y, Rectangle2D rect) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef((int) (x - Camera.get().frame.getX()), (int) (y - Camera.get().frame.getY()), 0);
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glLineWidth(1.0f);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(rect.getWidth(), 0);
			GL11.glVertex2d(rect.getWidth(), rect.getHeight());
			GL11.glVertex2d(0, rect.getHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	public static void drawPoly(float x, float y, Polygon poly) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef((int) (x - Camera.get().frame.getX()), (int) (y - Camera.get().frame.getY()), 0);
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glLineWidth(1.0f);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for(int n = 0; n<poly.npoints; n++) {
				GL11.glVertex2d(poly.xpoints[n], poly.ypoints[n]);
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	public static void drawShape(float x, float y, PathIterator path) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef((int) (x - Camera.get().frame.getX()), (int) (y - Camera.get().frame.getY()), 0);
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glLineWidth(1.0f);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			double[] coords = new double[6];
			while(!path.isDone()) {
				path.currentSegment(coords);
				GL11.glVertex2d(coords[0], coords[1]);
				path.next();
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	public static void drawGradient(float r, float g, float b, Rectangle2D rect, boolean fadeUp) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslated(rect.getX(), rect.getY(), 0f);
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glColor4f(r, g, b, fadeUp ? 0f : 1f);
			GL11.glVertex2d(0, 0);
			GL11.glColor4f(r, g, b, fadeUp ? 0f : 1f);
			GL11.glVertex2d(rect.getWidth(), 0);
			GL11.glColor4f(r, g, b, fadeUp ? 1f : 0f);
			GL11.glVertex2d(rect.getWidth(), rect.getHeight());
			GL11.glColor4f(r, g, b, fadeUp ? 1f : 0f);
			GL11.glVertex2d(0, rect.getHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	/**
	 * Draws a single color over a specific rectangle.
	 * 
	 * @param r Red value of color
	 * @param g Green value of color
	 * @param b Blue value of color
	 * @param a Transparency
	 * @param rect The rectangle to be drawn
	 */
	public static void fillRect(float r, float g, float b, float a, Rectangle2D rect) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glColor4f(r, g, b, a);
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(rect.getX(), rect.getY());
			GL11.glVertex2d(rect.getMaxX(), rect.getY());
			GL11.glVertex2d(rect.getMaxX(), rect.getMaxY());
			GL11.glVertex2d(rect.getX(), rect.getMaxY());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Draws a single color over the entire screen.
	 * 
	 * @param r Red value of color
	 * @param g Green value of color
	 * @param b Blue value of color
	 * @param a Transparency
	 */
	public static void fillColor(float r, float g, float b, float a) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glColor4f(r, g, b, a);
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(Camera.get().getDisplayWidth(1f), 0);
			GL11.glVertex2d(Camera.get().getDisplayWidth(1f), Camera.get().getDisplayHeight(1f));
			GL11.glVertex2d(0, Camera.get().getDisplayHeight(1f));
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
}
