package core.entities_new.components.states;

import core.entities_new.State;

public interface StateManager {

	public void resolveState();

	public State getState();
	public void changeStateForced(State state);
	public void changeState(State state);
	
}
