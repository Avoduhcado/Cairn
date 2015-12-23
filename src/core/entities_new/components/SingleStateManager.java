package core.entities_new.components;

import core.entities_new.Entity;
import core.entities_new.State;
import core.entities_new.event.StateChangeEvent;

public class SingleStateManager implements StateManager {

	private Entity entity;
	private State state;
	
	public SingleStateManager(Entity entity, State state) {
		this.entity = entity;
		this.state = state;
		
		this.entity.getRender().fireEvent(new StateChangeEvent(state, null));
	}
	
	@Override
	public void resolveState() {
		
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void changeStateForced(State state) {
		entity.destroy();
	}

	@Override
	public void changeState(State state) {
		changeStateForced(state);
	}

}
