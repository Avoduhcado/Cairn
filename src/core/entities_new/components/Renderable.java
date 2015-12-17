package core.entities_new.components;

import core.entities_new.event.EntityEvent;
import core.render.Transform;

public interface Renderable {
	
	public void draw();
	public void drawShadow();
	public void debugDraw();
	
	public void animate(float delta);
	public boolean isFlipped();
	public void setFlipped(boolean flipped);
	
	public Transform getTransform();
	public void setTransform(int index);
	public String getSprite();
	
	public void fireEvent(EntityEvent e);
		
}
