package core.entities_new.components.states;

import core.entities_new.State;

public interface Statable {

	public State getState();
	
	public void resolveState();
	
}
