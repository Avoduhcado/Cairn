package core.entities_new;

import org.jbox2d.common.Vec2;

import core.utilities.keyboard.Keybinds;

public class PlayerController implements Controller {

	private Entity player;
	
	private float speed = 30f;
	private float speedMod = 1f;
	private Vec2 movement = new Vec2();
	
	public PlayerController(Entity player) {
		this.player = player;
	}

	@Override
	public void collectInput() {
		speedMod = 1f;
		if(Keybinds.RUN.held()) {
			speedMod = 1.5f;
		}
		
		movement.set(Keybinds.RIGHT.press() ? 1f : Keybinds.LEFT.press() ? -1f : 0f,
				Keybinds.UP.press() ? -0.6f : Keybinds.DOWN.press() ? 0.6f : 0f);
		
		if(movement.length() > 0) {
			move(movement);
			movement.setZero();
		}
		
		if(Keybinds.DODGE.clicked()) {
			dodge();
		}
		
		if(Keybinds.ATTACK.clicked()) {
			attack();
		}
	}

	@Override
	public void resolveState() {
		switch(player.getState()) {
		case IDLE:
			break;
		case WALK:
		case RUN:
			if(player.getBody().getLinearVelocity().length() <= 0.25f) {
				player.changeState(CharacterState.IDLE);
			}
			break;
		case QUICKSTEP:
			break;
		case ATTACK:
			//player.getContainer().removeEntity(player.getSubEntities().remove(0));
			break;
		default:
			break;
		}
	}

	@Override
	public void move(Vec2 direction) {
		if(player.getState().canMove()) {
			direction.normalize();
			player.getBody().applyForceToCenter(direction.mul(speed * speedMod));
			player.changeState(speedMod > 1 ? CharacterState.RUN : CharacterState.WALK);
		}
	}
	
	@Override
	public void dodge() {
		if(player.getBody().getLinearVelocity().length() == 0) {
			player.getBody().applyLinearImpulse(new Vec2(player.getRender().isFlipped() ? 10f : -10f, 0f),
					player.getBody().getWorldCenter());
		} else {
			player.getBody().getLinearVelocity().normalize();
			player.getBody().applyLinearImpulse(player.getBody().getLinearVelocity().mul(10f * speedMod),
					player.getBody().getWorldCenter());
		}
		
		player.changeState(CharacterState.QUICKSTEP);
		player.setFixDirection(true);
	}

	@Override
	public void collapse(Vec2 force) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void attack() {
		player.changeState(CharacterState.ATTACK);
		Entity attack = new Entity("Right Arm", player.getBody().getPosition().x * 30f,
				player.getBody().getPosition().y * 30f, player.getContainer());
		attack.changeState(CharacterState.ATTACK);
		attack.getRender().setFlipped(player.getRender().isFlipped());
		
		player.getContainer().addEntity(attack);
	}
	
}
