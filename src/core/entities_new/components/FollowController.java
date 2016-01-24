package core.entities_new.components;

import java.awt.Point;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import core.entities_new.State;
import core.entities_new.Entity;
import core.entities_new.event.ActionEvent;
import core.entities_new.event.ActionEventListener;
import core.entities_new.event.ControllerEvent;
import core.entities_new.event.StateChangeEvent;
import core.entities_new.utils.BodyData;
import core.entities_new.utils.BodyLoader;
import core.setups.Stage_new;

public class FollowController extends EntityController {

	private Entity leader;
	private Entity follower;
		
	private float lagDistance = 50;
	private float xOffset, yOffset;
	
	private float speed = 20f;
	private float speedMod = 1f;
	
	private ActionEvent actionQueue;
	
	private ActionEventListener actionEventListener;
	
	public FollowController(Entity follower, Entity leader) {
		super(follower);
		this.follower = follower;
		this.leader = leader;
		
		addActionEventListener(e -> {
			switch(e.getState()) {
			case ATTACK:
				actionQueue = attack(e);
				//this.follower.changeStateForced(State.ATTACK);
				break;
			case DEFEND:
				actionQueue = defend(e);
				//this.follower.changeStateForced(State.DEFEND);
				break;
			case QUICKSTEP:
				//this.follower.changeStateForced(State.QUICKSTEP);
				//this.follower.getBody().setLinearDamping(5f);
				//this.follower.getBody().applyLinearImpulse(leader.getBody().getLinearVelocity().mul(0.75f), follower.getBody().getWorldCenter());
				//this.follower.setFixDirection(true);
				break;
			default:
				break;
			}
		});
	}
	
	@Override
	public void control() {
		//if(follower.getState().canMove()) {
			speedMod = 1f;
			Body leadBody = leader.getBody();
			Body followBody = follower.getBody();
			double distance = Point.distance((leadBody.getPosition().x * Stage_new.SCALE_FACTOR) + xOffset, (leadBody.getPosition().y * Stage_new.SCALE_FACTOR) + yOffset,
					followBody.getPosition().x * Stage_new.SCALE_FACTOR, followBody.getPosition().y * Stage_new.SCALE_FACTOR);
			
			/*if(distance > lagDistance) {
				if(distance > lagDistance * 1.5f) {
					speedMod = 1.5f;
				}
				move(new Vec2((((leadBody.getPosition().x * Stage_new.SCALE_FACTOR) + xOffset) 
						- followBody.getPosition().x * Stage_new.SCALE_FACTOR) / (float) distance,
						(((leadBody.getPosition().y * Stage_new.SCALE_FACTOR) + yOffset) 
								- followBody.getPosition().y * Stage_new.SCALE_FACTOR) / (float) distance));
			} else if(leadBody.getLinearVelocity().length() <= 0.25f && distance > lagDistance / 4f) {
				speedMod = 0.35f;
				move(new Vec2((((leadBody.getPosition().x * Stage_new.SCALE_FACTOR) + xOffset) 
						- followBody.getPosition().x * Stage_new.SCALE_FACTOR) / (float) distance,
						(((leadBody.getPosition().y * Stage_new.SCALE_FACTOR) + yOffset) 
								- followBody.getPosition().y * Stage_new.SCALE_FACTOR) / (float) distance));
			}
			
			if(leadBody.getLinearVelocity().length() == 0f && (leader.getRender().isFlipped() != follower.getRender().isFlipped())) {
				move(new Vec2((((leadBody.getPosition().x * Stage_new.SCALE_FACTOR) + xOffset) 
						- followBody.getPosition().x * Stage_new.SCALE_FACTOR) / (float) distance,
						(((leadBody.getPosition().y * Stage_new.SCALE_FACTOR) + yOffset) 
								- followBody.getPosition().y * Stage_new.SCALE_FACTOR) / (float) distance));
				follower.getRender().setFlipped(leader.getRender().isFlipped());
			}*/
		//}
		
		if(actionQueue != null && !follower.getState().isActing()) {
			actionQueue.act();
			actionQueue = null;
		}
	}

	@Override
	public void move(ControllerEvent e) {
		Vec2 movement = (Vec2) e.getData();
		
		if(!follower.getBody().isFixedRotation()) {
			follower.getBody().applyTorque(90 * (movement.x < 0 ? -1f : 1f));
		}
		follower.getBody().applyForceToCenter(movement.mul(speed * speedMod));
		if(follower.getState().canMove()) {
			follower.fireEvent(new StateChangeEvent(speedMod > 1 ? State.RUN : State.WALK));
		}
		
		if(follower.getBody().getLinearVelocity().x != 0 && !follower.isFixDirection() && follower.render()) {
			follower.getRender().setFlipped(follower.getBody().getLinearVelocity().x < 0);
		}
	}
	
	private ActionEvent attack(ActionEvent action) {
		ActionEvent ae = new ActionEvent(action.getState(), action.getPrevState()) {
			private String currentAnimation = follower.getRender().getAnimation();
			
			@Override
			public void act() {
				follower.getRender().setFlipped(leader.getRender().isFlipped());
				
				State.ATTACK.setCustomAnimation(this.getString());
				follower.fireEvent(new StateChangeEvent(State.ATTACK));
				
				Entity rightArm = new Entity("Right Arm", 
						new BodyData((follower.getBody().getPosition().x * Stage_new.SCALE_FACTOR),
						(follower.getBody().getPosition().y * Stage_new.SCALE_FACTOR),
						BodyLoader.FLOATING_ENTITY), follower.getContainer());
								
				rightArm.getZBody().setWalkThrough(true);
				rightArm.getRender().setFlipped(follower.getRender().isFlipped());

				rightArm.setStateManager(new SingleStateManager(rightArm, State.ATTACK));

				follower.getContainer().addEntity(rightArm);
			}
			
			@Override
			public String getString() {
				if(getPrevState() == State.ATTACK) {
					String[] prevAnimation = currentAnimation.split("-");
					int currentPhase = Integer.valueOf(prevAnimation[1]);
					
					if(follower.getRender().hasAnimation(prevAnimation[0] + "-" + (currentPhase + 1))) {
						return prevAnimation[0] + "-" + (currentPhase + 1);
					} else {
						return prevAnimation[0] + "-0";
					}
				} else {
					// TODO
					return null;
					//return follower.getEquipment().getEquippedWeapon().getAnimation() + "-0";
				}				
			}
		};
		
		return ae;
	}

	private ActionEvent defend(ActionEvent action) {
		ActionEvent ae = new ActionEvent(action.getState(), action.getPrevState()) {
			@Override
			public void act() {
				System.out.println(getPrevState());
				if(getPrevState() == State.DEFEND) {
					follower.fireEvent(new StateChangeEvent(State.DEFEND));
					follower.getRender().setFlipped(leader.getRender().isFlipped());
					follower.setFixDirection(true);
				} else {
					follower.fireEvent(new StateChangeEvent(State.IDLE));
					follower.setFixDirection(false);
				}
			}
		};
		
		return ae;
	}

	public Entity getFollower() {
		return follower;
	}
	
	public Entity getLeader() {
		return leader;
	}
	
	public void setLeader(Entity leader) {
		this.leader = leader;
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

	public void removeActionEventListener(ActionEventListener l) {
		if(l == null) {
			return;
		}
		actionEventListener = null;
	}

	public void addActionEventListener(ActionEventListener l) {
		this.actionEventListener = l;
	}
	
	public void fireEvent(ActionEvent e) {
		if(e instanceof ActionEvent) {
			processActionEvent((ActionEvent) e);
		}
	}
	
	protected void processActionEvent(ActionEvent e) {
		if(actionEventListener != null) {
			actionEventListener.actionPerformed(e);
		}
	}

}
