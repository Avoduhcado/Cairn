package core.entities_new;

import org.jbox2d.common.Vec2;

public interface Controller {

	public void collectInput();
	public void resolveState();
	
	public void move(Vec2 direction);
	public void dodge();
	public void fall();
	public void collapse(Vec2 force);
	
}
