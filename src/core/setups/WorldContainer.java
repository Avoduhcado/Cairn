package core.setups;

import org.jbox2d.dynamics.World;

import core.entities_new.Entity;

public interface WorldContainer {

	public World getWorld();
	
	public void addEntity(Entity entity);
	public boolean removeEntity(Entity entity);
	
}
