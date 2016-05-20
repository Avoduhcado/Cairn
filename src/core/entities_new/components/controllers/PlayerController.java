package core.entities_new.components.controllers;

import org.jbox2d.common.Vec2;

import core.entities_new.Entity;
import core.entities_new.components.Inventory;
import core.entities_new.event.InteractEvent;
import core.entities_new.event.controllers.AttackEvent;
import core.entities_new.event.controllers.CollapseEvent;
import core.entities_new.event.controllers.DefendEvent;
import core.entities_new.event.controllers.DodgeEvent;
import core.entities_new.event.controllers.JumpEvent;
import core.entities_new.event.controllers.MoveEvent;
import core.scene.ShadowMap;
import core.utilities.keyboard.Keybind;

public class PlayerController extends EntityController {
					
	private boolean defending = false;
	
	public PlayerController(Entity entity) {
		super(entity);
	}

	@Override
	protected void controlActions() {
		if(Keybind.DEFEND.press()) {
			setEventQueue(new DefendEvent(true));
		} else if(Keybind.DEFEND.released()) {
			setEventQueue(new DefendEvent(false));
		}
		
		if(Keybind.DODGE.clicked()) {
			setEventQueue(new DodgeEvent());
		} else if(Keybind.LIGHT_ATTACK.clicked()) {
			setEventQueue(new AttackEvent(((Inventory) entity.getComponent(Inventory.class)).getEquipment().getWeapons().get(0)));
			//deduceAttackPattern();
		} else if(Keybind.HEAVY_ATTACK.clicked()) {
			setEventQueue(new AttackEvent(((Inventory) entity.getComponent(Inventory.class)).getEquipment().getWeapons().get(1)));
		} else if(Keybind.SLOT1.clicked()) {
			setEventQueue(new CollapseEvent(entity.getBody().getLinearVelocity().clone()));
		} else if(Keybind.SLOT2.clicked()) {
			setEventQueue(new JumpEvent(new Vec2(0, -6f)));
		} else if(Keybind.CONFIRM.clicked()) {
			if(!entity.getZBody().getInteractables().isEmpty()) {
				entity.getZBody().getInteractables().get(0).fireEvent(new InteractEvent(InteractEvent.ON_ACTIVATE, entity));
			}
			//entity.getZBody().getInteractables().stream()
				//.forEach(e -> e.fireEvent(new InteractEvent(InteractEvent.ON_ACTIVATE, entity)));
		}
		
		// XXX Remove this later
		if(Keybind.SLOT9.clicked()) {
			ShadowMap.get().getLightSource(this.followers.get(0).getFollower()).setResize(1.5f);;
		}
	}

	private void deduceAttackPattern() {
		if(Keybind.LIGHT_ATTACK.clicked() && !Keybind.LIGHT_ATTACK.held()) {
			if(defending) {
				setEventQueue(new AttackEvent(((Inventory) entity.getComponent(Inventory.class)).getEquipment().getWeapons().get(2)));
			} else {
				setEventQueue(new AttackEvent(((Inventory) entity.getComponent(Inventory.class)).getEquipment().getWeapons().get(0)));
			}
		} else if(Keybind.LIGHT_ATTACK.clicked() && Keybind.LIGHT_ATTACK.held()) {
			setEventQueue(new AttackEvent(((Inventory) entity.getComponent(Inventory.class)).getEquipment().getWeapons().get(1)));
		}
	}

	@Override
	protected void controlMovement() {
		speedMod = 1f;
		if(Keybind.RUN.held() && !defending) {
			speedMod = 1.5f;
		}
		
		if(Keybind.movement()) {
			move(new MoveEvent(new Vec2(Keybind.RIGHT.press() ? 1f : Keybind.LEFT.press() ? -1f : 0f,
						Keybind.UP.press() ? -0.65f : Keybind.DOWN.press() ? 0.65f : 0f)));
		}
	}
	
	/*@Override
	public void defend(DefendEvent e) {
		entity.setFixDirection(e.isDefending());
		defending = e.isDefending();
	}*/
	
}
