package core.entities;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.lwjgl.util.vector.Vector2f;

import core.Input;
import core.render.SpriteIndex;
import core.utilities.mouse.MouseInput;

public abstract class Entity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Vector2f pos;
	protected String sprite;
	protected String name;
	protected Rectangle2D box;
	protected float scale;
	
	protected String ID;
	
	public Entity() {
		setID();
	}
	
	public abstract void update();
	
	public void draw() {
		SpriteIndex.getSprite(sprite).draw(pos.x, pos.y);
	}
	
	public void draw(float x, float y) {
		SpriteIndex.getSprite(sprite).draw(x, y);
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
	
	public void setPosition(float x, float y) {
		pos.set(x, y);
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
	
	public String getID() {
		return ID;
	}
	
	public abstract void setID();
	
}
