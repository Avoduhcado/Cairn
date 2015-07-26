package core.interactions;

import core.entities.Entity;

public interface InteractionListener {

	public void keyPress(Script Script);
	public void playerCollide(Script Script);
	public void entityCollide(Script Script, Entity entity);
	public void autorun(Script Script);
	
}
