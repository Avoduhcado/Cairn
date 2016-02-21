package core.setups;

import java.util.ArrayList;

import org.jbox2d.dynamics.World;

import core.entities_new.Entity;
import core.entities_new.EntityData;

public interface WorldContainer {

	public World getWorld();
	
	public ArrayList<Entity> getEntities();

	public void queueEntity(Entity entity, boolean add);
	
}
