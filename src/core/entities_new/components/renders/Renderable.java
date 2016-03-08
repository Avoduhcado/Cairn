package core.entities_new.components.renders;

import core.entities_new.event.EntityEvent;
import core.render.transform.Transform;

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
	public String getAnimation();
	public boolean hasAnimation(String animation);
	
	public void fireEvent(EntityEvent e);
		
}
