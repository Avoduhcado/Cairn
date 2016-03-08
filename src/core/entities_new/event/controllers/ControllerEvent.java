package core.entities_new.event.controllers;

import core.entities_new.event.EntityEvent;

public abstract class ControllerEvent extends EntityEvent {

	public static final int MOVE = 1;
	public static final int DODGE = 2;
	public static final int ATTACK = 3;
	public static final int DEFEND = 4;
	public static final int COLLAPSE = 5;
	public static final int JUMP = 6;
	public static final int CHANGE_WEAPON = 7;
	public static final int REMOVE = 8;
	
	private final int type;
	
	public ControllerEvent(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

}
