package core.render;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;

public class DrawUtils {
	
	/** Color of object to be drawn */
	private static Vector3f color = new Vector3f(0f, 0f, 0f);
	private static boolean scaled;
	private static boolean still;
	
	private static Matrix3f transform = new Matrix3f();

	/**
	 * Set new drawing color.
	 * 
	 * @param color to draw with
	 */
	public static void setColor(Vector3f color) {
		DrawUtils.color = color;
	}
	
	public static void applyCameraScale() {
		GL11.glTranslated(Camera.get().frame.getWidth() / 2f, Camera.get().frame.getHeight() / 2f, 0);
		GL11.glScalef(Camera.get().getScale(), Camera.get().getScale(), 1f);
		GL11.glTranslated(-Camera.get().frame.getWidth() / 2f, -Camera.get().frame.getHeight() / 2f, 0);
		scaled = true;
	}
	
	private static void reset() {
		color.set(0f, 0f, 0f);
		if(scaled) {
			GL11.glLoadIdentity();
			scaled = false;
		}
		still = false;
		transform.setZero();
	}
	
	public static void drawVerts(float[] vertices) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		//GL11.glTranslated(line.getX1() - Camera.get().frame.getX(), line.getY1() - Camera.get().frame.getY(), 0);
		GL11.glTranslated(vertices[0] - Camera.get().frame.getX(), vertices[1] - Camera.get().frame.getY(), 0);
		GL11.glColor3f(color.x, color.y, color.z);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for(int n = 0; n<vertices.length; n+=5) {
				GL11.glVertex2f(vertices[n], vertices[n+1]);
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		reset();
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
		//GL11.glTranslated(line.getX1() - Camera.get().frame.getX(), line.getY1() - Camera.get().frame.getY(), 0);
		GL11.glTranslated(line.getX1(), line.getY1(), 0);
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
		reset();
	}
	
	public static void drawLine(float x, float y, Line2D line) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslated(x - Camera.get().frame.getX(), y - Camera.get().frame.getY(), 0);
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
		reset();
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
		if(still) {
			GL11.glTranslatef(x, y, 0);
		} else {
			GL11.glTranslatef((int) (x - Camera.get().frame.getX()), (int) (y - Camera.get().frame.getY()), 0);
		}
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
		reset();
	}
	
	public static void drawPoly(float x, float y, Polygon poly) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		
		/*if(Float.isNaN(x)) {
			GL11.glTranslated(Camera.get().frame.getWidth() / 2f, Camera.get().frame.getHeight() / 2f, 0);
			GL11.glScalef(Camera.get().getScale(), Camera.get().getScale(), 1f);
			GL11.glTranslated(-Camera.get().frame.getWidth() / 2f, -Camera.get().frame.getHeight() / 2f, 0);
			GL11.glTranslatef((int) (0 - Camera.get().frame.getX()), (int) (y - Camera.get().frame.getY()), 0);
		} else {*/
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
		//color.set(0f, 0f, 0f);
		reset();
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
		reset();
	}
	
	// TODO Add parameters for gradiented edges
	public static void drawShadowFan(float x, float y, float width, float height, int precision) {
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) (x - Camera.get().frame.getX()), (float) (y - Camera.get().frame.getY()), 0f);
		GL11.glRotatef(transform.m12, 0, 0, 1);
		GL11.glColor4f(color.x, color.y, color.z, 1f);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		{
			GL11.glVertex2f(0, 0);
			//GL11.glColor4f(0f, 0f, 0f, 0f);
			for(int i = 0; i<=360; i += precision) {
				GL11.glVertex2f((float) (Math.sin(Math.toRadians(i)) * width),
						(float) (Math.cos(Math.toRadians(i)) * height));
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		reset();
		
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
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
		reset();
	}
	
	public static void drawBox2DPoly(Body body, PolygonShape poly) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		if(body != null) {
			GL11.glTranslatef((int) ((body.getPosition().x * 30f) - Camera.get().frame.getX()), 
					(int) ((body.getPosition().y * 30f) - Camera.get().frame.getY()), 0);
			GL11.glRotated(body.getAngle(), 0, 0, 1f);
		} else {
			GL11.glTranslatef((int) (transform.m00 - Camera.get().frame.getX()), 
					(int) (transform.m01 - Camera.get().frame.getY()), 0);
			GL11.glRotated(transform.m12, 0, 0, 1f);
			if(transform.m11 == 1) {
				GL11.glRotated(180, 0, 1, 0);
			}
			GL11.glScalef(transform.m20, transform.m21, transform.m22);
		}
		GL11.glColor4f(color.x, color.y, color.z, 0.5f);
		GL11.glBegin(GL11.GL_QUADS);
		{
			for(int n = 0; n<poly.m_count; n++) {
				GL11.glVertex2f(poly.m_vertices[n].x * 30f, poly.m_vertices[n].y * 30f);
			}
		}
		GL11.glEnd();
		
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for(int n = 0; n<poly.m_count; n++) {
				GL11.glVertex2f(poly.m_vertices[n].x * 30f, poly.m_vertices[n].y * 30f);
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		reset();
	}
	
	public static void drawBox2DEdge(Vec2 position, EdgeShape edge) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef((int) ((position.x * 30f) - Camera.get().frame.getX()), 
				(int) ((position.y * 30f) - Camera.get().frame.getY()), 0);
		GL11.glColor3f(color.x, color.y, color.z);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2f(edge.m_vertex1.x * 30f, edge.m_vertex1.y * 30f);
			GL11.glVertex2f(edge.m_vertex2.x * 30f, edge.m_vertex2.y * 30f);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		reset();
	}
	
	public static void drawBox2DCircle(Vec2 position, CircleShape circle) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef((int) ((position.x * 30f) - Camera.get().frame.getX()), 
				(int) ((position.y * 30f) - Camera.get().frame.getY()), 0);
		GL11.glColor4f(color.x, color.y, color.z, 0.5f);
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		{
			GL11.glVertex2f(0, 0);
			for(int i = 0; i<=360; i += 10) {
				GL11.glVertex2f((float) (Math.sin(Math.toRadians(i)) * (circle.m_radius * 30f)),
						(float) Math.cos(Math.toRadians(i)) * (circle.m_radius * 30f));
			}
		}
		GL11.glEnd();
		
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for(int i = 0; i<=360; i += 10) {
				GL11.glVertex2f((float) (Math.sin(Math.toRadians(i)) * (circle.m_radius * 30f)),
						(float) Math.cos(Math.toRadians(i)) * (circle.m_radius * 30f));
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		reset();
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

	public static void setStill(boolean still) {
		DrawUtils.still = still;
	}
	
	public static void setTransform(Matrix3f transform) {
		DrawUtils.transform = transform;
	}
	
	public static void setTransform(float x, float y, float z, float xR, float yR, float zR, float xS, float yS, float zS) {
		DrawUtils.transform.m00 = x;
		DrawUtils.transform.m01 = y;
		DrawUtils.transform.m02 = z;
		DrawUtils.transform.m10 = xR;
		DrawUtils.transform.m11 = yR;
		DrawUtils.transform.m12 = zR;
		DrawUtils.transform.m20 = xS;
		DrawUtils.transform.m21 = yS;
		DrawUtils.transform.m22 = zS;
	}
	
}
