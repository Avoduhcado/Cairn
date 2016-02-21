package core.entities_new.utils;

import core.entities_new.Entity;
import core.entities_new.State;
import core.entities_new.event.ControllerEvent;

public class ControllerValidator {

	public static ControllerEvent validateAttack(ControllerEvent event, Entity entity) {
		int comboStep = 0;
		if(entity.getState().equals(State.ATTACK)) {
			if(entity.render() && entity.getRender().getAnimation().startsWith(((String) event.getData()).split("-")[0])) {
				String[] animation = entity.getRender().getAnimation().split("-");
				comboStep = Integer.parseInt(animation[1]) + 1;
				if(!entity.getRender().hasAnimation(animation[0] + "-" + comboStep)) {
					comboStep = 0;
				}
			}
		}
		event.setData(((String) event.getData()).split("-")[0] + "-" + comboStep);
		
		return event;
	}
	
}
