package core.entities_new;

import java.awt.Point;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import core.utilities.keyboard.Keybinds;

public class FollowController implements Controller {

	private Entity leader;
	private Entity follower;
	
	private float lagDistance = 50;
	private float xOffset, yOffset;
	
	private float speed = 20f;
	private float speedMod = 1f;
	
	public FollowController(Entity follower, Entity leader) {
		this.follower = follower;
		this.leader = leader;
	}
	
	@Override
	public void collectInput() {
		speedMod = 1f;
		Body leadBody = leader.getBody();
		Body followBody = follower.getBody();
		double distance = Point.distance((leadBody.getPosition().x * 30f) + xOffset, (leadBody.getPosition().y * 30f) + yOffset,
				followBody.getPosition().x * 30f, followBody.getPosition().y * 30f);
		
		if(distance > lagDistance) {
			if(distance > lagDistance * 1.5f) {
				speedMod = 1.5f;
			}
			move(new Vec2((((leadBody.getPosition().x * 30f) + xOffset) - followBody.getPosition().x * 30f) / (float) distance,
					(((leadBody.getPosition().y * 30f) + yOffset) - followBody.getPosition().y * 30f) / (float) distance));
		} else if(leadBody.getLinearVelocity().length() <= 0.25f && distance > lagDistance / 4f) {
			speedMod = 0.35f;
			move(new Vec2((((leadBody.getPosition().x * 30f) + xOffset) - followBody.getPosition().x * 30f) / (float) distance,
					(((leadBody.getPosition().y * 30f) + yOffset) - followBody.getPosition().y * 30f) / (float) distance));
		}
		
		if(leadBody.getLinearVelocity().length() == 0f && (leader.getRender().isFlipped() != follower.getRender().isFlipped())) {
			move(new Vec2((((leadBody.getPosition().x * 30f) + xOffset) - followBody.getPosition().x * 30f) / (float) distance,
					(((leadBody.getPosition().y * 30f) + yOffset) - followBody.getPosition().y * 30f) / (float) distance));
			follower.getRender().setFlipped(leader.getRender().isFlipped());
		}
		
		if(!follower.getState().isActing()) {
			if(Keybinds.DODGE.clicked()) {
				follower.changeState(CharacterState.QUICKSTEP);
			}
			
			if(Keybinds.ATTACK.clicked()) {
				follower.changeState(CharacterState.ATTACK);
			}
			
			if(Keybinds.DEFEND.clicked()) {
				follower.changeState(CharacterState.DEFEND);
			}
		}
	}

	@Override
	public void resolveState() {
		switch(follower.getState()) {
		case IDLE:
			break;
		case WALK:
		case RUN:
			if(follower.getBody().getLinearVelocity().length() <= 0.25f) {
				follower.changeState(CharacterState.IDLE);
			}
			break;
		case QUICKSTEP:
			break;
		default:
			break;
		}
	}

	@Override
	public void move(Vec2 direction) {
		if(!follower.getBody().isFixedRotation()) {
			follower.getBody().applyTorque(90 * (direction.x < 0 ? -1f : 1f));
		}
		follower.getBody().applyForceToCenter(direction.mul(speed * speedMod));
		follower.changeState(speedMod > 1 ? CharacterState.RUN : CharacterState.WALK);
	}

	@Override
	public void dodge() {
		// TODO Auto-generated method stub

	}

	@Override
	public void collapse(Vec2 force) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attack() {
		// TODO Auto-generated method stub

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
