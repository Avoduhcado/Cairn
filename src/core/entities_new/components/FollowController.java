package core.entities_new.components;

import java.awt.Point;

import org.jbox2d.common.Vec2;
import core.entities_new.State;
import core.entities_new.Entity;
import core.entities_new.event.ControllerEvent;
import core.entities_new.event.StateChangeEvent;
import core.entities_new.utils.BodyData;
import core.entities_new.utils.BodyLoader;

// TODO Make a custom Dad skull controller
public class FollowController extends EntityController {

	private Entity leader;
		
	private float lagDistance = 50;
	private float xOffset, yOffset;
	
	private float closeInSpeedMod = 0.25f;
	
	private boolean queueLock = false;
	
	public FollowController(Entity follower, Entity leader) {
		super(follower);
		
		setLeader(leader);
	}

	@Override
	protected void controlMovement() {
		speedMod = 1f;
		float distance = (float) Point.distance(leader.getZBody().getX() + xOffset, leader.getZBody().getY() + yOffset,
				entity.getZBody().getX(), entity.getZBody().getY());

		if(needsToCatchUp(distance) || facingWrongDirection()) {
			if(distance > lagDistance * 1.5f) {
				speedMod = (float) Math.sqrt(distance / lagDistance);
			} else if(shouldCloseIn(distance)) {
				speedMod = closeInSpeedMod;
			}
			
			if(queueLock) {
				speedMod *= 2f;
			}

			move(new ControllerEvent(ControllerEvent.MOVE) {{
				setData(new Vec2(((leader.getZBody().getX() + xOffset) - entity.getZBody().getX()) / distance,
						((leader.getZBody().getY() + yOffset) - entity.getZBody().getY()) / distance));
			}});

			if(facingWrongDirection()) {
				entity.getRender().setFlipped(leader.getRender().isFlipped());
			}
		}
	}

	@Override
	protected void controlActions() {
	}
	
	private boolean needsToCatchUp(float distance) {
		return distance > lagDistance || shouldCloseIn(distance);
	}
	
	private boolean shouldCloseIn(float distance) {
		return leader.getBody().getLinearVelocity().length() <= 0.25f && distance > lagDistance / 4;
	}

	private boolean facingWrongDirection() {
		return leader.getBody().getLinearVelocity().length() <= 0.1f 
				&& leader.getRender().isFlipped() != entity.getRender().isFlipped()
				&& !entity.isFixDirection();
	}
	
	@Override
	public void attack(ControllerEvent e) {
		entity.getRender().setFlipped(leader.getRender().isFlipped());
		
		State.ATTACK.setCustomAnimation((String) e.getData());
		entity.fireEvent(new StateChangeEvent(State.ATTACK));
		
		Entity rightArm = new Entity("Right Arm", new BodyData(entity.getZBody().getX(), entity.getZBody().getY(), BodyLoader.FLOATING_ENTITY),
				entity.getContainer());
		rightArm.setController(new OneOffController(rightArm, e));
		rightArm.getRender().setFlipped(entity.getRender().isFlipped());

		entity.getContainer().queueEntity(rightArm, true);
	}

	@Override
	public void defend(ControllerEvent e) {
		super.defend(e);
	}

	@Override
	public void dodge(ControllerEvent e) {
		entity.getRender().setFlipped(leader.getRender().isFlipped());
		super.dodge(e);
	}
	
	@Override
	public void processEventQueue() {
		if(eventQueue != null && !entity.getState().isActing()) {
			float distance = (float) Point.distance(leader.getZBody().getX() + xOffset, leader.getZBody().getY() + yOffset,
					entity.getZBody().getX(), entity.getZBody().getY());
			if(distance > lagDistance && entity.getState().equals(State.DEFEND)) {
				queueLock = true;
			} else {
				queueLock = false;
			}
			
			if(!queueLock) {
				fireEvent(eventQueue);
				
				// Notify any followers of your actions, oh Lord
				followers.stream().forEach(e -> e.setEventQueue(eventQueue));
				
				eventQueue = null;
			}
		}
	}
	
	public Entity getFollower() {
		return entity;
	}
	
	public Entity getLeader() {
		return leader;
	}
	
	public void setLeader(Entity leader) {
		this.leader = leader;
		
		if(leader.controller() && leader.getController() instanceof EntityController) {
			((EntityController) leader.getController()).addFollower(this);
		}
	}
	
	public float getLagDistance() {
		return lagDistance;
	}

	public void setLagDistance(float lagDistance) {
		this.lagDistance = lagDistance;
	}

	public void setOffset(float xOffset, float yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

}
