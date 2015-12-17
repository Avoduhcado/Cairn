package core.entities_new.event;

import core.entities_new.CharacterState;

public class StateChangeEvent extends EntityEvent {

	private CharacterState oldState;
	private CharacterState newState;
	
	public StateChangeEvent(CharacterState newState, CharacterState oldState) {
		this.setNewState(newState);
		this.setOldState(oldState);
	}

	public CharacterState getOldState() {
		return oldState;
	}

	public void setOldState(CharacterState oldState) {
		this.oldState = oldState;
	}

	public CharacterState getNewState() {
		return newState;
	}

	public void setNewState(CharacterState newState) {
		this.newState = newState;
	}
	
}
