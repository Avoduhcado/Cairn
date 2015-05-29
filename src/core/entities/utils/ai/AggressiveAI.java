package core.entities.utils.ai;

import core.Theater;
import core.entities.Actor;
import core.entities.Enemy;
import core.entities.Entity;
import core.entities.interfaces.Combatant;
import core.setups.Stage;

public class AggressiveAI extends Intelligence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AggressiveAI() {
		super();
	}
	
	public void update() {
		super.update();
		
		switch(state) {
		case IDLE:
			searchForTarget();
			break;
		case PROVOKED:
			if(((Actor) host).getState().canAct()) {
				// TODO Devise timing for attacks and dodging OPPORTUNIST TRAIT!!!
				if(((Enemy) host).canReach((Entity) target)) {
					((Enemy) host).lookAt((Entity) target);
					((Enemy) host).attack();
				} else {
					if(sight.intersects(((Entity) target).getBox())) {
						alert(target);
						host.approach(((Actor) target).getPositionAsPoint());
					} else {
						host.approach(((Actor) target).getPositionAsPoint());
						chase();
					}
				}
			}
			break;
		case ENRAGED:
			break;
		}
	}
	
	@Override
	public void searchForTarget() {
		for(Actor a : ((Stage) Theater.get().getSetup()).getCast()) {
			if(a instanceof Combatant && host != a && ((Combatant) host).getReputation().isEnemy(((Combatant) a).getReputation())
					&& getSight().intersects(a.getBox())) {
				alert((Combatant) a);
				break;
			}
		}
	}

}
