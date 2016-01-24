package core.entities_new.components;

import org.jbox2d.common.Vec2;
import core.entities_new.State;
import core.entities_new.Entity;
import core.entities_new.event.ActionEvent;
import core.entities_new.event.ControllerEvent;
import core.entities_new.event.InteractEvent;
import core.entities_new.event.StateChangeEvent;
import core.utilities.keyboard.Keybind;

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
		if(Keybind.DODGE.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.DODGE));
		} else if(Keybind.ATTACK.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.ATTACK));
		} else if(Keybind.DEFEND.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.DEFEND));
		} else if(Keybind.SLOT1.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.COLLAPSE) {{
				setData(entity.getBody().getLinearVelocity().clone());
			}});
		} else if(Keybind.SLOT2.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.JUMP) {{
				setData(new Vec2(0, -6f));
			}});
		} else if(Keybind.CONFIRM.clicked()) {
			if(!entity.getZBody().getInteractables().isEmpty()) {
				entity.getZBody().getInteractables().get(0).fireEvent(new InteractEvent(InteractEvent.ON_ACTIVATE, entity));
			}
			//entity.getZBody().getInteractables().stream()
				//.forEach(e -> e.fireEvent(new InteractEvent(InteractEvent.ON_ACTIVATE, entity)));
		}
	}

	private void controlMovement() {
		speedMod = 1f;
		if(Keybind.RUN.held()) {
			speedMod = 1.5f;
		}
		
		if(Keybind.movement()) {
			move(new ControllerEvent(ControllerEvent.MOVE) {{
				setData(new Vec2(Keybind.RIGHT.press() ? 1f : Keybind.LEFT.press() ? -1f : 0f,
						Keybind.UP.press() ? -0.65f : Keybind.DOWN.press() ? 0.65f : 0f));
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
