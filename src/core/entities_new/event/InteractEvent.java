package core.entities_new.event;

import core.entities_new.Entity;

public class InteractEvent extends EntityEvent {

	public static final int AUTORUN = 1;
	public static final int ON_TOUCH = 2;
	public static final int ON_ACTIVATE = 3;
	
	private int interactType;
	private Entity interactor;
	
	public InteractEvent(int interactType, Entity interactor) {
		this.interactType = interactType;
		this.interactor = interactor;
	}
	
	public int getInteractType() {
		return interactType;
	}
	
	public void setInteractType(int interactType) {
		this.interactType = interactType;
	}

	public Entity getInteractor() {
		return interactor;
	}
	
}
