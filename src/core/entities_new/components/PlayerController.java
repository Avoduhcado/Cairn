package core.entities_new.components;

import org.jbox2d.common.Vec2;
import core.entities_new.Entity;
import core.entities_new.event.ControllerEvent;
import core.entities_new.event.InteractEvent;
import core.utilities.keyboard.Keybind;

public class PlayerController extends EntityController {
					
	public PlayerController(Entity entity) {
		super(entity);
	}

	@Override
	protected void controlActions() {
		if(Keybind.DODGE.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.DODGE));
		} else if(Keybind.ATTACK.clicked()) {
			setEventQueue(new ControllerEvent(ControllerEvent.ATTACK) {{
				setData(((Inventory) entity.getComponent(Inventory.class)).getEquipment().getEquippedWeapon().getAnimation());
			}});
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
		
		if(Keybind.DEFEND.press()) {
			setEventQueue(new ControllerEvent(ControllerEvent.DEFEND) {{
				setData(true);
			}});
		} else if(Keybind.DEFEND.released()) {
			setEventQueue(new ControllerEvent(ControllerEvent.DEFEND) {{
				setData(false);
			}});
		}
	}

	@Override
	protected void controlMovement() {
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
	
}
