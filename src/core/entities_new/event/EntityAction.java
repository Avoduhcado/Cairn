package core.entities_new.event;

import core.entities_new.CharacterState;

public abstract class EntityAction implements ActionEvent {

	private CharacterState prevState;
	private CharacterState type;
	
	public EntityAction(CharacterState state, CharacterState prevState) {
		this.setType(state);
		this.setPrevState(prevState);
	}

	@Override
	public abstract void act();

	public CharacterState getPrevState() {
		return prevState;
	}

	private void setPrevState(CharacterState prevState) {
		this.prevState = prevState;
	}

	public CharacterState getType() {
		return type;
	}

	public void setType(CharacterState type) {
		this.type = type;
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
