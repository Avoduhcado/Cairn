package core.entities_new.components;

import core.entities_new.Entity;
import core.entities_new.State;
import core.entities_new.event.ControllerEvent;
import core.entities_new.event.StateChangeEvent;

public class OneOffController extends EntityController {

	public OneOffController(Entity entity, ControllerEvent oneEvent) {
		super(entity);
		fireEvent(oneEvent);
		setEventQueue(new ControllerEvent(ControllerEvent.REMOVE));
	}

	@Override
	protected void controlMovement() {
	}

	@Override
	protected void controlActions() {
	}
	
	@Override
	public void attack(ControllerEvent e) {
		State.ATTACK.setCustomAnimation((String) e.getData());
		entity.fireEvent(new StateChangeEvent(State.ATTACK));
	}
	
	@Override
	protected void processEventQueue() {
		if(eventQueue != null && !entity.getState().isActing()) {
			fireEvent(eventQueue);
			
			// Notify any followers of your actions, oh Lord
			followers.stream().forEach(e -> e.setEventQueue(eventQueue));
			
			eventQueue = null;
		}
	}

}
