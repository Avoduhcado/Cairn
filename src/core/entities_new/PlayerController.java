package core.entities_new;

import org.jbox2d.common.Vec2;

import core.utilities.keyboard.Keybinds;

public class PlayerController implements Controller {

	private Entity player;
	private float speed = 30f;
	private float speedMod = 1f;
	
	public PlayerController(Entity player) {
		this.player = player;
	}

	@Override
	public void collectInput() {
		if(Keybinds.RUN.held()) {
			speedMod = 1.5f;
		} else if(Keybinds.RUN.released()) {
			speedMod = 1f;
		}
		
		if(Keybinds.RIGHT.press()) {
			move(new Vec2(1f, 0f));
		}
		if(Keybinds.LEFT.press()) {
			move(new Vec2(-1f, 0f));
		}
		
		if(Keybinds.DOWN.press()) {
			move(new Vec2(0f, 0.6f));
		}
		if(Keybinds.UP.press()) {
			move(new Vec2(0f, -0.6f));
		}
		
		if(Keybinds.DODGE.clicked()) {
			dodge();
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
		default:
			break;
		}
	}

	@Override
	public void move(Vec2 direction) {
		if(player.getState().canMove()) {
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
	public void fall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void collapse(Vec2 force) {
		// TODO Auto-generated method stub
		
	}
	
}
