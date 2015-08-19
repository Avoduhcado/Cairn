package core.entities.interfaces;

import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import core.entities.Clutter;

public interface Bonable {

	public void collapse(World world, Set<Clutter> bodies, Vec2 force);
	public void setCollisionBox(World world);
	
}
