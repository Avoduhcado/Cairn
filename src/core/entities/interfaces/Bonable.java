package core.entities.interfaces;

import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;

import core.entities.Clutter;

public interface Bonable {

	public Skeleton getSkeleton();
	public AnimationState getAnimState();
	public AnimationStateData getAnimStateData();
	
	public void buildSkeleton();
	public void buildAnimationEvents();
	public void updateAnimations();
	
	public void collapse(World world, Set<Clutter> bodies, Vec2 force);
	public void setCollisionBox(Body box);
	public Body getCollisionBox();
	
}
