package core.entities_new.components;

import org.jbox2d.common.Vec2;

public interface Controllable {

	public void collectInput();
	public void resolveState();
	
	public void move(Vec2 direction);
	public void dodge();
	public void collapse(Vec2 force);
	public void attack();
	public void defend();
	
}
