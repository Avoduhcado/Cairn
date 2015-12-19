package core.entities_new.event;

import core.entities_new.State;

public class ActionEvent extends EntityEvent {
	
	private State prevState;
	private State state;
	
	public ActionEvent(State state, State prevState) {
		this.setType(state);
		this.setPrevState(prevState);
	}

	public void act() {
		
	}

	public State getPrevState() {
		return prevState;
	}

	private void setPrevState(State prevState) {
		this.prevState = prevState;
	}

	public State getState() {
		return state;
	}

	public void setType(State type) {
		this.state = type;
	}
	
	public int getInt() {
		return 0;
	}
	
	public String getString() {
		return null;
	}
	
	public float getFloat() {
		return 0f;
	}
	
	public boolean getBoolean() {
		return false;
	}

}
