package core.entities_new.components.states;

import core.entities_new.Entity;
import core.entities_new.State;
import core.entities_new.event.StateChangeEvent;

public class PlainStateManager implements StateManager {

	private Entity entity;
	private State state;
	
	public PlainStateManager(Entity entity, State state) {
		this.entity = entity;
		this.state = state;
		
		changeStateForced(state);
	}
	
	@Override
	public void resolveState() {
		switch(getState()) {
		case WALK:
		case RUN:
			if(entity.getBody().getLinearVelocity().length() <= 0.25f) {
				changeState(State.IDLE);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public State getState() {
		return state;
	}

	@Override
	public void changeStateForced(State state) {
		if(entity.render()) {
			entity.getRender().fireEvent(new StateChangeEvent(state));
		}
		
		this.state = state;
	}

	@Override
	public void changeState(State state) {
		if(this.state != state) {
			changeStateForced(state);
		}
	}

}
