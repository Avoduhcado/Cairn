package core.entities_new.event;

import core.entities_new.State;

public class StateChangeEvent extends EntityEvent {

	private State oldState;
	private State newState;
	private boolean forced;
	
	public StateChangeEvent(State newState, State oldState) {
		this.setNewState(newState);
		this.setOldState(oldState);
	}

	public StateChangeEvent(State newState) {
		this.setNewState(newState);
		this.setOldState(null);
	}

	public StateChangeEvent(State hit, boolean forced) {
		this.setNewState(newState);
		this.setOldState(null);
		this.setForced(forced);
	}

	public State getOldState() {
		return oldState;
	}

	public void setOldState(State oldState) {
		this.oldState = oldState;
	}

	public State getNewState() {
		return newState;
	}

	public void setNewState(State newState) {
		this.newState = newState;
	}

	public boolean isForced() {
		return forced;
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}
	
}
