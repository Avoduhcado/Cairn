package core.entities_new.utils;

import core.entities_new.Entity;
import core.entities_new.State;
import core.entities_new.event.controllers.AttackEvent;
import core.entities_new.event.controllers.ControllerEvent;
import core.inventory.Weapon;

public class ControllerValidator {

	public static ControllerEvent validateAttack(AttackEvent event, Entity entity) {
		int comboStep = 0;
		Weapon weapon = event.getWeapon();
		if(entity.getState().equals(State.ATTACK)) {
			if(entity.render() && entity.getRender().getAnimation().startsWith(weapon.getAnimation())) {
				comboStep = Integer.parseInt(entity.getRender().getAnimation().split("-")[1]);
				comboStep++;
				if(!entity.getRender().hasAnimation(weapon.getAnimation() + "-" + comboStep)) {
					comboStep = 0;
				}
			}
		}
		event.setAnimation(weapon.getAnimation() + "-" + comboStep);
		
		return event;
	}
	
}
