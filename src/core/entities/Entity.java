package core.entities;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.Input;
import core.Theater;
import core.render.DrawUtils;
import core.render.SpriteIndex;
import core.utilities.mouse.MouseInput;
import core.utilities.text.Text;

public abstract class Entity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static transient int count;
		
	protected Vector2f pos;
	protected String sprite;
	protected String name;
	protected Rectangle2D box;
	protected float scale;
	
	protected String ID;
	protected transient boolean debug;
	
	public Entity() {
		setID();
	}
	
	public abstract void update();
	
	public void toDraw() {
		if(Point2D.distance(getX(), getY(), Camera.get().frame.getCenterX(), Camera.get().frame.getCenterY()) < Camera.DRAW_DISTANCE) {
			draw();
		}
	}
	
	public void draw() {
		SpriteIndex.getSprite(sprite).draw(pos.x, pos.y);
		
		drawDebug();
	}
	
	public void draw(float x, float y) {
		SpriteIndex.getSprite(sprite).draw(x, y);
		
		drawDebug();
	}
	
	public void drawDebug() {
		if(Theater.get().debug || this.debug) {
			DrawUtils.setColor(new Vector3f(1f, 0, 0));
			DrawUtils.drawRect(pos.x, pos.y, getBox());
			Text.getDefault().drawString(getID(), pos.x, pos.y);
		}
	}
	
	public void updateBox() {
		this.box.setFrame(pos.x, pos.y, box.getWidth(), box.getHeight());
	}
	
	public Rectangle2D getBox() {
		return box;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Vector2f getPosition() {
		return pos;
	}
	
	public Point2D getPositionAsPoint() {
		return new Point2D.Double(pos.x, pos.y);
	}
	
	public void setPosition(float x, float y) {
		pos.set(x, y);
		updateBox();
	}
	
	public void movePosition(float x, float y) {
		pos.set(pos.x + x, pos.y + y);
		updateBox();
	}
	
	public float getX() {
		return pos.x;
	}
	
	public void setX(float x) {
		this.pos.setX(x);
	}
	
	public float getY() {
		return pos.y;
	}
	
	public void setY(float y) {
		this.pos.setY(y);
	}
	
	public float getYPlane() {
		return (float) box.getMaxY();
	}
	
	public float getScale() {
		return scale;
	}

	public boolean isClicked() {
		return box.contains(MouseInput.getMouse()) && Input.mouseClicked();
	}
	
	public boolean isHovering() {
		return box.contains(MouseInput.getMouse());
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public String getID() {
		return ID;
	}
	
	public abstract void setID();
	
	public void setID(String ID) {
		this.ID = ID;
	}
		
}
