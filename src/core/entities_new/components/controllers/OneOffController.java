package core.entities_new.components.controllers;

import core.entities_new.Entity;
import core.entities_new.State;
import core.entities_new.event.StateChangeEvent;
import core.entities_new.event.controllers.AttackEvent;
import core.entities_new.event.controllers.ControllerEvent;
import core.entities_new.event.controllers.DefendEvent;
import core.entities_new.event.controllers.RemoveEvent;

public class OneOffController extends EntityController {

	public OneOffController(Entity entity, ControllerEvent oneEvent) {
		super(entity);
		fireEvent(oneEvent);
		setEventQueue(new RemoveEvent());
	}
	
	@Override
	public void attack(AttackEvent e) {
		State.ATTACK.setCustomAnimation(e.getAnimation());
		entity.fireEvent(new StateChangeEvent(State.ATTACK));
	}
	
	@Override
	public void defend(DefendEvent e) {
		if(e.isDefending()) {
			entity.fireEvent(new StateChangeEvent(State.DEFEND));
		} else {
			entity.fireEvent(new RemoveEvent());
		}
		entity.setFixDirection(e.isDefending());
		defending = e.isDefending();
	}
	
	@Override
	public void processEventQueue() {
		if(eventQueue != null && !entity.getState().isActing() && !defending) {
			fireEvent(eventQueue);
			
			// Notify any followers of your actions, oh Lord
			followers.stream().forEach(e -> e.setEventQueue(eventQueue));
			
			eventQueue = null;
		}
	}

	@Override
	protected void controlMovement() {
	}

	@Override
	protected void controlActions() {
	}

}
