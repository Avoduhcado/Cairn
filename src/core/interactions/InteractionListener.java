package core.interactions;

import core.entities.Entity;

public interface InteractionListener {

	public void keyPress();
	public void playerCollide();
	public void entityCollide(Entity entity);
	public void autorun();
	
}
