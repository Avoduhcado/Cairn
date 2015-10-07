package core.entities_new;

import java.io.Serializable;

import org.jbox2d.common.Vec2;
import core.render.Transform;

public interface Render extends Serializable {
	
	public void draw();
	public void debugDraw();
	
	public void animate(float speed, Vec2 position);
	public void setAnimation(String animation, boolean loop);
	public boolean isFlipped();
	public void setFlipped(boolean flipped);
	
	public Transform getTransform();
	public void setTransform(int index);
	
	public float getWidth();
	public float getHeight();
		
}
