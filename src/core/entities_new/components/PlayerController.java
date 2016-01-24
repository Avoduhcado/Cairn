package core.entities_new.components;

import org.jbox2d.common.Vec2;
import core.entities_new.State;
import core.entities_new.Entity;
import core.entities_new.event.ActionEvent;
import core.entities_new.event.ControllerEvent;
import core.entities_new.event.InteractEvent;
import core.entities_new.event.StateChangeEvent;
import core.utilities.keyboard.Keybinds;

public class PlayerController extends EntityController {
					
	public PlayerController(Entity entity) {
		super(entity);
	}

	@Override
	public void control() {
		controlMovement();
		
		controlActions();
		
		processEventQueue();
	}

	private void controlActions() {
		if(Keybinds.DODGE.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.DODGE));
		} else if(Keybinds.ATTACK.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.ATTACK));
		} else if(Keybinds.DEFEND.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.DEFEND));
		} else if(Keybinds.SLOT1.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.COLLAPSE) {{
				setData(entity.getBody().getLinearVelocity().clone());
			}});
		} else if(Keybinds.SLOT2.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.JUMP) {{
				setData(new Vec2(0, -6f));
			}});
		} else if(Keybinds.CONFIRM.clicked()) {
			if(!entity.getZBody().getInteractables().isEmpty()) {
				entity.getZBody().getInteractables().get(0).fireEvent(new InteractEvent(InteractEvent.ON_ACTIVATE, entity));
			}
			//entity.getZBody().getInteractables().stream()
				//.forEach(e -> e.fireEvent(new InteractEvent(InteractEvent.ON_ACTIVATE, entity)));
		}
	}

	private void controlMovement() {
		speedMod = 1f;
		if(Keybinds.RUN.held()) {
			speedMod = 1.5f;
		}
		
		if(Keybinds.movement()) {
			move(new ControllerEvent(ControllerEvent.MOVE) {{
				setData(new Vec2(Keybinds.RIGHT.press() ? 1f : Keybinds.LEFT.press() ? -1f : 0f,
							Keybinds.UP.press() ? -0.65f : Keybinds.DOWN.press() ? 0.65f : 0f));
			}});
		}
	}
	
	@Override
	public void move(ControllerEvent e) {
		if(entity.getState().canMove()) {
			Vec2 movement = (Vec2) e.getData();
			movement.normalize();
			if(entity.isWalkingBackwards()) {
				speedMod -= 0.25f;
			}
			entity.getBody().applyForceToCenter(movement.mul(speed * speedMod));
			entity.fireEvent(new StateChangeEvent(speedMod > 1 ? State.RUN : State.WALK));
			
			if(entity.getBody().getLinearVelocity().x != 0 && !entity.isFixDirection() && entity.getRender() != null) {
				entity.getRender().setFlipped(entity.getBody().getLinearVelocity().x < 0);
			}
		}
	}

	public void attack() {
		entity.getContainer().getEntities().stream()
			.filter(e -> e.getController() instanceof FollowController 
					&& ((FollowController) e.getController()).getLeader() == entity)
			.map(e -> (FollowController) e.getController())
			.forEach(e -> e.fireEvent(new ActionEvent(State.ATTACK, e.getFollower().getState())));
	}
	
	public void defend(boolean release) {
		entity.getContainer().getEntities().stream()
			.filter(e -> e.getController() instanceof FollowController 
					&& ((FollowController) e.getController()).getLeader() == entity)
			.map(e -> (FollowController) e.getController())
			.forEach(e -> e.fireEvent(new ActionEvent(State.DEFEND, release ? State.IDLE : State.DEFEND)));
	}
	
}
