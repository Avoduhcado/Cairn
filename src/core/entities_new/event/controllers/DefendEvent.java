package core.entities_new.event.controllers;

public class DefendEvent extends ControllerEvent {
	
	private final boolean defending;
	
	public DefendEvent(boolean defending) {
		super(DEFEND);
		
		this.defending = defending;
	}

	public boolean isDefending() {
		return defending;
	}
	
}